package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import java.io.IOException;

import dao.UsuarioDAO;

/**
 * Servlet implementation class ActualizarPerfilServlet
 */
@WebServlet("/ActualizarPerfilServlet")
public class ActualizarPerfilServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActualizarPerfilServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    HttpSession session = request.getSession();
	    Usuario usuLogueado = (Usuario) session.getAttribute("usuarioLogueado");

	    if (usuLogueado == null) {

	        response.sendRedirect("login.jsp");
	        return;
	    }

	    request.getRequestDispatcher("editar-perfil.jsp").forward(request, response);
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Usuario usuLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuLogueado == null) {
            response.sendRedirect("login.jsp");
            return; 
        }

        try {
            String nombreUsu = request.getParameter("usuario");
            String email = request.getParameter("email");
            String nombreReal = request.getParameter("nombre");    
	        String apellidos = request.getParameter("apellidos"); 
            String passNueva = request.getParameter("password");
            String passConfirm = request.getParameter("passwordConfirm");

            if (passNueva != null && !passNueva.trim().isEmpty()) {
                
                if (!passNueva.equals(passConfirm)) {
                    session.setAttribute("mensaje", "❌ Las contraseñas no coinciden. Inténtalo de nuevo.");
                    response.sendRedirect("editar-perfil.jsp");
                    return; 
                }
   
                usuLogueado.setPassword(passNueva);
            }

            usuLogueado.setUsuario(nombreUsu);
            usuLogueado.setEmail(email);
            usuLogueado.setNombre(nombreReal);    
	        usuLogueado.setApellidos(apellidos);

            UsuarioDAO.getInstance().actualizar(usuLogueado);

            session.setAttribute("usuarioLogueado", usuLogueado);
            session.setAttribute("mensaje", "✅ Tu perfil ha sido actualizado con éxito.");

            response.sendRedirect("perfil.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("mensaje", "❌ Error técnico: " + e.getMessage());
            response.sendRedirect("editar-perfil.jsp");
        }
    }
	 
}


