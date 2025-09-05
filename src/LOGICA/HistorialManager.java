package LOGICA;

import java.sql.Connection;
import PERSISTENCIA.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author elian
 */
public class HistorialManager {

    ListaDobleHistorial listaHistorial = new ListaDobleHistorial();

    // Método para registrar una acción
    public void registrarAccion(String accion) {
        // Agregar la acción a la lista doble
        listaHistorial.agregarAccion(accion);

        // Guardar la acción en la base de datos
        String sql = "INSERT INTO historial_acciones (accion) VALUES (?)";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accion);
            ps.executeUpdate();

            //JOptionPane.showMessageDialog(null, "Acción ingresada correctamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar la acción: " + e.getMessage());
        }

    }

    public void registrarLogin(String usuario, String tipoUsuario, boolean exito) {
        String resultado = exito ? "Éxito" : "Fallo";
        String mensaje = "Intento de inicio de sesión [" + resultado + "] - Usuario: " + usuario + " - Tipo: " + tipoUsuario;
        registrarAccion(mensaje);
    }

}
