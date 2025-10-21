
package LOGICA;

import PERSISTENCIA.ConexionBD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Inventario_prod {
    public static void mostrarInv_HabitacionesEnTabla(JTable jtable_inv_hab) {
        DefaultTableModel model = (DefaultTableModel) jtable_inv_hab.getModel();

        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar();) {
            // Crear la consulta SQL
            String consulta = "SELECT * FROM vista_inventario_habitaciones;";

            // Limpiar la tabla antes de cargar nuevos datos
            model.setRowCount(0);

            // Crear el statement
            try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(consulta)) {

                // Procesar los resultados y agregar filas a la tabla
                while (resultSet.next()) {
                    Object[] fila = {
                        resultSet.getInt("habitacion"),
                        resultSet.getString("tipo"),
                        resultSet.getString("producto"),
                        resultSet.getInt("cantidad"),
                        resultSet.getString("estado")

                    };
                    model.addRow(fila);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
     public static void mostrarProductos_HabitacionesEnTabla(JTable jtable_prod_hab) {
        DefaultTableModel model = (DefaultTableModel) jtable_prod_hab.getModel();

        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar();) {
            // Crear la consulta SQL
            String consulta = "SELECT * FROM productos_habitacion;";

            // Limpiar la tabla antes de cargar nuevos datos
            model.setRowCount(0);

            // Crear el statement
            try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(consulta)) {

                // Procesar los resultados y agregar filas a la tabla
                while (resultSet.next()) {
                    Object[] fila = {
                        resultSet.getString("id_producto"),
                        resultSet.getString("nombre_producto"),
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("valor_reposicion"),
                        resultSet.getString("estado"),
                        resultSet.getInt("cantidad")

                    };
                    model.addRow(fila);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
     
      public static void mostrarProductos_MinibarEnTabla(JTable jtable_prod_mbar) {
        DefaultTableModel model = (DefaultTableModel) jtable_prod_mbar.getModel();

        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar();) {
            // Crear la consulta SQL
            String consulta = "SELECT * FROM productos_minibar;";

            // Limpiar la tabla antes de cargar nuevos datos
            model.setRowCount(0);

            // Crear el statement
            try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(consulta)) {

                // Procesar los resultados y agregar filas a la tabla
                while (resultSet.next()) {
                    Object[] fila = {
                        resultSet.getString("id_producto"),
                        resultSet.getString("nombre_producto"),
                        resultSet.getDouble("valor_unitario"),
                        resultSet.getInt("cantidad")

                    };
                    model.addRow(fila);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
