/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;
import java.sql.*;
import LOGICA.HistorialManagerSingleton;
import LOGICA.HistorialManager;
import LOGICA.ManejadorErrores;
import static LOGICA.ManejadorErrores.accesoDenegado;
import PERSISTENCIA.ConexionBD;
import java.sql.*;


/**
 *
 * @author Astrid Acosta
 */
public class ClienteService {
     public boolean insertarCliente(String cedula, String nombre, String apellido, String correo, String telefono) throws Exception {
          if (cedula == null || cedula.trim().isEmpty()) {
        throw new NullPointerException("La cédula no puede estar vacía");
    }
    if (nombre == null || nombre.trim().isEmpty()) {
        throw new NullPointerException("El nombre no puede estar vacío");
    }
    if (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        throw new IllegalArgumentException("Correo inválido");
    }
         String sql = "INSERT INTO Clientes (Cedula, nombre, apellido, correo_electronico, telefono) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, cedula);
            statement.setString(2, nombre);
            statement.setString(3, apellido);
            statement.setString(4, correo);
            statement.setString(5, telefono);

            int filas = statement.executeUpdate();
            return filas > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            ManejadorErrores.valorDuplicado(e);
            throw e;
        } catch (SQLTransactionRollbackException e) {
            ManejadorErrores.tablasBloqueadas(e);
            throw e;
        } catch (SQLTimeoutException e) {
            ManejadorErrores.bloqueoTimeout(e);
            throw e;
        } catch (SQLException e) {
            ManejadorErrores.bloqueTrigger(e);
            throw e;
        } catch (Exception e) {
            ManejadorErrores.errorDesconocido(e);
            throw e;
        }
    }
    
}
