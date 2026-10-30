const express = require('express');
const router = express.Router();

/*
 * Integra este router con tu pool de PostgreSQL y middleware JWT existente.
 * Contrato esperado del middleware: req.user.id contiene el ID del usuario validado.
 * No confíes en un usuario_id enviado por el cliente: se toma del token.
 */
module.exports = function createNotasRouter(pool, verificarToken) {
  router.post('/', verificarToken, async (req, res) => {
    try {
      const { titulo, contenido } = req.body || {};
      if (typeof titulo !== 'string' || !titulo.trim() ||
          typeof contenido !== 'string' || !contenido.trim()) {
        return res.status(400).json({ message: 'El título y el contenido son obligatorios.' });
      }
      const usuarioId = req.user && (req.user.id ?? req.user.usuarioId);
      if (!usuarioId) return res.status(401).json({ message: 'Token inválido o usuario no identificado.' });

      const result = await pool.query(
        `INSERT INTO notas (usuario_id, titulo, contenido)
         VALUES ($1, $2, $3)
         RETURNING id, titulo, contenido, created_at`,
        [usuarioId, titulo.trim(), contenido.trim()]
      );
      return res.status(201).json(result.rows[0]);
    } catch (error) {
      console.error('Error creando nota:', error);
      return res.status(500).json({ message: 'Error interno al guardar la nota.' });
    }
  });

  router.get('/', verificarToken, async (req, res) => {
    try {
      const usuarioId = req.user && (req.user.id ?? req.user.usuarioId);
      if (!usuarioId) return res.status(401).json({ message: 'Token inválido o usuario no identificado.' });

      const result = await pool.query(
        `SELECT id, titulo, contenido, created_at
         FROM notas WHERE usuario_id = $1
         ORDER BY created_at DESC`,
        [usuarioId]
      );
      return res.json(result.rows);
    } catch (error) {
      console.error('Error consultando notas:', error);
      return res.status(500).json({ message: 'Error interno al consultar las notas.' });
    }
  });

  return router;
};
