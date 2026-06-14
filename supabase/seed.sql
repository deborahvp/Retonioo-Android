-- ============================================================
--  RETOÑO · Datos de ejemplo (seed) — ROPA INFANTIL
--  Plataforma de suscripción circular: las prendas que el niño
--  deja de usar (por talla) se reintroducen al catálogo si están
--  en buen estado. El usuario intercambia "batches" de ropa.
--  Ejecutar en SQL Editor DESPUÉS de schema.sql.
--  Imágenes: placeholders de picsum.photos (luego se reemplazan
--  por URLs reales del bucket de Storage).
-- ============================================================

insert into public.garments
  (title, description, brand, size, category, color, condition, image_url, rental_price, status)
values
  ('Overol de mezclilla',       'Overol ajustable, tirantes con broche.',          'OshKosh',        '2-3 años',     'bottoms',     'Azul',     'como nuevo',   'https://picsum.photos/seed/retonio1/600/800', 45.00, 'available'),
  ('Vestido de florecitas',     'Vestido de algodón con vuelo, manga corta.',      'Mayoral',        '3-4 años',     'dresses',     'Rosa',     'buen estado',  'https://picsum.photos/seed/retonio2/600/800', 50.00, 'available'),
  ('Sudadera con capucha',      'Sudadera afelpada, cierre frontal.',              'Zara Kids',      '4-5 años',     'outerwear',   'Mostaza',  'como nuevo',   'https://picsum.photos/seed/retonio3/600/800', 40.00, 'rented'),
  ('Playera de dinosaurios',    'Playera de algodón con estampado de dinos.',      'Carter''s',      '2-3 años',     'tops',        'Verde',    'buen estado',  'https://picsum.photos/seed/retonio4/600/800', 30.00, 'available'),
  ('Pantalón de pana',          'Pantalón de pana suave, cintura elástica.',       'H&M Kids',       '18-24 meses',  'bottoms',     'Café',     'como nuevo',   'https://picsum.photos/seed/retonio5/600/800', 35.00, 'available'),
  ('Tenis con luces',           'Tenis con suela que enciende al caminar.',        'Bubblegummers',  '22 (MX)',      'shoes',       'Multicolor','buen estado', 'https://picsum.photos/seed/retonio6/600/800', 55.00, 'in_cleaning'),
  ('Chamarra impermeable',      'Chamarra rompevientos con forro ligero.',         'Columbia Kids',  '5-6 años',     'outerwear',   'Rojo',     'nuevo',        'https://picsum.photos/seed/retonio7/600/800', 60.00, 'available'),
  ('Mameluco de algodón',       'Mameluco de manga larga con broches.',            'Carter''s',      '0-3 meses',    'tops',        'Beige',    'como nuevo',   'https://picsum.photos/seed/retonio8/600/800', 25.00, 'available'),
  ('Falda tutú',                'Falda con capas de tul y resorte.',               'Gap Kids',       '3-4 años',     'bottoms',     'Lila',     'como nuevo',   'https://picsum.photos/seed/retonio9/600/800', 35.00, 'reserved'),
  ('Set gorro y bufanda',       'Conjunto tejido para invierno.',                  'Mayoral',        'Única',        'accessories', 'Gris',     'buen estado',  'https://picsum.photos/seed/retonio10/600/800', 28.00, 'available');

-- Verificación rápida
-- select title, size, category, status from public.garments order by created_at;
