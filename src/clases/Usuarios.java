package clases;

import java.sql.*;

public class Usuarios {

    private String user;
    private String pass;
    private double saldoI;

    //obtener datos desde la interfaz de registrarse
    public Usuarios(String user, String pass, double saldoI) {
        this.user = user;
        this.pass = pass;
        this.saldoI = saldoI;
    }

    //obtener desde login
    public Usuarios(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    //validar si el usuario ya existe
    public boolean usuarioExiste() {

        if (user == null || user.isBlank()) {
            throw new IllegalArgumentException("Usuario inválido");
        }

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(
                "SELECT id_usuario FROM usuarios WHERE username = ?"
        )) {

            pst.setString(1, user);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar usuario", e);
        }

    }

    //guardar en base de datos - registrar user y saldo inicial
    public boolean registrar() {

        if (saldoI <= 0) {
            throw new IllegalArgumentException("Saldo inicial inválido");
        }

        if (user == null || user.isBlank()) {
            throw new IllegalArgumentException("Usuario inválido");
        }

        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);

            String hash = Seguridad.generarHash(pass);

            try (PreparedStatement pst = cn.prepareStatement(
                    "INSERT INTO usuarios (username, pass_hash, fecha_creacion) VALUES (?, ?, CURDATE())",
                    Statement.RETURN_GENERATED_KEYS)) {

                pst.setString(1, user);
                pst.setString(2, hash);
                pst.executeUpdate();
                pass = null;

                int id_usuario = -1;

                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        id_usuario = rs.getInt(1);
                    } else {
                        cn.rollback();
                        return false; // No se generó ID → algo falló raro
                    }
                }

                Movimientos m = new Movimientos();
                if (!m.registrarSaldoI(cn, id_usuario, saldoI)) {
                    cn.rollback();
                    return false;
                }

                cn.commit();
                return true;

            } catch (SQLException e) {
                try {
                    cn.rollback();
                } catch (Exception ignored) {
                }

                if (e.getErrorCode() == 1062) {
                    throw new IllegalStateException("Usuario duplicado", e);
                }

                throw new RuntimeException("Error en registro", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de conexión al registrar usuario", e);
        }
    }

    public int login() {

        if (user == null || user.isBlank()) {
            throw new IllegalArgumentException("Usuario inválido");
        }

        int idUsuario = -1;

        String hash = Seguridad.generarHash(pass);
        pass = null; // borrar password de memoria

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(
                "SELECT id_usuario FROM usuarios WHERE username = ? AND pass_hash = ?"
        )) {

            pst.setString(1, user);
            pst.setString(2, hash);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    idUsuario = rs.getInt("id_usuario");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error durante el login", e);
        }

        return idUsuario;
    }

}
