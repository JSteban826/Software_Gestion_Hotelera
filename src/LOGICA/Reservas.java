package LOGICA;

import IGU.Reserva;
import PERSISTENCIA.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Reservas {

    // Método para insertar una reserva en la tabla Reservas
    public static void insertarReserva(int idReserva, String idCliente, int idHabitacion, String fechaEntrada, String fechaSalida, double totalPrecio, long diasEstancia) throws SQLIntegrityConstraintViolationException, SQLTransactionRollbackException, SQLTimeoutException, SQLException, FechasInvalidasException {
        
        String sql = "INSERT INTO Reservas (id_reserva, id_cliente, id_habitacion, fecha_entrada, fecha_salida, total_precio, dias_estancia) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, idReserva);
            statement.setString(2, idCliente);
            statement.setInt(3, idHabitacion);
            statement.setString(4, fechaEntrada);
            statement.setString(5, fechaSalida);
            statement.setDouble(6, totalPrecio);
            statement.setLong(7, diasEstancia);
            
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Reserva insertada correctamente.");
        } catch (SQLException e) {
            ManejadorErrores.errorInsertSQL(e);
        }
    }
    
    public static boolean existeCliente(String cedula) {
        boolean existe = false;
        Connection conn = ConexionBD.conectar(); // usa tu clase de conexión

        String sql = "SELECT COUNT(*) FROM clientes WHERE Cedula = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar cliente: " + e.getMessage());
        }
        
        return existe;
    }
    
    public static void insertarPago(int idPago, String idCliente, double valorPago, String estadoPago) {
        String sql = "INSERT INTO historial_pagos (id_pago, id_cliente, valor_pago, estado_pago) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, idPago);
            statement.setString(2, idCliente);
            statement.setDouble(3, valorPago);
            statement.setString(4, estadoPago);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Pago insertado correctamente.");
        } catch (SQLException e) {
           ManejadorErrores.errorInsertSQL(e);
        }
    }
    
    public static void insertarHistorial(int idHistorial, int idReserva, String idCliente, int idHabitacion, String fechaEntrada, String fechaSalida, double totalPrecio, String estadoReserva) {
        String sql = "INSERT INTO historial (id_historial, id_reserva, id_cliente, id_habitacion, fecha_entrada, fecha_salida, total_precio, estado_reserva) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, idHistorial);
            statement.setInt(2, idReserva);
            statement.setString(3, idCliente);
            statement.setInt(4, idHabitacion);
            statement.setString(5, fechaEntrada);
            statement.setString(6, fechaSalida);
            statement.setDouble(7, totalPrecio);
            statement.setString(8, estadoReserva);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Reserva insertada correctamente.");
        } catch (SQLException e) {
            ManejadorErrores.errorInsertSQL(e);
        }
    }
    
    public static void mostrarReservasEnTabla(JTable tabla) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();

        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar()) {
            // Crear la consulta SQL
            String consulta = "SELECT * FROM reservas";

            // Limpiar la tabla antes de cargar nuevos datos
            model.setRowCount(0);

            // Crear el statement
            try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(consulta)) {

                // Procesar los resultados y agregar filas a la tabla
                while (resultSet.next()) {
                    Object[] fila = {
                        resultSet.getInt("id_reserva"),
                        resultSet.getString("id_cliente"),
                        resultSet.getInt("id_habitacion"),
                        resultSet.getString("fecha_entrada"),
                        resultSet.getString("fecha_salida"),
                        resultSet.getDouble("total_precio")
                    };
                    model.addRow(fila);
                }
            }
        } catch (SQLException ex) {
            ManejadorErrores.errorSelectSQL(ex);
        }
    }
    
    public static void actualizarEstadoHabitacion(int idHabitacion, String estado) {
        String sqlUpdate = "UPDATE habitaciones SET estado = ? WHERE id_habitacion = ?";
        
        try (Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            
            pstmt.setString(1, estado);
            pstmt.setInt(2, idHabitacion);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Estado de ocupación actualizado para la habitación con ID: " + idHabitacion);
        } catch (SQLException e) {
            ManejadorErrores.errorUpdateSQL(e);
        }
    }
    
    public static double obtenerPrecioNoche(int idHabitacion) {
        // Declaración e inicialización de la variable para almacenar el precio por noche
        double precioNoche = 0.0;
        // Consulta SQL para obtener el precio por noche a partir del ID de la habitación
        String consulta = "SELECT precio_noche FROM Habitaciones WHERE id_habitacion = ?";
        
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(consulta)) {
            // Establecer el parámetro ID de la habitación en la consulta
            statement.setInt(1, idHabitacion);

            // Ejecutar la consulta y obtener el resultado
            try (ResultSet resultSet = statement.executeQuery()) {
                // Verificar si se encontró el precio por noche
                if (resultSet.next()) {
                    // Obtener el precio por noche del resultado
                    precioNoche = resultSet.getDouble("precio_noche");
                } else {
                    System.out.println("No se encontró el precio por noche para la habitación con ID: " + idHabitacion);
                }
            }
        } catch (SQLException ex) {
            ManejadorErrores.errorSelectSQL(ex);
        }

        // Devolver el precio por noche
        return precioNoche;
    }
    
    public static void eliminarReserva(int idReserva) {
        Connection conn = null;
        String sqlUpdateHistorial = "UPDATE historial SET estado_reserva = 'eliminada' WHERE id_reserva = ?";
        String sqlUpdateHabitacion = "UPDATE habitaciones SET estado = 'Libre' WHERE id_habitacion = (SELECT id_habitacion FROM reservas WHERE id_reserva = ?)";
        String sqlDeleteReserva = "DELETE FROM reservas WHERE id_reserva = ?";
        
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false); // Iniciar transacción

            try (PreparedStatement stmtUpdateHistorial = conn.prepareStatement(sqlUpdateHistorial); PreparedStatement stmtUpdateHabitacion = conn.prepareStatement(sqlUpdateHabitacion); PreparedStatement stmtDeleteReserva = conn.prepareStatement(sqlDeleteReserva)) {

                // 1. Marcar la reserva como eliminada en el historial
                stmtUpdateHistorial.setInt(1, idReserva);
                stmtUpdateHistorial.executeUpdate();

                // 2. Actualizar la habitación como "Libre"
                stmtUpdateHabitacion.setInt(1, idReserva);
                stmtUpdateHabitacion.executeUpdate();

                // 3. Eliminar la reserva
                stmtDeleteReserva.setInt(1, idReserva);
                stmtDeleteReserva.executeUpdate();
                
                conn.commit(); // Confirmar transacción
                JOptionPane.showMessageDialog(null, "Reserva eliminada correctamente y habitación liberada.");
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir cambios si hay error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            ManejadorErrores.errorDeleteSQL(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Cerrar la conexión
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
    
    public static void modificarReserva(int idReserva, String idCliente, int idHabitacion, String fechaEntrada, String fechaSalida) {
        // Verificar que los datos no estén vacíos
        if (fechaEntrada == null || fechaSalida == null) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione fechas válidas.");
            return;
        }

        // Calcular el nuevo precio total según las fechas actualizadas
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaIn, fechaOut;
        try {
            fechaIn = sdf.parse(fechaEntrada);
            fechaOut = sdf.parse(fechaSalida);
            
            long diferenciaEnMilisegundos = fechaOut.getTime() - fechaIn.getTime();
            long diasDiferencia = diferenciaEnMilisegundos / (1000 * 60 * 60 * 24);
            
            double precioNoche = obtenerPrecioNoche(idHabitacion);
            double totalPrecio = diasDiferencia * precioNoche;

            // Actualizar la reserva en la base de datos
            String sql = "UPDATE Reservas SET id_cliente = ?, id_habitacion = ?, fecha_entrada = ?, fecha_salida = ?, total_precio = ? WHERE id_reserva = ?";
            try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, idCliente);
                statement.setInt(2, idHabitacion);
                statement.setString(3, fechaEntrada);
                statement.setString(4, fechaSalida);
                statement.setDouble(5, totalPrecio);
                statement.setInt(6, idReserva);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Reserva modificada correctamente. Nuevo total: " + totalPrecio + " pesos.");
            } catch (SQLException e) {
                ManejadorErrores.errorUpdateSQL(e);
            }
            
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Error al procesar las fechas.");
        }
    }
    
    public static Reserva.ResultadoPrecio calcularPrecioYEstancia(int idHabitacion, Date fechaEntrada, Date fechaSalida) throws FechasInvalidasException {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new FechasInvalidasException(CodigoError.ERR_FECHAS_INVALIDAS, "Por favor seleccione fechas validas.");
            
        }
        
        long diferenciaEnMilisegundos = fechaSalida.getTime() - fechaEntrada.getTime();
        long diasDiferencia = diferenciaEnMilisegundos / (1000 * 60 * 60 * 24);
        
        if (diasDiferencia <= 0) {
            throw new FechasInvalidasException(CodigoError.ERR_FECHAS_INVALIDAS, "La fecha de salida debe ser posterior a la de entrada.");
            
        }
        
        double precioNoche = obtenerPrecioNoche(idHabitacion);
        double totalPrecio = diasDiferencia * precioNoche;
        
        JOptionPane.showMessageDialog(null, "El Precio Total de la reserva es: " + totalPrecio + " pesos");
        
        return new Reserva.ResultadoPrecio(totalPrecio, diasDiferencia);
    }
    
    public static boolean verificarAnticipacion(String fechaEntrada) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date fecha = sdf.parse(fechaEntrada);
            Date hoy = new Date();
            long diferencia = (fecha.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24);
            return diferencia >= 1;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }    
    
}
