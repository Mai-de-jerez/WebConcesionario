package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import modelo.Reserva;
import modelo.Usuario;
import servicio.ReservaService;
import servicio.UsuarioService;
import util.ServletUtil;

@WebServlet(value = "/Cliente_Sv", loadOnStartup = 1)
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 2,
	    maxFileSize = 1024 * 1024 * 10,
	    maxRequestSize = 1024 * 1024 * 50
	)
public class Cliente_Sv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int POR_PAGINA = 10;
    private final ReservaService pedidoService = ReservaService.getInstance();
    private final UsuarioService usuarioService = new UsuarioService();

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
            ejecutarListarMisPedidos(request, response);
        } else if (accion.equals("detalle")) {
            ejecutarDetallePedido(request, response);
        } else if (accion.equals("perfil")) {
            ejecutarVerMiPerfil(request, response);
        } 
        
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!esCliente(request)) { /* error 403 */ return; }

        String accion = request.getParameter("accion");

        if ("editar_mi_perfil".equals(accion)) {
            ejecutarEditarMiPerfil(request, response);
        }
    }



    private void ejecutarListarMisPedidos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Usuario user = obtenerUsuario(request);
            int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), "página");
            if (pagina < 1) pagina = 1;

            List<Reserva> pedidos = pedidoService.listarPorUsuario(user.getId_usuario(), pagina, POR_PAGINA);
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

    private void ejecutarDetallePedido(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Usuario user = obtenerUsuario(request);
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del pedido");
            Reserva rp = pedidoService.obtenerPorId(id);

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
    
    private void ejecutarVerMiPerfil(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            ServletUtil.enviarRespuesta(response, obtenerUsuario(request));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    private void ejecutarEditarMiPerfil(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
        	// Primero obtenemos al usuario logueado 
            Usuario logueado = (Usuario) request.getSession().getAttribute("usuarioLogueado");
            // Mapeamos los datos editados del usuario
            Usuario u = ServletUtil.mapearRequestAUsuario(request);     
            u.setId_usuario(logueado.getId_usuario());      
            // Recuperamos los datos de la foto
            String fotoActual = request.getParameter("foto_actual");
            Part imagenPart = request.getPart("foto"); 
            
            usuarioService.actualizar(u, logueado, imagenPart, fotoActual);

            request.getSession().setAttribute("usuarioLogueado", usuarioService.obtener(logueado.getId_usuario()));
            
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Perfil actualizado correctamente"));
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
