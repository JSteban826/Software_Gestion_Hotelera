/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;
import javax.swing.JOptionPane;


/**
 *
 * @author Astrid Acosta
 */
public class CheckInService {
     public void registrarCheckIn(String idReserva, String documentoCliente, String fechaEntrada, String fechaSalida) {
        if (idReserva == null || idReserva.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El ID de la reserva es obligatorio");
            return;
        }
        if (documentoCliente == null || documentoCliente.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El documento del cliente es obligatorio");
            return;
        }

        // Aquí iría la conexión a BD o lógica real
        JOptionPane.showMessageDialog(null, "✅ Check-In registrado con éxito");
    }

    public void buscarReserva(String idReserva) {
        if (idReserva == null || idReserva.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debes ingresar un ID de reserva");
            return;
        }

        // Aquí iría la búsqueda en BD
        JOptionPane.showMessageDialog(null, "🔎 Reserva encontrada: " + idReserva);
    }

    public void limpiarCampos(javax.swing.JTextField... campos) {
        for (javax.swing.JTextField campo : campos) {
            campo.setText("");
        }
        JOptionPane.showMessageDialog(null, "🧹 Campos limpiados");
    }
    
}
