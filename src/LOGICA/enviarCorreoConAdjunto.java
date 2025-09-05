/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;

/**
 *
 * @author Windows
 */
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.File;

public class enviarCorreoConAdjunto {

    public static void enviarCorreoConAdjunto(String destinatario, String asunto, String cuerpo, String rutaAdjunto) {
        final String emisor = "hotelbahiacoral@gmail.com";
        final String claveApp = "yzra lhhs jnif yhme"; //

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // <- Esta línea soluciona el problema

        Session sesion = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emisor, claveApp);
            }
        });

        try {
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(emisor));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);

            // Cuerpo del mensaje
            BodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo);

            // Archivo adjunto
            MimeBodyPart adjunto = new MimeBodyPart();
            adjunto.attachFile(new File(rutaAdjunto));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(texto);
            multipart.addBodyPart(adjunto);

            mensaje.setContent(multipart);

            Transport.send(mensaje);
            System.out.println("Correo enviado exitosamente a " + destinatario);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
