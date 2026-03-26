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
 * Servlet implementation class EliminarUsuario
 */
@WebServlet("/EliminarUsuario")
public class EliminarUsuario extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EliminarUsuario() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Usuario usuLogueado = (Usuario) request.getSession().getAttribute("usuarioLogueado");

		if (usuLogueado == null || usuLogueado.getRol().getNivel() != 1) {
	
		    response.sendRedirect("ListarUsuariosServlet?error=Solo los Administradores pueden borrar usuarios.");
		    return;
		}

		String idStr = request.getParameter("id");

		if (idStr != null) {
			try {
	            int idABorrar = Integer.parseInt(idStr);

	            if (idABorrar != usuLogueado.getId_usuario()) {
	          
	                Usuario uABorrar = UsuarioDAO.getInstance().obtenerPorId(idABorrar);
	                
	                if (uABorrar != null) {

	                    UsuarioDAO.getInstance().eliminar(idABorrar);
	                    response.sendRedirect("ListarUsuariosServlet?mensaje=Usuario eliminado con éxito.");
	                } else {
	                    response.sendRedirect("ListarUsuariosServlet?error=Usuario no encontrado.");
	                }
                    
	            } else {
	     
	                response.sendRedirect("ListarUsuariosServlet?error=No puedes eliminar tu propia cuenta.");
	            }
	                
	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendRedirect("ListarUsuariosServlet?error=Error al eliminar (puede que tenga pedidos asociados).");
	        }
	    }
	}
	
	


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}








