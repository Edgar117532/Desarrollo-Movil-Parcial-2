CREATE TABLE IF NOT EXISTS notas (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    titulo VARCHAR(200) NOT NULL,
    contenido TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_notas_usuario_id ON notas(usuario_id);
-- Ajusta 'usuarios(id)' al nombre real de la tabla de usuarios de tu backend.
