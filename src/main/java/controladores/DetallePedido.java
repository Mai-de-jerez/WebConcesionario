package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Coche;
import modelo.Pedido;
import modelo.Usuario;

import java.io.IOException;

import dao.CocheDAO;
import dao.PedidosDAO;
import dao.UsuarioDAO;


/**
 * Servlet implementation class DetallePedido
 */
@WebServlet("/DetallePedido")
public class DetallePedido extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DetallePedido() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String idParam = request.getParameter("id");
	
	    String origen = request.getParameter("origen"); 

	    if (idParam != null) {
	    	
	    	try {
	    	    int id = Integer.parseInt(idParam);
	    
	    	    Pedido pedido = PedidosDAO.getInstance().obtenerPorId(id);


	            if (pedido != null) {
	
	            	Usuario cliente = UsuarioDAO.getInstance().obtenerPorId(pedido.getIdUsuario());    
	                Coche coche = CocheDAO.getInstance().obtenerPorId(pedido.getIdCoche());
	          
	                request.setAttribute("pedido", pedido);
	                request.setAttribute("cliente", cliente); 
	                request.setAttribute("coche", coche);     
	                request.setAttribute("origen", origen); 

	                request.getRequestDispatcher("detalle-pedido.jsp").forward(request, response);
	            } else {
	                response.sendRedirect("ListarPedidos?error=notfound" + (origen != null ? "&origen=" + origen : ""));
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendRedirect("ListarPedidos?error=error" + (origen != null ? "&origen=" + origen : ""));
	        }
	    } else {
	        response.sendRedirect("ListarPedidos");
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
