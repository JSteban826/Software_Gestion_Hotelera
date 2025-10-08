package LOGICA;

import java.util.Locale;
import javax.swing.JOptionPane;

public class PagoService {

    public enum MetodoPago {
        TRANSACCION,
        EFECTIVO
    }

    public static void procesarPago(MetodoPago metodo, String correo, double totalCOP, String nombre, Runnable accionInsertarPago) {
        try {
            if (metodo == MetodoPago.TRANSACCION) {
                double tasaCambio = 4300.0;
                double totalUSD = totalCOP / tasaCambio;
                String montoUSD = String.format(Locale.US, "%.2f", totalUSD);

                // Enviar correo con enlace de pago
                CorreoPago.enviarCorreo(correo, montoUSD);
                JOptionPane.showMessageDialog(null,
                        "Correo enviado con el enlace de pago en dólares.",
                        "Correo Enviado", JOptionPane.INFORMATION_MESSAGE);

                HistorialManager historial = HistorialManagerSingleton.getInstancia();
                historial.registrarAccion("Pago TRANSACCIÓN del cliente: " + nombre + " enviado al correo");

            } else if (metodo == MetodoPago.EFECTIVO) {
                JOptionPane.showMessageDialog(null,
                        "Pago recibido en efectivo.",
                        "Pago en Efectivo", JOptionPane.INFORMATION_MESSAGE);

                HistorialManager historial = HistorialManagerSingleton.getInstancia();
                historial.registrarAccion("Pago en EFECTIVO del cliente: " + nombre);
            }

            // Insertar el pago en la BD
            accionInsertarPago.run();

        } catch (CorreoNoEnviadoException e) {
            ManejadorErrores.enviarEnlace(e);
            JOptionPane.showMessageDialog(null,
                    "No se pudo enviar el correo: " + e.getMessage(),
                    "Error de Correo", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            ManejadorErrores.errorDesconocido(e);
            JOptionPane.showMessageDialog(null,
                    "Error inesperado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
