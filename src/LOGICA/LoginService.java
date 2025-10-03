/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;

/**
 *
 * @author Astrid Acosta
 */


import LOGICA.HistorialManagerSingleton;
import LOGICA.HistorialManager;
import LOGICA.ManejadorErrores;
import static LOGICA.ManejadorErrores.accesoDenegado;
import PERSISTENCIA.ConexionBD;
import java.sql.*;

public class LoginService {

    /**
     * Valida las credenciales de usuario contra la base de datos.
     * 
     * @param usuario Nombre de usuario
     * @param contraseña Contraseña
     * @return Rol asociado al usuario si las credenciales son correctas, o null si no existe
     */
    public String validarLogin(String usuario, String contraseña) {
        String sql = "SELECT r.nombre AS rol " +
                     "FROM usuarios u " +
                     "JOIN roles r ON u.rol_id = r.id " +
                     "WHERE u.usuario = ? AND u.contraseña = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, usuario);
            statement.setString(2, contraseña);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getString("rol"); // Ej: "Administrador" o "Recepcionista"
            } else {
                return null;
            }

        } catch (SQLTransactionRollbackException e) {
            ManejadorErrores.tablasBloqueadas(e);
        } catch (SQLTimeoutException e) {
            ManejadorErrores.bloqueoTimeout(e);
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        } catch (Exception e) {
            ManejadorErrores.errorDesconocido(e);
        }

        return null;
    }

    /**
     * Maneja el login con registro en historial.
     * 
     * @param usuario Usuario ingresado
     * @param contraseña Contraseña ingresada
     * @return Rol autenticado o mensaje de error
     */
    public String realizarLogin(String usuario, String contraseña) {
        try {
            String rol = validarLogin(usuario, contraseña);
            HistorialManager historial = HistorialManagerSingleton.getInstancia();

            if (rol != null) {
                historial.registrarAccion("Ingreso de " + rol + ": " + usuario);
                historial.registrarLogin(usuario, rol, true);
                return rol; // devolver rol válido para que el JFrame decida qué hacer
            } else {
                historial.registrarLogin(usuario, "Desconocido", false);
                return null;
            }

        } catch (Exception e) {
            ManejadorErrores.errorDesconocido(e);
            return null;
        }
    }
}
