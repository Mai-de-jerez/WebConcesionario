package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import modelo.Coche;
import modelo.Usuario;
import servicio.ReservaPedidoService;
import servicio.CocheService;
import util.ServletUtil;

@WebServlet(value = "/Tienda_Sv", loadOnStartup = 1)
public class Tienda_Sv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int POR_PAGINA = 8;
    private final CocheService cocheService = CocheService.getInstance();
    private final ReservaPedidoService pedidoService = ReservaPedidoService.getInstance();

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
        } else if (accion.equals("checkout")) {
            ejecutarCheckout(request, response);
        } else if (accion.equals("confirmacion")) {
            ejecutarConfirmacion(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if ("reservar".equals(accion)) {
            ejecutarReservar(request, response);
        }
    }

    private void ejecutarListar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String busqueda = request.getParameter("busqueda");
            int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), "página");
            if (pagina < 1) pagina = 1;

            List<Coche> lista = cocheService.listarParaTienda(busqueda, pagina, POR_PAGINA);
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
            List<Coche> novedades = cocheService.obtenerNovedades();
       
            ServletUtil.enviarRespuesta(response, novedades);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    private void ejecutarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del coche");
            Coche coche = cocheService.obtenerCoche(id);
            ServletUtil.enviarRespuesta(response, coche);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    private void ejecutarCheckout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usu = session != null ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (usu == null) {
            String idCoche = request.getParameter("idCoche");
            response.sendRedirect("login.html?error=nosesion&idRegreso=" + idCoche);
            return;
        }

        request.getRequestDispatcher("checkout.html").forward(request, response);
    }

    private void ejecutarConfirmacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usu = session != null ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (usu == null) {
            response.sendRedirect("login.html");
            return;
        }

        request.getRequestDispatcher("confirmacion_reserva.html").forward(request, response);
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

            Coche coche = cocheService.obtenerCoche(idCoche);
            pedidoService.crear(user, coche, importeSenal);

            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Reserva realizada correctamente"));
                       
        } catch (IllegalStateException | IllegalArgumentException e) {

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
