const jwt = require('jsonwebtoken');

function verificarToken(req, res, next) {
  const authHeader = req.headers.authorization || '';
  const [tipo, token] = authHeader.split(' ');
  if (tipo !== 'Bearer' || !token) {
    return res.status(401).json({ error: 'Falta el token de autenticación.' });
  }
  try {
    req.usuario = jwt.verify(token, process.env.JWT_SECRET);
    next();
  } catch (_) {
    return res.status(401).json({ error: 'Token inválido o vencido.' });
  }
}
module.exports = verificarToken;
