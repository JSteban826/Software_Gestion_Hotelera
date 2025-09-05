package LOGICA;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import javax.swing.JOptionPane;

public class Logger {

    public static void registrarError(String codigoError, Exception ex) {
        String mensaje = ex.getMessage();
        String log = "[" + LocalDateTime.now() + "] " + codigoError + " - " + mensaje;

        // Mostrar mensaje al usuario
        //JOptionPane.showMessageDialog(null, "Ocurrió un error (" + codigoError + "): "  + mensaje);
        JOptionPane.showMessageDialog(null, "Ocurrió un error (" + codigoError + ")", "Error", JOptionPane.ERROR_MESSAGE);

        // Guardar en archivo de log
        try (FileWriter fw = new FileWriter("errores.log", true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(log);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de log: " + e.getMessage());
        }
    }
}

