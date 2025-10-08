/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import LOGICA.ManejadorErrores;
import static LOGICA.ManejadorErrores.accesoDenegado;
import PERSISTENCIA.ConexionBD;
import java.sql.*;

/**
 *
 * @author Astrid Acosta
 */
public class RegistroService {
    
    
      // Método para insertar usuario
    public boolean insertarUsuario(String usuario, String contraseña, int rol_id) throws SQLException {
        String sql = "INSERT INTO usuarios (usuario, contraseña, rol_id, fecha_registro) VALUES (?, ?, ?, NOW())";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, usuario);
            statement.setString(2, contraseña);
            statement.setInt(3, rol_id);

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0; // devuelve true si se insertó bien
        }
    }

    // Método para generar una contraseña sencilla en base al nombre de usuario
    public String generarContraseña(String usuario) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < usuario.length(); i++) {
            char caracter = usuario.charAt(i);
            char ascii2 = (char) (caracter + 1);
            resultado.append(ascii2);
        }
        return resultado.toString();
    }

    // Método para validar datos de entrada
    public boolean validarCampos(String usuario, String contraseña) {
        return usuario != null && !usuario.isEmpty()
                && contraseña != null && !contraseña.isEmpty();
    }
}
