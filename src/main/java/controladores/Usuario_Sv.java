package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import modelo.Usuario;
import servicio.UsuarioService;
import util.ServletUtil;
import java.util.Map;

import dto.UsuarioDTO;
import jakarta.servlet.http.Part;

 
/**
 * Servlet implementation class Usuario_Sv
 */
@WebServlet("/Usuario_Sv")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 2,
	    maxFileSize = 1024 * 1024 * 10,
	    maxRequestSize = 1024 * 1024 * 50
	)
public class Usuario_Sv extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UsuarioService usuarioService = UsuarioService.getInstance();
    
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
        } else if (accion.equals("eliminar")) {
            ejecutarEliminar(request, response);
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
        if ("crear".equals(accion)) {
            ejecutarCrear(request, response);
        } else if ("editar".equals(accion)) {
            ejecutarEditar(request, response);
        }
    }
    
    private void ejecutarCrear(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            Usuario logueado = (Usuario) request.getSession().getAttribute("usuarioLogueado");           
            Usuario u = ServletUtil.mapearRequestAUsuario(request);           

            Part imagenPart = request.getPart("foto");
            usuarioService.registrar(u, logueado, imagenPart); 
            
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Usuario creado correctamente"));
            
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    
    private void ejecutarListar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String busqueda = request.getParameter("busqueda");
            int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), 1);
            int porPagina = 8; 

            Map<String, Object> respuesta = usuarioService.listarPaginado(busqueda, pagina, porPagina);

            ServletUtil.enviarRespuesta(response, respuesta);
            
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    
    private void ejecutarDetalle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "id");

            UsuarioDTO u = usuarioService.obtener(id); 
            
            if (u == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Usuario no encontrado"));
                return;
            }

            ServletUtil.enviarRespuesta(response, u);
            
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    
    private void ejecutarEditar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "id");
            Usuario logueado = (Usuario) request.getSession().getAttribute("usuarioLogueado");          

            Usuario u = ServletUtil.mapearRequestAUsuario(request);
            u.setId_usuario(id);
            String fotoActual = request.getParameter("foto_actual");
            Part imagenPart = request.getPart("foto");
            usuarioService.actualizar(u, logueado, imagenPart, fotoActual);
            
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Usuario actualizado correctamente"));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    
    private void ejecutarEliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "id");
            Usuario logueado = (Usuario) request.getSession().getAttribute("usuarioLogueado");

            usuarioService.eliminar(id, logueado);
            
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Usuario eliminado"));
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
