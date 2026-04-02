package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import modelo.EstadoPedido;
import modelo.ReservaPedido;
import modelo.Usuario;
import servicio.ReservaPedidoService;
import util.ServletUtil;

@WebServlet(value = "/Pedido_Sv", loadOnStartup = 1)
public class Pedido_Sv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int POR_PAGINA = 6;
    private final ReservaPedidoService pedidoService = ReservaPedidoService.getInstance();

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
        } else if ("crear".equals(accion)) { 
        	ejecutarCrear(request, response);
	    }
	}
     
    
    private void ejecutarCrear(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String email = request.getParameter("email");
            int idCoche = ServletUtil.parsearInt(request.getParameter("idCoche"), "ID del coche");
            double importeTotal = ServletUtil.parsearDouble(request.getParameter("importeTotal"));
            String fechaPagoStr = request.getParameter("fechaPago");
            LocalDateTime fechaPago = fechaPagoStr != null && !fechaPagoStr.isBlank() 
                ? LocalDateTime.parse(fechaPagoStr) 
                : LocalDateTime.now();
 
            pedidoService.crearVentaDirecta(email, idCoche, importeTotal, fechaPago);

            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Venta creada correctamente"));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    } 
   
   
	
    private void ejecutarListar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		  try { 
			  String busqueda = request.getParameter("busqueda"); 
			  String estadoParam = request.getParameter("estado"); 
			  EstadoPedido estado = null;
			  
			  if (estadoParam != null && !estadoParam.isBlank()) { 
				  estado = EstadoPedido.desdeTexto(estadoParam); 
			  }
			  
			  int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), "página"); 
			  if (pagina < 1) pagina = 1;
			  List<ReservaPedido> pedidos = pedidoService.listarAdmin(busqueda, estado, pagina, POR_PAGINA); 
			  long total = pedidoService.contarAdmin(busqueda, estado); 
			  int totalPaginas = (int) Math.ceil((double) total / POR_PAGINA);
			  ServletUtil.enviarRespuesta(response, Map.of( "pedidos", pedidos, "totalPaginas", totalPaginas, "paginaActual", pagina )); 
		 } catch (Exception e) { 
			 ServletUtil.manejarError(response, e); 
		 } 
	}
	  

    private void ejecutarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del pedido");
            ReservaPedido rp = pedidoService.obtenerPorId(id);
            ServletUtil.enviarRespuesta(response, rp);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    private void ejecutarCompletar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {

            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del pedido");
            pedidoService.completar(id); 
            
            ServletUtil.enviarRespuesta(response, Map.of(
                "resultado", "OK", 
                "mensaje", "Pedido completado correctamente"
            ));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    private void ejecutarCancelar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del pedido");

            pedidoService.cancelar(id); 
            
            ServletUtil.enviarRespuesta(response, Map.of(
                "resultado", "OK", 
                "mensaje", "Pedido cancelado correctamente"
            ));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    
    private void ejecutarEditar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del pedido");

            String observaciones = ServletUtil.sanitizar(request.getParameter("observaciones"));
            pedidoService.editar(id, observaciones); 

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
