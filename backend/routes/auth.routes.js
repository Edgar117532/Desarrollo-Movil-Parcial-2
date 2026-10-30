const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const pool = require('../db');
const router = express.Router();

router.post('/register', async (req, res) => {
  const { nombre, email, password } = req.body || {};
  if (!nombre || !email || !password) return res.status(400).json({ error: 'nombre, email y password son obligatorios.' });
  if (String(password).length < 6) return res.status(400).json({ error: 'La contraseña debe tener al menos 6 caracteres.' });
  try {
    const hash = await bcrypt.hash(String(password), 10);
    const result = await pool.query(
      'INSERT INTO usuarios (nombre, email, password_hash) VALUES ($1, LOWER($2), $3) RETURNING id, nombre, email',
      [String(nombre).trim(), String(email).trim(), hash]
    );
    return res.status(201).json({ mensaje: 'Usuario creado correctamente.', user: { id: result.rows[0].id, name: result.rows[0].nombre, email: result.rows[0].email } });
  } catch (err) {
    if (err.code === '23505') return res.status(409).json({ error: 'Ese correo ya está registrado.' });
    console.error(err);
    return res.status(500).json({ error: 'No se pudo registrar el usuario.' });
  }
});

router.post('/login', async (req, res) => {
  const { email, password } = req.body || {};
  if (!email || !password) return res.status(400).json({ error: 'email y password son obligatorios.' });
  try {
    const result = await pool.query('SELECT id, nombre, email, password_hash FROM usuarios WHERE email = LOWER($1)', [String(email).trim()]);
    if (!result.rows.length) return res.status(401).json({ error: 'Correo o contraseña incorrectos.' });
    const user = result.rows[0];
    const valida = await bcrypt.compare(String(password), user.password_hash);
    if (!valida) return res.status(401).json({ error: 'Correo o contraseña incorrectos.' });
    const token = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET, { expiresIn: '12h' });
    return res.json({ token, user: { id: user.id, name: user.nombre, email: user.email } });
  } catch (err) {
    console.error(err);
    return res.status(500).json({ error: 'No se pudo iniciar sesión.' });
  }
});
module.exports = router;
