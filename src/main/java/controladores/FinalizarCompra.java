package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Coche;
import modelo.EstadoPedido;
import modelo.EstadoVehiculo;
import modelo.Pedido;
import modelo.Usuario;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

import dao.CocheDAO;
import dao.PedidosDAO;


/**
 * Servlet implementation class FinalizarCompra
 */
@WebServlet("/FinalizarCompra")
public class FinalizarCompra extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FinalizarCompra() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    HttpSession session = request.getSession();
	    Usuario user = (Usuario) session.getAttribute("usuarioLogueado");

	    String idCocheParam = request.getParameter("idCoche");
	    String importeStr = request.getParameter("importeElegido"); 

	    if (user != null && idCocheParam != null && importeStr != null) {
	        try {
	            int idCoche = Integer.parseInt(idCocheParam);
	            double importeReserva = Double.parseDouble(importeStr);
	            
	            Coche coche = CocheDAO.getInstance().obtenerPorId(idCoche);

	            if (coche != null) {
	  
	                Pedido pedido = new Pedido();
	                pedido.setIdUsuario(user.getId_usuario());
	                pedido.setIdCoche(idCoche);
	                pedido.setImporteAbonado(importeReserva);
	                pedido.setFechaReserva(new Timestamp(System.currentTimeMillis()));
	                
	                pedido.setTransaccionId(UUID.randomUUID().toString());
	                pedido.setEstado(EstadoPedido.PENDIENTE);
	                pedido.setImagen(coche.getImagen());
	         
	                String notasPago = "Titular: " + user.getNombre() + " " + user.getApellidos();
	                pedido.setObservaciones(notasPago);

	                //pedido.insertar(); 
	                PedidosDAO.getInstance().insertarPedido(pedido);

	                CocheDAO.getInstance().actualizarEstadoCoche(idCoche, EstadoVehiculo.RESERVADO);

	                session.removeAttribute("idPedidoActual");
	                response.sendRedirect("confirmacion_compra.jsp?id=" + pedido.getId());
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendRedirect("index.jsp?error=No se pudo finalizar la reserva");
	        }
	    } else {

	        response.sendRedirect("index.jsp");
	    }
	}
	
}

            
            



