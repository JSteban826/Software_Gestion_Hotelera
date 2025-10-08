/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import PERSISTENCIA.ConexionBD;
/**
 *
 * @author Astrid Acosta
 */
public class AcompañantesService {
    // Método para insertar acompañante
    public void insertarAcompañante(String documento, int id_check_in, String nombre, String apellido, String telefono, String parentesco, int edad) {
        String sql = "INSERT INTO acompanantes (Documento, id_check_in, nombre, apellido, telefono, parentesco, edad) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, documento);
            statement.setInt(2, id_check_in);
            statement.setString(3, nombre);
            statement.setString(4, apellido);
            statement.setString(5, telefono);
            statement.setString(6, parentesco);
            statement.setInt(7, edad);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Acompañante insertado correctamente.");
        } catch (SQLIntegrityConstraintViolationException e) {
            ManejadorErrores.valorDuplicado(e);
        } catch (SQLTransactionRollbackException e) {
            ManejadorErrores.tablasBloqueadas(e);
        } catch (SQLTimeoutException e) {
            ManejadorErrores.bloqueoTimeout(e);
        } catch (SQLException e) {
            ManejadorErrores.bloqueTrigger(e);
        } catch (Exception e) {
            ManejadorErrores.errorDesconocido(e);
        }
    }

    // Método para cargar clientes desde DB
    public Map<String, Cliente1> cargarClientes() {
        Map<String, Cliente1> clientes = new HashMap<>();
        String sql = "SELECT Cedula, nombre, apellido FROM clientes";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String cedula = rs.getString("Cedula");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                Cliente1 cliente = new Cliente1(cedula, nombre, apellido);
                clientes.put(cliente.getNombreCompleto(), cliente);
            }
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
        return clientes;
    }

    // Método para buscar CheckIn por cédula
    public int buscarCheckIn(String cedulaCliente) {
        String sqlReserva = "SELECT id_check_in FROM check_in WHERE id_cliente = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sqlReserva)) {
            ps.setString(1, cedulaCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_check_in");
                }
            }
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
        return -1; // No encontrado
    }

    // Método para buscar cliente por cédula
    public Cliente1 buscarClientePorCedula(String cedula) throws ClienteNoExisteException, SQLException {
        String sql = "SELECT Cedula, nombre, apellido FROM clientes WHERE Cedula = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, cedula);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Cliente1(
                        cedula,
                        rs.getString("nombre"),
                        rs.getString("apellido")
                    );
                } else {
                    throw new ClienteNoExisteException(
                        "El cliente con cédula " + cedula + " no existe en la base de datos."
                    );
                }
            }
        }
    }

    
}
