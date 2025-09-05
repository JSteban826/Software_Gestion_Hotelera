package LOGICA;

import PERSISTENCIA.ConexionBD;
import LOGICA.FechasInvalidasException;
import java.sql.*;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import IGU.Reserva.ResultadoPrecio;

public class GestionReservas {

    Connection conn = ConexionBD.conectar();

    public String obtenerCorreoCliente(int idCliente) {
        String correo = null;
        String sql = "SELECT correo_electronico FROM Clientes WHERE Cedula = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                correo = rs.getString("correo_electronico");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return correo;
    }

    public double obtenerMontoReserva(int idReserva) {
        double monto = 0.0;

        try {

            String sql = "SELECT total_precio FROM Reservas WHERE id_reserva = ?";

            try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idReserva);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    monto = rs.getDouble("total_precio");
                }
            }
        } catch (NumberFormatException e) {
            ManejadorErrores.conversion(e);
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        return monto;

    }

    public String convertirPesosADolares(double totalCOP) {
        double tasaCambio = 4300.0;
        double totalUSD = totalCOP / tasaCambio;
        return String.format(Locale.US, "%.2f", totalUSD);
    }

    public void procesarPago(int idCliente, int idReserva) throws PagoFallidoException, CorreoNoEnviadoException {
        String correoCliente = obtenerCorreoCliente(idCliente);
        double totalCOP = obtenerMontoReserva(idReserva);

        if (correoCliente != null && totalCOP > 0) {
            String montoUSD = convertirPesosADolares(totalCOP);
            CorreoPago.enviarCorreo(correoCliente, montoUSD); // puede lanzar excepción

            JOptionPane.showMessageDialog(null,
                    "Procesando pago para el cliente: " + correoCliente + "\nMonto: $" + montoUSD + " USD",
                    "Confirmación de Pago", JOptionPane.INFORMATION_MESSAGE);
        } else {
            throw new PagoFallidoException("Error: Correo del cliente o monto de reserva no válido.");
        }
    }

    public void capturarDatosYRealizarPago(JTextField txtIdCliente, JTextField txtIdReserva) throws PagoFallidoException, CorreoNoEnviadoException {
        try {
            int idCliente = Integer.parseInt(txtIdCliente.getText().trim());
            int idReserva = Integer.parseInt(txtIdReserva.getText().trim());

            procesarPago(idCliente, idReserva);

        } catch (CorreoNoEnviadoException e) {
            ManejadorErrores.enviarEnlace(e);
            JOptionPane.showMessageDialog(null,
                    "Error al enviar el correo: " + e.getMessage(),
                    "Error de Correo", JOptionPane.ERROR_MESSAGE);
        } catch (PagoFallidoException e) {
            ManejadorErrores.errorPago(e);
            JOptionPane.showMessageDialog(null,
                    "Error al procesar el pago: " + e.getMessage(),
                    "Error de Pago", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            ManejadorErrores.conversion(e);
        }
    }

    public boolean clienteExiste(String cedula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Clientes WHERE Cedula = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

}
