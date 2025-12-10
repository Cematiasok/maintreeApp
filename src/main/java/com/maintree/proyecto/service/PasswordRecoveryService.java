package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.UsuarioRepository;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
public class PasswordRecoveryService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final long EXPIRATION_TIME_MS = 3600000; // 1 hora

    public boolean initiatePasswordReset(String email, String requestUrl) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            return false;
        }

        String token = UUID.randomUUID().toString();
        Date expiryDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS);

        usuario.setResetToken(token);
        usuario.setResetTokenExpiry(expiryDate);
        usuarioRepository.save(usuario);

        sendRecoveryEmail(usuario, requestUrl);
        return true;
    }

    public boolean finalizePasswordReset(String token, String newPassword) {
        Usuario usuario = usuarioRepository.findByResetToken(token);

        if (usuario == null || usuario.getResetTokenExpiry() == null || usuario.getResetTokenExpiry().before(new Date())) {
            return false;
        }

        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        usuario.setPassword(hashedPassword);

        usuario.setResetToken(null);
        usuario.setResetTokenExpiry(null);
        usuarioRepository.save(usuario);

        return true;
    }

    @SuppressWarnings("null")
    private void sendRecoveryEmail(Usuario usuario, String requestUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(usuario.getEmail() != null ? usuario.getEmail() : "");
            helper.setSubject("Recuperación de Contraseña - MaintreeApp");

            // Construir baseUrl de forma robusta a partir de la request URL recibida.
            String baseUrl;
            try {
                URL url = new java.net.URI(requestUrl).toURL();
                baseUrl = url.getProtocol() + "://" + url.getHost();
                if (url.getPort() != -1) {
                    baseUrl += ":" + url.getPort();
                }
            } catch (Exception e) {
                // Fallback a la lógica anterior, pero manejando '/api/forgot-password' también
                if (requestUrl.endsWith("/api/forgot-password")) {
                    baseUrl = requestUrl.substring(0, requestUrl.length() - "/api/forgot-password".length());
                } else if (requestUrl.endsWith("/forgot-password")) {
                    baseUrl = requestUrl.substring(0, requestUrl.length() - "/forgot-password".length());
                } else {
                    // Ultimo recurso: usar requestUrl tal cual
                    baseUrl = requestUrl;
                }
            }

            String resetLink = baseUrl + "/reset-password.html?token=" + usuario.getResetToken();

            String emailBody = "Hola, \n\n"
                    + "Has solicitado restablecer tu contraseña.\n"
                    + "Haz clic en el siguiente enlace para continuar:\n"
                    + "<a href=\"" + resetLink + "\">Restablecer Contraseña</a>\n\n"
                    + "Si no solicitaste esto, por favor ignora este correo.\n\n"
                    + "El enlace expirará en 1 hora.";

            helper.setText(emailBody, true);

            mailSender.send(message);

            System.out.println("Email de recuperación enviado a " + usuario.getEmail());

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }
}
