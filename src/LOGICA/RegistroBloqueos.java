package LOGICA;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroBloqueos {

    private static final String RUTA_ARCHIVO = "bloqueos_login.txt";

    public static void registrar(String usuario, int intentos, boolean bloqueado, long duracionMs) {
        try (FileWriter writer = new FileWriter(RUTA_ARCHIVO, true)) {
            String estado = bloqueado ? "BLOQUEADO" : "DESBLOQUEADO";
            String tiempo = bloqueado ? (duracionMs / 1000) + "s" : "Inmediato";
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String linea = String.format("[%s] Usuario: %s | Intentos: %d | Estado: %s | Tiempo: %s%n",
                    fecha, usuario, intentos, estado, tiempo);

            writer.write(linea);
        } catch (IOException e) {
            System.err.println("Error al registrar bloqueo en archivo: " + e.getMessage());
        }
    }
}
