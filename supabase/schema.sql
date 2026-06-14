-- ============================================================
--  RETOÑO · Esquema de base de datos (MVP)
--  Renta de ropa circular · Cliente B Android
--  Ejecutar en: Supabase Dashboard -> SQL Editor -> New query -> Run
--  Seguro de re-ejecutar (idempotente donde se puede).
-- ============================================================

-- Extensión para gen_random_uuid()
create extension if not exists pgcrypto;

-- ------------------------------------------------------------
-- 1. ENUMS
-- ------------------------------------------------------------
-- Estado de la prenda (máquina de estados del "Ciclo de Armario")
do $$ begin
  create type garment_status as enum
    ('available', 'reserved', 'rented', 'in_cleaning', 'retired');
exception when duplicate_object then null; end $$;

-- Categoría de la prenda (para filtros del catálogo)
do $$ begin
  create type garment_category as enum
    ('tops', 'bottoms', 'dresses', 'outerwear', 'shoes', 'accessories');
exception when duplicate_object then null; end $$;

-- ------------------------------------------------------------
-- 2. PROFILES  (perfil de la app, espejo de auth.users)
-- ------------------------------------------------------------
create table if not exists public.profiles (
  id           uuid primary key references auth.users(id) on delete cascade,
  display_name text,
  avatar_url   text,
  created_at   timestamptz not null default now()
);

-- Crear perfil automáticamente cuando alguien se registra
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer set search_path = public
as $$
begin
  insert into public.profiles (id, display_name)
  values (
    new.id,
    coalesce(new.raw_user_meta_data->>'display_name', split_part(new.email, '@', 1))
  )
  on conflict (id) do nothing;
  return new;
end; $$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();

-- ------------------------------------------------------------
-- 3. GARMENTS  (catálogo de prendas)
-- ------------------------------------------------------------
create table if not exists public.garments (
  id           uuid primary key default gen_random_uuid(),
  owner_id     uuid references public.profiles(id) on delete set null,
  title        text not null,
  description  text,
  brand        text,
  size         text,                       -- XS, S, M, L, XL, 28, 30...
  category     garment_category,
  color        text,
  condition    text,                       -- 'nuevo', 'como nuevo', 'buen estado'
  image_url    text,
  rental_price numeric(10,2) default 0,    -- precio de renta por semana
  status       garment_status not null default 'available',
  created_at   timestamptz not null default now()
);

create index if not exists idx_garments_status   on public.garments(status);
create index if not exists idx_garments_category on public.garments(category);

-- ------------------------------------------------------------
-- 4. GARMENT_STATES  (historial / máquina de estados)
--    Lo llena un trigger automáticamente; el módulo Detalle lo lee.
-- ------------------------------------------------------------
create table if not exists public.garment_states (
  id         uuid primary key default gen_random_uuid(),
  garment_id uuid not null references public.garments(id) on delete cascade,
  state      garment_status not null,
  note       text,
  changed_by uuid references public.profiles(id) on delete set null,
  changed_at timestamptz not null default now()
);

create index if not exists idx_garment_states_garment on public.garment_states(garment_id);

-- Registrar el estado inicial al crear la prenda
create or replace function public.log_garment_initial_state()
returns trigger language plpgsql as $$
begin
  insert into public.garment_states (garment_id, state, note)
  values (new.id, new.status, 'Prenda registrada en el catálogo');
  return new;
end; $$;

drop trigger if exists trg_garment_insert on public.garments;
create trigger trg_garment_insert
  after insert on public.garments
  for each row execute procedure public.log_garment_initial_state();

-- Registrar cada cambio de estado
create or replace function public.log_garment_state_change()
returns trigger language plpgsql as $$
begin
  if new.status is distinct from old.status then
    insert into public.garment_states (garment_id, state)
    values (new.id, new.status);
  end if;
  return new;
end; $$;

drop trigger if exists trg_garment_status_update on public.garments;
create trigger trg_garment_status_update
  after update of status on public.garments
  for each row execute procedure public.log_garment_state_change();

-- ------------------------------------------------------------
-- 5. CART_ITEMS  ("Ciclo de Armario" = carrito de renta)
-- ------------------------------------------------------------
create table if not exists public.cart_items (
  id         uuid primary key default gen_random_uuid(),
  user_id    uuid not null references public.profiles(id) on delete cascade,
  garment_id uuid not null references public.garments(id) on delete cascade,
  start_date date,
  end_date   date,
  added_at   timestamptz not null default now(),
  unique (user_id, garment_id),
  check (end_date is null or start_date is null or end_date >= start_date)
);

-- ------------------------------------------------------------
-- 6. WISHLIST  (lista de deseos)
-- ------------------------------------------------------------
create table if not exists public.wishlist (
  id         uuid primary key default gen_random_uuid(),
  user_id    uuid not null references public.profiles(id) on delete cascade,
  garment_id uuid not null references public.garments(id) on delete cascade,
  added_at   timestamptz not null default now(),
  unique (user_id, garment_id)
);

-- ------------------------------------------------------------
-- 7. SEGURIDAD (RLS)
--    Bloqueamos acceso directo. Solo el backend Node, que usa la
--    service_role key, puede leer/escribir (service_role salta RLS).
--    Sin políticas permisivas = la anon key no entra a la base.
-- ------------------------------------------------------------
alter table public.profiles       enable row level security;
alter table public.garments       enable row level security;
alter table public.garment_states enable row level security;
alter table public.cart_items     enable row level security;
alter table public.wishlist       enable row level security;

-- (No creamos políticas: todo acceso pasa por Node con service_role.)

-- ============================================================
--  FIN DEL ESQUEMA
-- ============================================================
