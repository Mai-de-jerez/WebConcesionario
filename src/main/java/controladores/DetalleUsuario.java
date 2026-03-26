package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Usuario;
import java.io.IOException;

import dao.UsuarioDAO;

/**
 * Servlet implementation class DetalleUsuario
 */
@WebServlet("/DetalleUsuario")
public class DetalleUsuario extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DetalleUsuario() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Usuario usuLogueado = (Usuario) request.getSession().getAttribute("usuarioLogueado");

        if (usuLogueado == null || usuLogueado.getRol().getNivel() > 2) {
            response.sendRedirect("login.jsp?error=Acceso denegado");
            return;
        }
        
        String idStr = request.getParameter("id");
        
        if (idStr != null) {
        	try {
                int id = Integer.parseInt(idStr);
        
                Usuario uDetalle = UsuarioDAO.getInstance().obtenerPorId(id);
            
                if (uDetalle != null) {
           
	                request.setAttribute("usuarioFicha", uDetalle);
	                request.getRequestDispatcher("detalle_usuario.jsp").forward(request, response);
	            } else {
	                response.sendRedirect("ListarUsuariosServlet?error=Usuario no encontrado");
	            }
	    	} catch (Exception e) {
	            e.printStackTrace();
	            response.sendRedirect("ListarUsuariosServlet?error=Error al buscar usuario");
	        }
        } else {
        	response.sendRedirect("ListarUsuariosServlet");
	    }
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		doGet(request, response);
	}

}
