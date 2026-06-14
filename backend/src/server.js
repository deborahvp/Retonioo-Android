// Punto de entrada del backend Retoño.
import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import { supabase } from './config/supabase.js';

import authRoutes from './routes/auth.routes.js';
import garmentsRoutes from './routes/garments.routes.js';
import cartRoutes from './routes/cart.routes.js';
import wishlistRoutes from './routes/wishlist.routes.js';
import uploadRoutes from './routes/upload.routes.js';

dotenv.config();

const app = express();
app.use(cors());
app.use(express.json());

// Ruta raíz
app.get('/', (req, res) => {
  res.json({ service: 'Retoño API', status: 'ok' });
});

// Healthcheck: prueba la conexión real a Supabase contando las prendas.
app.get('/health', async (req, res) => {
  try {
    const { count, error } = await supabase
      .from('garments')
      .select('*', { count: 'exact', head: true });
    if (error) return res.status(500).json({ ok: false, error });
    res.json({ ok: true, conexion: 'Supabase OK', prendas_en_catalogo: count });
  } catch (e) {
    res.status(500).json({ ok: false, thrown: String(e) });
  }
});

// Módulos de la API
app.use('/auth', authRoutes);
app.use('/garments', garmentsRoutes);
app.use('/cart', cartRoutes);
app.use('/wishlist', wishlistRoutes);
app.use('/upload', uploadRoutes);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`🌱 Retoño backend corriendo en http://localhost:${PORT}`);
  console.log(`   Healthcheck: http://localhost:${PORT}/health`);
});
