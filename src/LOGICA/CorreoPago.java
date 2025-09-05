package LOGICA;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class CorreoPago {

   public static void enviarCorreo(String destinatario, String monto) throws CorreoNoEnviadoException {
    final String emisor = "hotelbahiacoral@gmail.com";
    final String claveApp = "yzra lhhs jnif yhme";
    final String emisor1 = "sb-uxzlj32146108@business.example.com";

    String enlace = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick"
            + "&business=" + emisor1
            + "&item_name=CheckOut_Hotel"
            + "&amount=" + monto
            + "&currency_code=USD";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(emisor, claveApp);
        }
    });

    try {
        Message mensaje = new MimeMessage(session);
        mensaje.setFrom(new InternetAddress(emisor));
        mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        mensaje.setSubject("Pago de Reserva(Prueba Sandbox)");
        mensaje.setText("Estimado cliente,\n\nGracias por usar nuestro servicio.\n"
                + "Puede realizar el pago haciendo clic en el siguiente enlace:\n\n"
                + enlace + "\n\n*Este es un enlace de prueba en entorno sandbox.*");

        Transport.send(mensaje);
        System.out.println("Correo enviado correctamente a " + destinatario);

    } catch (MessagingException e) {
        throw new CorreoNoEnviadoException("No se pudo enviar el correo al cliente: " + e.getMessage());
    }
}

}
