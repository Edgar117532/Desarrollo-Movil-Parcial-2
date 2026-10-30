const express = require('express');
const pool = require('../db');
const verificarToken = require('../auth');
const router = express.Router();
router.use(verificarToken);

router.get('/', async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT id, titulo, contenido, fecha_creacion FROM notas WHERE usuario_id = $1 ORDER BY fecha_creacion DESC',
      [req.usuario.id]
    );
    return res.json(result.rows);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ error: 'No se pudieron consultar las notas.' });
  }
});

router.post('/', async (req, res) => {
  const { titulo, contenido } = req.body || {};
  if (!titulo || !contenido) return res.status(400).json({ error: 'titulo y contenido son obligatorios.' });
  try {
    const result = await pool.query(
      'INSERT INTO notas (usuario_id, titulo, contenido) VALUES ($1, $2, $3) RETURNING id, titulo, contenido, fecha_creacion',
      [req.usuario.id, String(titulo).trim(), String(contenido).trim()]
    );
    return res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ error: 'No se pudo guardar la nota.' });
  }
});
module.exports = router;
