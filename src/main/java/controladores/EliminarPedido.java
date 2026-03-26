package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.EstadoVehiculo;
import modelo.Pedido;
import modelo.Usuario;
import java.io.IOException;


/**
 * Servlet implementation class EliminarPedido
 */
@WebServlet("/EliminarPedido")
public class EliminarPedido extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EliminarPedido() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");

        if (user == null || user.getRol().getNivel() != 1) {
            response.sendRedirect("ListarPedidos?error=No tienes permisos para realizar esta accion");
            return; 
        }
        
        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                int idPedido = Integer.parseInt(idParam);
 
                // 1. Buscamos el pedido usando el DAO especializado (PedidosDAO con 's')
                Pedido pedido = dao.PedidosDAO.getInstance().obtenerPorId(idPedido);
                
                if (pedido != null) {
                   
                    // 2. Liberamos el coche usando su ID y el CocheDAO
                    dao.CocheDAO.getInstance().actualizarEstadoCoche(pedido.getIdCoche(), EstadoVehiculo.DISPONIBLE);
              
                    // 3. Eliminamos el pedido físicamente de la BD a través del DAO
                    dao.PedidosDAO.getInstance().eliminarPedido(idPedido); 
              
                    response.sendRedirect("ListarPedidos?msg=Pedido eliminado y coche liberado");
                } else {
                    response.sendRedirect("ListarPedidos?error=Pedido no encontrado");
                }
              
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("ListarPedidos?error=Error al eliminar");
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
