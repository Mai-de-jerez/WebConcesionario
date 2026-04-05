package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import dto.CocheDTO;
import servicio.CocheService;
import util.ServletUtil;

@WebServlet(value = "/Tienda_Sv", loadOnStartup = 1)
public class Tienda_Sv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int POR_PAGINA = 8;
    private final CocheService cocheService = CocheService.getInstance();
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if ("novedades".equals(accion)) {
            ejecutarNovedades(request, response);
        } else if (accion == null || accion.equals("listar")) {
            ejecutarListar(request, response);
        } else if (accion.equals("detalle")) {
            ejecutarDetalle(request, response);
        }
    }

    private void ejecutarListar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String busqueda = request.getParameter("busqueda");
            int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), "página");
            if (pagina < 1) pagina = 1;

            List<CocheDTO> lista = cocheService.listarParaTienda(busqueda, pagina, POR_PAGINA);
            long total = cocheService.totalCochesTienda(busqueda);
            int totalPaginas = (int) Math.ceil((double) total / POR_PAGINA);

            ServletUtil.enviarRespuesta(response, Map.of(
                "coches", lista,
                "totalPaginas", totalPaginas,
                "paginaActual", pagina
            ));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    private void ejecutarNovedades(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            List<CocheDTO> novedades = cocheService.obtenerNovedades();
       
            ServletUtil.enviarRespuesta(response, novedades);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    private void ejecutarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del coche");
            CocheDTO coche = cocheService.obtenerCoche(id);
            ServletUtil.enviarRespuesta(response, coche);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

}
