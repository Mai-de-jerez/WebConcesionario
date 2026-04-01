package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.UsuarioDAO;
import util.EmailUtil;
import util.ServletUtil;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/OlvidoPasswordServlet")
public class OlvidoPasswordServlet extends HttpServlet {  
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String accion = request.getParameter("accion");

        if ("validar".equals(accion) && token != null) {

            response.sendRedirect("recuperar-password.html?token=" + token);
        } else {

            response.sendRedirect("login.html");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("solicitar".equals(accion)) {
            ejecutarSolicitar(request, response);
        } else if ("validar".equals(accion)) {
            ejecutarValidar(request, response);
        } else if ("cambiar".equals(accion)) {
            ejecutarCambiar(request, response);
        }
    }

    // El usuario mete su email y pide el token
    private void ejecutarSolicitar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String email = request.getParameter("email");
            String token = UUID.randomUUID().toString();
            UsuarioDAO.getInstance().guardarToken(email, token);
            EmailUtil.enviarTokenRecuperacion(email, token);
            ServletUtil.enviarRespuesta(response, Map.of(
                "resultado", "OK",
                "mensaje", "Si el email existe, recibirás un enlace en unos minutos."
            ));
        } catch (Exception e) {
            e.printStackTrace();
            ServletUtil.manejarError(response, e);
        }
    }

    // Valida si el token es válido y no ha expirado
    private void ejecutarValidar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String token = request.getParameter("token");
            boolean valido = UsuarioDAO.getInstance().validarToken(token);
            if (valido) {
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Token válido"));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "El enlace no es válido o ha caducado."));
            }
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    // Cambia la contraseña con el token
    private void ejecutarCambiar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String token = request.getParameter("token");
            String pass1 = request.getParameter("pass1");
            String pass2 = request.getParameter("pass2");

            if (pass1 == null || !pass1.equals(pass2)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Las contraseñas no coinciden."));
                return;
            }

            boolean ok = UsuarioDAO.getInstance().cambiarPasswordConToken(token, pass1);
            if (ok) {
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Contraseña cambiada correctamente."));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "El token ya no es válido."));
            }
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
}