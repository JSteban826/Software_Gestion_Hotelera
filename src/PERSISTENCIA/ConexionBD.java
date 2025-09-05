package PERSISTENCIA;
import LOGICA.ManejadorErrores;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/hotel";
    private static final String USUARIO = "root";
    private static final String CONTRASEÑA = "Juan01020304%";

    public static Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
        } catch (ClassNotFoundException | SQLException ex) {
            ManejadorErrores.errorConexionBD(ex);
        }
        return conexion;
    }

}
