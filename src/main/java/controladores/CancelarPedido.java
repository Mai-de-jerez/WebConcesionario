package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.EstadoVehiculo;
import java.io.IOException;
import dao.CocheDAO;
import dao.PedidosDAO;


/**
 * Servlet implementation class CancelarPedido
 */
@WebServlet("/CancelarPedido")
public class CancelarPedido extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
    public CancelarPedido() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    HttpSession session = request.getSession();
	    Integer idPedido = (Integer) session.getAttribute("idPedidoActual");
	    String idCocheParam = request.getParameter("idCoche");
	    
	    
	    if (idPedido != null && idCocheParam != null) {
	        try {
	            int idCoche = Integer.parseInt(idCocheParam);

	            CocheDAO.getInstance().actualizarEstadoCoche(idCoche, EstadoVehiculo.DISPONIBLE);
	   
	   
	            PedidosDAO.getInstance().eliminarPedido(idPedido);
	      
	            session.removeAttribute("idPedidoActual");           
	            response.sendRedirect("Tienda?msj=cancelado");

	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendRedirect("Tienda?error=No_se_pudo_cancelar");
	        }
	    } else {
	        response.sendRedirect("Tienda");
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
