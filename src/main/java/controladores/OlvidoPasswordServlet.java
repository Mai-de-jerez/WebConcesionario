package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.UsuarioDAO;
import util.EmailUtil;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/OlvidoPasswordServlet")
public class OlvidoPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public OlvidoPasswordServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("olvido-password.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
        String email = request.getParameter("emailRecuperar");
      
        String token = UUID.randomUUID().toString();
                
        try {
       
            UsuarioDAO.getInstance().guardarToken(email, token);

            EmailUtil.enviarTokenRecuperacion(email, token); 
   
            request.setAttribute("mensaje", "Si el email existe en nuestra base de datos, recibirás un enlace de recuperación en unos minutos.");
            request.getRequestDispatcher("olvido-password.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
   
            request.setAttribute("error", "Hubo un problema al procesar la solicitud. Inténtalo más tarde.");
            request.getRequestDispatcher("olvido-password.jsp").forward(request, response);
        }
    }
}