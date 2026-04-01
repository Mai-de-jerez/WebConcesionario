package util;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import modelo.Consultas;

public class EmailUtil {

    private static Session crearSesion() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "sandbox.smtp.mailtrap.io");
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    System.getenv("MAIL_USER"),
                    System.getenv("MAIL_PASSWORD")
                );
            }
        });
    }

    public static void enviarConfirmacion(Consultas c) {
        try {
            Message message = new MimeMessage(crearSesion());
            message.setFrom(new InternetAddress("no-reply@concesionario-may.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(c.getEmail()));
            message.setSubject("Mensaje recibido - Concesionario May");
            message.setContent(c.getMensaje(), "text/html; charset=utf-8");
            Transport.send(message);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void enviarTokenRecuperacion(String emailDestino, String token) {
        try {
            String enlace = "http://localhost:8080/WebConcesionario/OlvidoPasswordServlet?token=" + token + "&accion=validar";
            String html = "<h2>Hola,</h2>"
                + "<p>Has solicitado restablecer tu contraseña en el <strong>Concesionario May</strong>.</p>"
                + "<p>Haz clic en el botón de abajo para elegir una nueva:</p>"
                + "<a href='" + enlace + "' style='background-color: #764ba2; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Restablecer Contraseña</a>"
                + "<p>Si no has sido tú, ignora este mensaje.</p>";
            Message message = new MimeMessage(crearSesion());
            message.setFrom(new InternetAddress("seguridad@concesionario-may.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));
            message.setSubject("🔑 Recuperación de contraseña - Concesionario May");
            message.setContent(html, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}








