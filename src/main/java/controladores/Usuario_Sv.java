package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import modelo.Rol;
import modelo.Usuario;
import servicio.UsuarioService;
import util.ServletUtil;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.Part;
import util.ImagenUtil;
 
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
    private final UsuarioService usuarioService = new UsuarioService();
    private static final String PATH_IMAGENES = "C:\\Users\\carme\\Proyectos_Java\\WebConcesionario\\src\\main\\webapp\\img";
    private static final String IMG_DEFECTO = "sin-foto.png";
    
    @Override
    public void init() throws ServletException {
        ImagenUtil.validarDirectorio(PATH_IMAGENES);
    }
    
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
            Usuario u = mapearRequestAUsuario(request);
            Part imagenPart = request.getPart("foto");
            if (imagenPart != null && imagenPart.getSize() > 0) {
                u.setFoto(ImagenUtil.guardarArchivo(imagenPart, PATH_IMAGENES, IMG_DEFECTO));
            } else {
                u.setFoto(IMG_DEFECTO);
            }
            usuarioService.registrar(u);
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
            if (pagina < 1) pagina = 1;

            List<Usuario> lista = usuarioService.listar(busqueda, pagina, porPagina);
            long total = usuarioService.total(busqueda);
            int totalPaginas = (int) Math.ceil((double) total / porPagina);

            ServletUtil.enviarRespuesta(response, Map.of(
                "usuarios", lista,
                "totalPaginas", totalPaginas,
                "paginaActual", pagina
            ));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    private void ejecutarDetalle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "id");
            Usuario u = usuarioService.obtener(id);
            if (u == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Usuario no encontrado"));
                return;
            }
            u.setPassword(null); // nunca enviamos la password al frontend
            ServletUtil.enviarRespuesta(response, u);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    
    private void ejecutarEditar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "id");
            Usuario u = mapearRequestAUsuario(request);
            u.setId_usuario(id);
            String fotoActual = request.getParameter("foto_actual");
            Part imagenPart = request.getPart("foto");
            if (imagenPart != null && imagenPart.getSize() > 0) {
                String nuevaFoto = ImagenUtil.guardarArchivo(imagenPart, PATH_IMAGENES, IMG_DEFECTO);
                u.setFoto(nuevaFoto);
                if (fotoActual != null && !fotoActual.equals(IMG_DEFECTO)) {
                    ImagenUtil.borrarArchivo(fotoActual, PATH_IMAGENES, IMG_DEFECTO);
                }
            } else {
                u.setFoto(fotoActual);
            }
            usuarioService.actualizar(u);
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Usuario actualizado correctamente"));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    private void ejecutarEliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "id");

            Usuario logueado = (Usuario) request.getSession().getAttribute("usuarioLogueado");
            if (logueado.getId_usuario() == id) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "No puedes eliminar tu propia cuenta"));
                return;
            }
            
            usuarioService.eliminar(id);
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Usuario eliminado correctamente"));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    private Usuario mapearRequestAUsuario(HttpServletRequest request) {
        Usuario u = new Usuario();
        u.setUsuario(ServletUtil.sanitizar(request.getParameter("usuario")));
        u.setNombre(ServletUtil.sanitizar(request.getParameter("nombre")));
        u.setApellidos(ServletUtil.sanitizar(request.getParameter("apellidos")));
        u.setEmail(ServletUtil.sanitizar(request.getParameter("email")));
        u.setTelefono(ServletUtil.sanitizar(request.getParameter("telefono")));
        u.setDireccion(ServletUtil.sanitizar(request.getParameter("direccion")));
        u.setPassword(request.getParameter("password"));
        String rolParam = request.getParameter("rol");
        if (rolParam != null && !rolParam.isBlank()) {
            u.setRol(Rol.valueOf(rolParam));
        }
        return u;
    }

    private boolean esAdmin(HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null) return false;
        modelo.Usuario usu = (modelo.Usuario) session.getAttribute("usuarioLogueado");
        return (usu != null && usu.getRol().getNivel() <= 2);
    }
}
