package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import java.io.IOException;
import java.util.List;

import dao.UsuarioDAO;


/**
 * Servlet implementation class ListarUsuariosServlet
 */
@WebServlet("/ListarUsuariosServlet")
public class ListarUsuariosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListarUsuariosServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");

        if (usu == null || usu.getRol().getNivel() > 2) {
            response.sendRedirect("login.jsp?error=Acceso denegado");
            return;
        }
              
        try {
      
            List<Usuario> lista = UsuarioDAO.getInstance().listarTodos(); 

            request.setAttribute("listaUsuarios", lista);
            request.getRequestDispatcher("listar-usuarios.jsp").forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();
            request.setAttribute("error", "Error al cargar usuarios: " + e.getMessage());

            request.getRequestDispatcher("admin-panel.jsp").forward(request, response);
        }
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}
