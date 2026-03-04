-- =====================================================
-- Finance Manager - Database Script
-- =====================================================

DROP DATABASE IF EXISTS bd_fm;

CREATE DATABASE bd_fm
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE bd_fm;

-- =========================
-- Tabla usuarios
-- =========================

CREATE TABLE usuarios (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  pass_hash VARCHAR(64) NOT NULL,
  fecha_creacion DATE NOT NULL
) ENGINE=InnoDB;

-- =========================
-- Tabla movimientos
-- =========================

CREATE TABLE movimientos (
  id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  tipo ENUM('Ingreso','Egreso') NOT NULL,
  monto DECIMAL(10,2) NOT NULL,
  fecha DATE NOT NULL,
  comentarios TEXT,
  
  CONSTRAINT fk_movimientos_usuario
    FOREIGN KEY (id_usuario)
    REFERENCES usuarios(id_usuario)
    ON DELETE CASCADE
) ENGINE=InnoDB;