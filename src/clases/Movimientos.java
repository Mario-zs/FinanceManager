package clases;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Movimientos {

    public static final String TIPO_INGRESO = "Ingreso";
    public static final String TIPO_EGRESO = "Egreso";
    private List<Movimiento> listaMovimientos = new ArrayList<>();

    public boolean registrarSaldoI(Connection cn, int id_usuario, double saldoI) {
        String sql = "INSERT INTO movimientos (id_usuario, tipo, monto, fecha, comentarios) "
                + "VALUES (?,?,?,CURDATE(),'Saldo Inicial')";

        try (PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setInt(1, id_usuario);
            pst.setString(2, TIPO_INGRESO);
            pst.setDouble(3, saldoI);

            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error en registrar saldo inicial: " + e.getMessage());
            return false;
        }
    }

    public double calcularSaldo(int idUsuario) {
        double saldo = 0;

        String sql = "SELECT SUM(CASE "
                + "WHEN tipo = ? THEN monto "
                + "WHEN tipo = ? THEN -monto "
                + "ELSE 0 END) AS saldo "
                + "FROM movimientos WHERE id_usuario = ?";

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setString(1, TIPO_INGRESO);
            pst.setString(2, TIPO_EGRESO);
            pst.setInt(3, idUsuario);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    saldo = rs.getDouble("saldo");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en bd - obtener saldo actual: " + e.getMessage());
        }

        return saldo;
    }

    public double calcularTotalIngresos(int idUsuario) {
        double totalIngresos = 0;

        String sql = "SELECT COALESCE(SUM(monto), 0) AS total_ingresos "
                + "FROM movimientos "
                + "WHERE id_usuario = ? "
                + "AND tipo = 'Ingreso'";

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    totalIngresos = rs.getDouble("total_ingresos");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular total ingresos: " + e.getMessage());
        }

        return totalIngresos;
    }

    public double calcularTotalEgresos(int idUsuario) {
        double totalEgresos = 0;

        String sql = "SELECT COALESCE(SUM(monto), 0) AS total_egresos "
                + "FROM movimientos "
                + "WHERE id_usuario = ? "
                + "AND tipo = 'Egreso'";

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    totalEgresos = rs.getDouble("total_egresos");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular total egresos: " + e.getMessage());
        }

        return totalEgresos;
    }

    public boolean agregarMovimiento(String tipo, double cantidad, String comentarios, int idUsuario) {
        String sql = "INSERT INTO movimientos (id_usuario, tipo, monto, fecha, comentarios) "
                + "VALUES (?, ?, ?, CURDATE(), ?)";

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);
            pst.setString(2, tipo);
            pst.setDouble(3, cantidad);
            pst.setString(4, comentarios);

            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al agregar movimiento: " + e.getMessage());
            return false;
        }
    }

    public List<Integer> obtenerAniosUsuarios(int idUsuario) {
        List<Integer> anios = new ArrayList<>();

        String sql = "SELECT DISTINCT YEAR(fecha) AS anio "
                + "FROM movimientos "
                + "WHERE id_usuario = ? "
                + "ORDER BY anio";

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    anios.add(rs.getInt("anio"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener años de usuario: " + e.getMessage());
        }

        return anios;
    }

    public int obtenerMovimientos(DefaultTableModel modelo, int idUsuario, String filtro, Integer mes, Integer anio,
            List<Integer> ids, List<Integer> idVisual) throws SQLException {

        modelo.setRowCount(0);
        ids.clear();
        listaMovimientos.clear();
        idVisual.clear();
        int index = 1, movimientos = 0;

        StringBuilder sql = new StringBuilder(
                "SELECT id_movimiento, tipo, monto, fecha, comentarios FROM movimientos WHERE id_usuario = ?"
        );

        // filtro por tipo
        if (filtro != null) {
            sql.append(" AND tipo = ?");
        }

        // filtro por mes
        if (mes != null) {
            sql.append(" AND MONTH(fecha) = ?");
        }

        // filtro por año
        if (anio != null) {
            sql.append(" AND YEAR(fecha) = ?");
        }

        sql.append(" ORDER BY fecha DESC");

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql.toString())) {

            pst.setInt(index++, idUsuario);

            if (filtro != null) {
                pst.setString(index++, filtro);
            }
            if (mes != null) {
                pst.setInt(index++, mes);
            }
            if (anio != null) {
                pst.setInt(index++, anio);
            }

            try (ResultSet rs = pst.executeQuery()) {
                // calcular total de movimientos
                int contador = obtenerTotalMovimientos(idUsuario);

                while (rs.next()) {
                    int idMovimiento = rs.getInt("id_movimiento");
                    String tipo = rs.getString("tipo");
                    double monto = rs.getDouble("monto");
                    Date fecha = rs.getDate("fecha");
                    String comentarios = rs.getString("comentarios");

                    // guardar IDs
                    ids.add(idMovimiento);
                    idVisual.add(contador);
                    movimientos++;

                    // crear objeto Movimiento
                    Movimiento mov = new Movimiento(idMovimiento, tipo, monto, fecha, comentarios);
                    listaMovimientos.add(mov);

                    // llenar tabla
                    Object[] fila = {contador--, tipo, monto, fecha};
                    modelo.addRow(fila);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos: " + e.getMessage());
        }

        return movimientos;
    }

    public List<Movimiento> getlistaMovimientos() {
        return listaMovimientos;
    }

    public boolean validarSinMovimientos(int movimientos) {
        return movimientos == 0;
    }

    public Movimiento obtenerDetallesMovimiento(int idMovimiento) {
        Movimiento movimiento = null;

        String sql = "SELECT tipo, monto, fecha, comentarios "
                + "FROM movimientos WHERE id_movimiento = ?";

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setInt(1, idMovimiento);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    double monto = rs.getDouble("monto");
                    Date fecha = rs.getDate("fecha");
                    String comentarios = rs.getString("comentarios");

                    movimiento = new Movimiento(idMovimiento, tipo, monto, fecha, comentarios);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener detalle: " + e.getMessage());
        }

        return movimiento;
    }

    public int obtenerTotalMovimientos(int idUsuario) {
        int total = 0;

        String sql = "SELECT COUNT(*) FROM movimientos WHERE id_usuario = ?";

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener total de movimientos: " + e.getMessage());
        }

        return total;
    }
    
}
