import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private PasswordRecoveryService recoveryService = new PasswordRecoveryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        if (token == null || token.isEmpty()) {
            // No hay token, redirigir con error
            resp.sendRedirect("main.html?status=invalid_token");
            return;
        }

        // Simplemente redirigimos a la página de reseteo con el token en la URL
        resp.sendRedirect("reset-password.html?token=" + token);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");
        String errorMsg;

        // 1. Validar que las contraseñas no estén vacías y coincidan
        if (newPassword == null || newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            errorMsg = "Las contraseñas no coinciden o están vacías.";
            String redirectUrl = String.format("reset-password.html?token=%s&error=%s",
                    token, URLEncoder.encode(errorMsg, StandardCharsets.UTF_8.toString()));
            resp.sendRedirect(redirectUrl);
            return;
        }

        // 2. Intentar finalizar el reseteo
        boolean success = recoveryService.finalizePasswordReset(token, newPassword);

        if (success) {
            // Éxito, redirigir al login con mensaje de éxito
            resp.sendRedirect("main.html?status=reset_success");
        } else {
            // Fallo (token inválido o expirado)
            errorMsg = "El enlace de recuperación es inválido o ha expirado. Por favor, solicita uno nuevo.";
            String redirectUrl = String.format("reset-password.html?token=%s&error=%s",
                    token, URLEncoder.encode(errorMsg, StandardCharsets.UTF_8.toString()));
            resp.sendRedirect(redirectUrl);
        }
    }
}
