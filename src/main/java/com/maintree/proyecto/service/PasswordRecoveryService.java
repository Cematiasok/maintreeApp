package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.UsuarioDAO;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.util.PasswordHasher;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;

public class PasswordRecoveryService {

    private final UsuarioDAO usuarioDAO;
    private static final long EXPIRATION_TIME_MS = 3600000; // 1 hora

    // Constructor para Inyección de Dependencias.
    public PasswordRecoveryService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Inicia el proceso de reseteo de contraseña.
     * Genera un token, lo guarda en la BD y envía el email de recuperación.
     * @param email El email del usuario que solicita el reseteo.
     * @return true si el proceso se inició correctamente, false si el email no existe.
     */
    public boolean initiatePasswordReset(String email, String requestUrl) {
        Usuario usuario = usuarioDAO.findByEmail(email);
        if (usuario == null) {
            return false; // No revelar que el usuario no existe por seguridad, pero el proceso falla.
        }

        String token = UUID.randomUUID().toString();
        Date expiryDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS);

        usuario.setResetToken(token);
        usuario.setResetTokenExpiry(expiryDate);
        usuarioDAO.update(usuario);

        sendRecoveryEmail(usuario, requestUrl);
        return true;
    }

    /**
     * Finaliza el proceso de reseteo.
     * Verifica el token y actualiza la contraseña.
     * @param token El token de reseteo.
     * @param newPassword La nueva contraseña (sin hashear).
     * @return true si la contraseña se actualizó, false si el token es inválido o expiró.
     */
    public boolean finalizePasswordReset(String token, String newPassword) {
        Usuario usuario = usuarioDAO.findByResetToken(token);

        if (usuario == null || usuario.getResetTokenExpiry() == null || usuario.getResetTokenExpiry().before(new Date())) {
            return false; // Token no encontrado o expirado.
        }

        // Hashear la nueva contraseña
        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        usuario.setPassword(hashedPassword);

        // Limpiar el token para que no se pueda reutilizar
        usuario.setResetToken(null);
        usuario.setResetTokenExpiry(null);
        usuarioDAO.update(usuario);

        return true;
    }

    /**
     * Envía el email de recuperación de contraseña.
     * ¡NECESITA CONFIGURACIÓN!
     */
    private void sendRecoveryEmail(Usuario usuario, String requestUrl) {
        // --- ¡CONFIGURACIÓN REQUERIDA! ---
        // Sustituye con los datos de tu servidor de correo (SMTP)
        final String username = "noreply@maintree.app"; // Tu email de envío
        final String password = "";   // O una contraseña de aplicación si usas Gmail/Outlook
        final String host = "localhost";      // Servidor SMTP
        final String port = "1025";                   // Puerto SMTP (587 para TLS, 465 para SSL)

        Properties props = new Properties();
        // Para desarrollo local con MailHog, la autenticación se establece en 'false'.
        // Para un servidor SMTP real (producción), cambia esto a 'true' y proporciona un 'username' y 'password'.
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(usuario.getEmail()));
            message.setSubject("Recuperación de Contraseña - MaintreeApp");

            // Construye la URL base a partir de la URL de la solicitud
            String baseUrl = requestUrl.substring(0, requestUrl.length() - "/forgot-password".length());
            String resetLink = baseUrl + "/reset-password.html?token=" + usuario.getResetToken();


            String emailBody = "Hola, \n\n"
                    + "Has solicitado restablecer tu contraseña.\n"
                    + "Haz clic en el siguiente enlace para continuar:\n"
                    + "<a href=\"" + resetLink + "\">Restablecer Contraseña</a>\n\n"
                    + "Si no solicitaste esto, por favor ignora este correo.\n\n"
                    + "El enlace expirará en 1 hora.";

            message.setContent(emailBody, "text/html; charset=utf-8");


            Transport.send(message);

            System.out.println("Email de recuperación enviado a " + usuario.getEmail());

        } catch (MessagingException e) {
            // En una app real, aquí deberías tener un log más robusto.
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }
}
