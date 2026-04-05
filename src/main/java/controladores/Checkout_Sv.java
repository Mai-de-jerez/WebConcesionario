package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ReservaService;
import util.ServletUtil;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet implementation class Checkout_Sv
 */
@WebServlet("/Checkout_Sv")
public class Checkout_Sv extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ReservaService reservaService = ReservaService.getInstance();  
    
	/**
     * @see HttpServlet#HttpServlet()
     */
    public Checkout_Sv() {
        super();
    }

    /**
     * Redirige a la página de checkout si el cliente está logueado, o al index público si no lo está
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.sendRedirect("Tienda_Sv");
    }

    /**
     * Gestiona la acción de guardar la reserva en la DB
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        
        if ("reservar".equals(accion)) {
            ejecutarReservar(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
	
	private void ejecutarReservar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
        	if (!esClienteLogueado(request)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Debes iniciar sesión para reservar"));
                return;
            }

            Usuario user = obtenerUsuario(request);
            int idCoche = ServletUtil.parsearInt(request.getParameter("idCoche"), "ID del coche");
            double importeSenal = ServletUtil.parsearDouble(request.getParameter("importeSenal"));

            String metodoPagoStr = request.getParameter("metodoPago"); 

            reservaService.crear(user, idCoche, importeSenal, metodoPagoStr);

            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Reserva realizada correctamente"));
                             
	    } catch (IllegalArgumentException | IllegalStateException e) {
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
	        ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", e.getMessage()));
	    } catch (Exception e) {
	        ServletUtil.manejarError(response, e);
	    }
    }	
	
	private boolean esClienteLogueado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
        return usu != null;
    }

    private Usuario obtenerUsuario(HttpServletRequest request) {
        return (Usuario) request.getSession(false).getAttribute("usuarioLogueado");
    }

}
