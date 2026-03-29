package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Coche;
import servicio.CocheService;
import util.ServletUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet de la Tienda de Vehículos
 */
@WebServlet("/Tienda")
public class Tienda extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CocheService cocheService = new CocheService();

    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String accion = request.getParameter("accion");
            
            if ("detalle".equals(accion)) {
                int id = ServletUtil.parsearInt(request.getParameter("id"), "id");
                Coche coche = cocheService.obtenerCoche(id);
                ServletUtil.enviarRespuesta(response, coche);
                return;
            }

            String busqueda = request.getParameter("busqueda");
            int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), "página");
            int porPagina = 8;
            if (pagina < 1) pagina = 1;
            List<Coche> lista = cocheService.listarParaTienda(busqueda, pagina, porPagina);
            long total = cocheService.totalCochesTienda(busqueda);
            int totalPaginas = (int) Math.ceil((double) total / porPagina);
            Map<String, Object> respuesta = Map.of(
                "coches", lista,
                "totalPaginas", totalPaginas,
                "paginaActual", pagina
            );
            ServletUtil.enviarRespuesta(response, respuesta);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}