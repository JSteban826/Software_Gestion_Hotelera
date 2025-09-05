package LOGICA;

import PERSISTENCIA.ConexionBD;
import java.sql.*;

public class ReservaManager {

    public void gestionarReservasVencidas() {
    Connection conn = null;
    PreparedStatement psDeleteReserva = null;
    PreparedStatement psUpdateHabitacion = null;
    PreparedStatement psUpdateHistorial = null;

    try {
        conn = ConexionBD.conectar();
        conn.setAutoCommit(false);

        // Seleccionar reservas vencidas con id_cliente
        String selectSQL = "SELECT id_reserva, id_habitacion, id_cliente FROM reservas WHERE fecha_salida < CURDATE()";
        PreparedStatement psSelect = conn.prepareStatement(selectSQL);
        ResultSet rs = psSelect.executeQuery();

        while (rs.next()) {
            int idReserva = rs.getInt("id_reserva");
            int idHabitacion = rs.getInt("id_habitacion");
            String idCliente = rs.getString("id_cliente");

            // Eliminar reserva
            String deleteReservaSQL = "DELETE FROM reservas WHERE id_reserva = ?";
            psDeleteReserva = conn.prepareStatement(deleteReservaSQL);
            psDeleteReserva.setInt(1, idReserva);
            psDeleteReserva.executeUpdate();

            // Actualizar habitación a 'Libre'
            String updateHabitacionSQL = "UPDATE habitaciones SET estado = 'Libre' WHERE id_habitacion = ?";
            psUpdateHabitacion = conn.prepareStatement(updateHabitacionSQL);
            psUpdateHabitacion.setInt(1, idHabitacion);
            psUpdateHabitacion.executeUpdate();

            // Cambiar estado_reserva a 'finalizada' en historial según id_cliente
            String updateHistorialSQL = "UPDATE historial SET estado_reserva = 'finalizada' WHERE id_cliente = ?";
            psUpdateHistorial = conn.prepareStatement(updateHistorialSQL);
            psUpdateHistorial.setString(1, idCliente);
            psUpdateHistorial.executeUpdate();
        }

        conn.commit();

    } catch (Exception e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        e.printStackTrace();
    } finally {
        try {
            if (psDeleteReserva != null) psDeleteReserva.close();
            if (psUpdateHabitacion != null) psUpdateHabitacion.close();
            if (psUpdateHistorial != null) psUpdateHistorial.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    
}
