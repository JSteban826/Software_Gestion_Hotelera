/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import IGU.MIniBar;
import PERSISTENCIA.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import LOGICA.Productos;
import java.sql.Connection;

public class Consumo_Minibar {

    // Método que agrega una fila a la JTable usando los JTextField
    public static void agregarConsumo(JTextField txt_id_prod, JComboBox cmb_productos, JTextField txt_valor, JTextField txt_cantidad, JTable jtable_productos) {
        try {
            // Obtiene el modelo de la tabla
            DefaultTableModel modelo = (DefaultTableModel) jtable_productos.getModel();

            // Valida que no haya campos vacíos
            if (txt_id_prod.getText().isEmpty() || cmb_productos.getSelectedItem().equals("") || txt_valor.getText().isEmpty() || txt_cantidad.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos antes de agregar el consumo.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Crea un arreglo con los datos a insertar
            Object[] fila = new Object[4];
            fila[0] = txt_id_prod.getText();
            fila[1] = cmb_productos.getSelectedItem();
            fila[2] = txt_valor.getText();
            fila[3] = txt_cantidad.getText();

            // Agrega la fila a la tabla
            modelo.addRow(fila);

            // Limpia los campos
            txt_id_prod.setText("");
            cmb_productos.setSelectedItem("");
            txt_valor.setText("");
            txt_cantidad.setText("");

            JOptionPane.showMessageDialog(null, "Consumo agregado correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar el consumo: " + e.getMessage());
        }
    }
    ;
    
    Map<String, Productos> datosProductos = new HashMap<>();

    public void cargarProductosEnComboBox(JTextField txt_id_prod, JComboBox cmb_productos, JTextField txt_valor) {
        String sql = "SELECT id_producto, nombre_producto, valor_unitario FROM productos";

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            cmb_productos.removeAllItems();
            datosProductos.clear();
            txt_id_prod.setText("");
            txt_valor.setText("");

            while (rs.next()) {
                String id_prod = rs.getString("id_producto");
                String nom_prod = rs.getString("nombre_producto");
                int valor = rs.getInt("valor_unitario");

                Productos prod = new Productos(id_prod, nom_prod, valor);

                cmb_productos.addItem(prod);
                datosProductos.put(nom_prod, prod);

            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        // Listener para actualizar campos cuando seleccionas un cliente
        cmb_productos.addActionListener(e -> {
            Productos seleccionado = (Productos) cmb_productos.getSelectedItem(); // ✅ ahora sí es un Producto
            if (seleccionado != null) {
                txt_id_prod.setText(seleccionado.getId_prod());
                txt_valor.setText(String.valueOf(seleccionado.getValor()));
            }
        });

    }

    private void buscarProductosPorId(String id_producto, JTextField txt_id_prod, JComboBox<Productos> cmb_productos, JTextField txt_valor) {
        String sql = "SELECT id_producto, nombre_producto, valor_unitario FROM productos WHERE id_producto = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, id_producto);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String idProd = rs.getString("id_producto");
                    String nombre = rs.getString("nombre_producto");
                    int valor = rs.getInt("valor_unitario");

                    // Crear objeto producto
                    Productos producto = new Productos(idProd, nombre, valor);

                    // Mostrar en los componentes
                    txt_id_prod.setText(idProd);
                    cmb_productos.setSelectedItem(producto); // Buscar el producto en el combo
                    txt_valor.setText(String.valueOf(valor));

                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró ningún producto con ese ID.");
                    txt_id_prod.setText("");
                    txt_valor.setText("");
                }
            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }

}
