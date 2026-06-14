-- ============================================================
--  RETOÑO · Actualizar imágenes de las prendas de muestra
--  1) Sube las fotos a Storage -> bucket "garments".
--  2) Copia la URL pública de cada una (Storage -> archivo -> Copy URL).
--  3) Pega la URL en el UPDATE correspondiente y ejecuta en SQL Editor.
--
--  Formato de URL pública:
--  https://hmswqjrkreytcvftbxtb.supabase.co/storage/v1/object/public/garments/NOMBRE
-- ============================================================

update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Overol de mezclilla';
update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Vestido de florecitas';
update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Sudadera con capucha';
update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Playera de dinosaurios';
update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Pantalón de pana';
update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Tenis con luces';
update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Chamarra impermeable';
update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Mameluco de algodón';
update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Falda tutú';
update public.garments set image_url = 'PEGA_URL_AQUI' where title = 'Set gorro y bufanda';

-- Verifica:
-- select title, image_url from public.garments order by created_at;
