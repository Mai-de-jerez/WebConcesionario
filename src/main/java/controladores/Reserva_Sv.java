package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import modelo.Reserva;
import modelo.Usuario;
import servicio.ReservaService;
import util.ServletUtil;

@WebServlet(value = "/Reserva_Sv", loadOnStartup = 1)
public class Reserva_Sv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int POR_PAGINA = 6;
    private final ReservaService reservaService = ReservaService.getInstance(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!esAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Acceso denegado"));
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null || accion.equals("listar")) {
            ejecutarListar(request, response);
        } else if (accion.equals("detalle")) {
            ejecutarDetalle(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!esAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Acceso denegado"));
            return;
        }

        String accion = request.getParameter("accion");
        if ("completar".equals(accion)) {
            ejecutarCompletar(request, response);
        } else if ("cancelar".equals(accion)) {
            ejecutarCancelar(request, response);
        } else if ("editar".equals(accion)) {
            ejecutarEditar(request, response);
        } 
	}

   
    /**
     * Permite al admin listar los pedidos con filtros de búsqueda y paginación.
     * @param request contiene los parámetros: busqueda (opcional), estado (opcional), pagina (opcional)
     * @param response se envía un JSON con la lista de pedidos y datos de paginación
     * @throws ServletException si ocurre un error en el servlet
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void ejecutarListar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
        try { 
            String busqueda = request.getParameter("busqueda"); 
            String estadoParam = request.getParameter("estado"); 
            int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), "página"); 
            if (pagina < 1) pagina = 1;

            Map<String, Object> resultado = reservaService.ListarAdmin(busqueda, estadoParam, pagina, POR_PAGINA);
            
            ServletUtil.enviarRespuesta(response, resultado); 
        } catch (Exception e) { 
            ServletUtil.manejarError(response, e);   
        } 
    } 
    
    /**
     * Permite al admin ver el detalle de un pedido específico.
     * @param request contiene el parámetro: id (ID del pedido)
     * @param response se envía un JSON con los detalles del pedido
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void ejecutarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del pedido");
            Reserva rp = reservaService.obtenerPorId(id);
            ServletUtil.enviarRespuesta(response, rp);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    /**
     * Permite al admin marcar un pedido como completado.
     * @param request contiene el parámetro: id (ID del pedido)
     * @param response se envía un JSON con el resultado de la operación
     * @throws IOException si ocurre un error de entrada/salida
     */    
    private void ejecutarCompletar(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
         
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID de la reserva");
            reservaService.completar(id); 
       
            ServletUtil.enviarRespuesta(response, Map.of(
                "resultado", "OK", 
                "mensaje", "Venta finalizada con éxito. Registrada internamente como TRANSFERENCIA."
            ));

        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    /**
     * Permite al admin cancelar un pedido y devolver el coche a stock.
     * @param request contiene el parámetro: id (ID del pedido)
     * @param response se envía un JSON con el resultado de la operación
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void ejecutarCancelar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID de la Reserva");

            reservaService.cancelar(id); 
            
            ServletUtil.enviarRespuesta(response, Map.of(
                "resultado", "OK", 
                "mensaje", "Reserva cancelada correctamente"
            ));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    /**
     * Permite al admin editar las observaciones de un pedido.
     * @param request contiene los parámetros: id (ID del pedido), observaciones (nuevas observaciones)
     * @param response se envía un JSON con el resultado de la operación
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void ejecutarEditar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del pedido");

            String observaciones = ServletUtil.sanitizar(request.getParameter("observaciones"));
            reservaService.editar(id, observaciones); 

            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Observaciones actualizadas correctamente"));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    private boolean esAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
        return (usu != null && usu.getRol().getNivel() <= 2);
    }
}
