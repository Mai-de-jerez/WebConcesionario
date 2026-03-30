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
import modelo.ReservaPedido;
import modelo.Usuario;
import servicio.ReservaPedidoService;
import util.ServletUtil;

@WebServlet(value = "/Cliente_Sv", loadOnStartup = 1)
public class Cliente_Sv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int POR_PAGINA = 10;
    private final ReservaPedidoService pedidoService = ReservaPedidoService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!esCliente(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Acceso denegado"));
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null || accion.equals("listar")) {
            ejecutarListar(request, response);
        } else if (accion.equals("detalle")) {
            ejecutarDetalle(request, response);
        } else if (accion.equals("perfil")) {
            ejecutarPerfil(request, response);
        }
        
    }

    private void ejecutarListar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Usuario user = obtenerUsuario(request);
            int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), "página");
            if (pagina < 1) pagina = 1;

            List<ReservaPedido> pedidos = pedidoService.listarPorUsuario(user.getId_usuario(), pagina, POR_PAGINA);
            long total = pedidoService.contarPorUsuario(user.getId_usuario());
            int totalPaginas = (int) Math.ceil((double) total / POR_PAGINA);

            ServletUtil.enviarRespuesta(response, Map.of(
                "pedidos", pedidos,
                "totalPaginas", totalPaginas,
                "paginaActual", pagina
            ));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    private void ejecutarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Usuario user = obtenerUsuario(request);
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del pedido");
            ReservaPedido rp = pedidoService.obtenerPorId(id);

            // Seguridad: el cliente solo puede ver sus propios pedidos
            if (rp == null || rp.getUsuario() == null || rp.getUsuario().getId_usuario() != user.getId_usuario()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Acceso denegado"));
                return;
            }

            ServletUtil.enviarRespuesta(response, rp);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    private void ejecutarPerfil(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            ServletUtil.enviarRespuesta(response, obtenerUsuario(request));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    private boolean esCliente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
        return (usu != null && usu.getRol().getNivel() == 3);
    }

    private Usuario obtenerUsuario(HttpServletRequest request) {
        return (Usuario) request.getSession(false).getAttribute("usuarioLogueado");
    }
}
