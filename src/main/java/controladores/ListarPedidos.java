package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Pedido;
import modelo.Usuario;
import java.io.IOException;
import java.util.List;

import dao.PedidosDAO;


/**
 * Servlet implementation class ListarPedidos
 */
@WebServlet("/ListarPedidos")
public class ListarPedidos extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListarPedidos() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		HttpSession session = request.getSession();
	    Usuario user = (Usuario) session.getAttribute("usuarioLogueado");

	    if (user == null) {
	        response.sendRedirect("login.jsp");
	        return;
	    }

	    // La clave es este parámetro que mandamos en la URL
	    String origen = request.getParameter("origen"); 

	    try {
	        List<Pedido> listaParaMostrar;

	        if ("perfil".equals(origen)) {
	            listaParaMostrar = PedidosDAO.getInstance().listarPedidosPorEmail(user.getEmail());
	        } 
	        else if (user.getRol().getNivel() <= 2) {
	            listaParaMostrar = PedidosDAO.getInstance().listarTodosLosPedidos();
	        } 
	        else {
	            listaParaMostrar = PedidosDAO.getInstance().listarPedidosPorEmail(user.getEmail());
	        }

	        request.setAttribute("listaPedidos", listaParaMostrar);
	        request.setAttribute("origen", origen); // Pasamos la zona al JSP
	        request.getRequestDispatcher("listar-pedidos.jsp").forward(request, response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendRedirect("perfil.jsp?error=error");
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
