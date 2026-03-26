package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import dao.UsuarioDAO;


/**
 * Servlet implementation class RestablecerPassword
 */
@WebServlet("/RestablecerPassword")
public class RestablecerPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RestablecerPassword() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	String token = request.getParameter("token");
             
        try {

            if (token != null && UsuarioDAO.getInstance().validarToken(token)) {
            	
                request.setAttribute("token", token);
                request.getRequestDispatcher("nueva-password.jsp").forward(request, response);
                
            } else {
                response.getWriter().println("<h1>El enlace no es valido o ha caducado.</h1>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
  

    // Se activa cuando el usuario escribe la clave y da al botón
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        String pass1 = request.getParameter("pass1");
        String pass2 = request.getParameter("pass2");

        if (pass1 != null && pass1.equals(pass2)) {
            try {

            	boolean ok = UsuarioDAO.getInstance().cambiarPasswordConToken(token, pass1);
                
                if (ok) {
                    request.setAttribute("mensaje", "¡Contraseña cambiada con exito! Ya puedes entrar.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                } else {
                    response.getWriter().println("Error al actualizar. El token ya no es valido.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Error técnico. Inténtalo de nuevo.");
                request.getRequestDispatcher("nueva-password.jsp").forward(request, response);
            }
        } else {
            response.getWriter().println("<h1>Las contraseñas no coinciden.</h1>");
            request.setAttribute("token", token); 
            request.getRequestDispatcher("nueva-password.jsp").forward(request, response);
        }
    }
    
}
                  


