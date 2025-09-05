/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;

import PERSISTENCIA.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author elian
 */
public class Check_Out {
    
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
            JOptionPane.showMessageDialog(null, "Error al insertar el pago: " + e.getMessage());
        }
    }
    
}
