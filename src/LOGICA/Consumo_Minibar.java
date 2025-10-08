/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import IGU.MIniBar;

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

}
