package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import util.ServletUtil;
import dao.UsuarioDAO; // Importamos el DAO
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        super();
    }

 
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario u = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (u != null) {
      
            Map<String, Object> respuestaJS = new HashMap<>();
            respuestaJS.put("nombre", u.getNombre());
            respuestaJS.put("usuario", u.getUsuario());
            respuestaJS.put("nivel", u.getRol().getNivel());
            respuestaJS.put("id_usuario", u.getId_usuario());

            ServletUtil.enviarRespuesta(response, respuestaJS);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Sesión no iniciada"));
        }
    }
    
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
        String user = request.getParameter("usuario");
        String pass = request.getParameter("password");

        try {
            Usuario u = UsuarioDAO.getInstance().validar(user, pass);

            if (u != null) {
   
                HttpSession sesion = request.getSession();
                sesion.setAttribute("usuarioLogueado", u);

                Map<String, Object> respuestaJS = new HashMap<>();
                respuestaJS.put("nombre", u.getNombre());
                respuestaJS.put("usuario", u.getUsuario());
                respuestaJS.put("nivel", u.getRol().getNivel()); 
                respuestaJS.put("id_usuario", u.getId_usuario());

                System.out.println("✅ Login exitoso para: " + u.getUsuario() + " (Nivel: " + u.getRol().getNivel() + ")");
                
                ServletUtil.enviarRespuesta(response, respuestaJS);

            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ServletUtil.enviarRespuesta(response, java.util.Map.of("resultado", "ERROR", "mensaje", "Credenciales incorrectas"));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ServletUtil.manejarError(response, e);
        }
    }
}