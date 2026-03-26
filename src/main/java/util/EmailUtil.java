package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import modelo.Consultas;

public class EmailUtil {

    public static void enviarConfirmacion(Consultas c) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "sandbox.smtp.mailtrap.io");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ef29aae8420a5b", "75caca5ebdfb84"); 
            }
        });

        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("no-reply@concesionario-may.com")); 
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(c.getEmail()));
            message.setSubject("Mensaje recibido - Concesionario May");
            
            String htmlContent = c.getMensaje();

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println(">>> [¡EXITO TOTAL!] El correo ha salido hacia Mailtrap.");

        } catch (Throwable t) {
            System.err.println(">>> Si ves esto, mira el error en la consola:");
            t.printStackTrace();
        }
    }
    
    public static void enviarTokenRecuperacion(String emailDestino, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "sandbox.smtp.mailtrap.io");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Usamos tus mismas credenciales que ya funcionan
                return new PasswordAuthentication("ef29aae8420a5b", "75caca5ebdfb84"); 
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("seguridad@concesionario-may.com")); 
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));
            message.setSubject("🔑 Recuperación de contraseña - Concesionario May");

            // Creamos el enlace que llevará al JSP de cambio de password
            String enlace = "http://localhost:8080/WebConcesionario//RestablecerPassword?token=" + token;
            String htmlContent = "<h2>Hola,</h2>"
                    + "<p>Has solicitado restablecer tu contraseña en el <strong>Concesionario May</strong>.</p>"
                    + "<p>Haz clic en el botón de abajo para elegir una nueva:</p>"
                    + "<a href='" + enlace + "' style='background-color: #764ba2; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Restablecer Contraseña</a>"
                    + "<p>Si no has sido tú, ignora este mensaje.</p>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println(">>> [¡EXITO!] Token enviado a Mailtrap.");

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}








