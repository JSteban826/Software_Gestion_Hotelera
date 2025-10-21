package LOGICA;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.JOptionPane;

public class CorreoPago {

    // Método reutilizable para enviar el correo con enlace de pago
    public static void enviarCorreo(String destinatario, String monto, Locale locale) throws CorreoNoEnviadoException {
        final String emisor = "hotelbahiacoral@gmail.com";
        final String claveApp = "yzra lhhs jnif yhme";
        final String emisor1 = "sb-uxzlj32146108@business.example.com";

        // 📦 Carga de los mensajes según el idioma
        Locale idioma = Locale.getDefault();
        ResourceBundle mensajes = ResourceBundle.getBundle("LOGICA.mensajes", idioma);

        // 💳 Enlace dinámico de PayPal (modo Sandbox)
        String enlace = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick"
                + "&business=" + emisor1
                + "&item_name=CheckOut_Hotel"
                + "&amount=" + monto
                + "&currency_code=USD";

        // 🏷️ Obtiene los textos traducidos del archivo .properties
        String asunto = mensajes.getString("correo.asunto");
        String cuerpo = mensajes.getString("correo.cuerpo")
                .replace("{monto}", monto)
                .replace("{enlace}", enlace);

        // ⚙️ Configuración SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emisor, claveApp);
            }
        });

        try {
            // ✉️ Creación del mensaje
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(emisor));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);

            // 🚀 Envío del correo
            Transport.send(mensaje);
            System.out.println(mensajes.getString("correo.enviado") + " -> " + destinatario);
            JOptionPane.showMessageDialog(null, mensajes.getString("correo.enviado") + " -> " + destinatario);

        } catch (MessagingException e) {
            throw new CorreoNoEnviadoException(
                    mensajes.getString("correo.error") + ": " + e.getMessage());
        }
    }
}
