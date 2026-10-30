require('dotenv').config();
const express = require('express');
const cors = require('cors');
const pool = require('./db');
const app = express();
const PORT = Number(process.env.PORT || 3000);

if (!process.env.JWT_SECRET) {
  console.error('Falta JWT_SECRET en el archivo .env');
  process.exit(1);
}
app.use(cors());
app.use(express.json());
app.get('/', (_req, res) => res.json({ mensaje: 'API de NotasApp funcionando.' }));
app.get('/api/health', async (_req, res) => {
  try { await pool.query('SELECT 1'); res.json({ servidor: 'ok', baseDatos: 'conectada' }); }
  catch (err) { console.error(err); res.status(500).json({ servidor: 'ok', baseDatos: 'error' }); }
});
app.use('/api/auth', require('./routes/auth.routes'));
app.use('/api/notas', require('./routes/notas.routes'));
app.use((err, _req, res, _next) => {
  console.error(err);
  res.status(500).json({ error: 'Error interno del servidor.' });
});

async function iniciar() {
  try {
    await pool.query('SELECT 1');
    app.listen(PORT, '0.0.0.0', () => console.log(`Backend de NotasApp activo en http://localhost:${PORT}`));
  } catch (err) {
    console.error('No se pudo conectar con PostgreSQL. Revisa las variables del archivo .env.');
    console.error(err.message);
    process.exit(1);
  }
}
iniciar();
