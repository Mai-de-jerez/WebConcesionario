package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import modelo.Coche;
import modelo.TipoMotor;
import modelo.EstadoVehiculo;
import util.ImagenUtil;
import util.ServletUtil;
import servicio.CocheService;

@WebServlet(value = "/Coche_Sv", loadOnStartup = 1)
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class Coche_Sv extends HttpServlet {

    private final CocheService cocheService = new CocheService();
    private static final long serialVersionUID = 1L;
    
    // Adaptado a tu estructura de carpetas
    private static final String PATH_IMAGENES = "C:\\Users\\carme\\Proyectos_Java\\WebConcesionario\\src\\main\\webapp\\img";
    private static final String IMG_DEFECTO = "coche-defecto.png";

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
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "No tienes permisos de escritura"));
            return;
        }
    	
    	
        String accion = request.getParameter("accion");
        if ("alta".equals(accion)) {
            ejecutarAlta(request, response);
        } else if ("editar".equals(accion)) {
            ejecutarEditar(request, response);
        }
    }

    private void ejecutarListar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String busqueda = request.getParameter("busqueda");
            int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), "página");
            int porPagina = 6; // Olvídate de parsear esto, déjalo fijo a 6 para no liarla

            if (pagina < 1) pagina = 1;

            // 1. La lista de coches (esto está bien)
            List<Coche> lista = cocheService.listarParaAdmin(busqueda, pagina, porPagina);
            
            // 2. El total de COCHES (p.ej. 12)
            long totalCoches = cocheService.totalCochesAdmin(busqueda);
            
            // 3. LA CUENTA DE LAS PÁGINAS (Lo que te falta, leñe)
            // Dividimos coches entre 6 y redondeamos hacia arriba
            int totalPaginas = (int) Math.ceil((double) totalCoches / porPagina);

            // 4. El Mapa con los nombres que tu JS espera
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

    private void ejecutarDetalle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del coche");
            Coche coche = cocheService.obtenerCoche(id);
            ServletUtil.enviarRespuesta(response, coche);
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    private void ejecutarAlta(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String nombreImagenUnico = IMG_DEFECTO;
        try {
            Coche c = mapearRequestACoche(request);
            
            Part imagenPart = request.getPart("imagen");
            if (imagenPart != null && imagenPart.getSize() > 0) {
                nombreImagenUnico = ImagenUtil.guardarArchivo(imagenPart, PATH_IMAGENES, IMG_DEFECTO);
                c.setImagen(nombreImagenUnico);
            } else {
                c.setImagen(IMG_DEFECTO);
            }

            cocheService.guardarCoche(c);
            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Coche creado correctamente"));
            
        } catch (Exception e) {
            if (!nombreImagenUnico.equals(IMG_DEFECTO)) {
                ImagenUtil.borrarArchivo(nombreImagenUnico, PATH_IMAGENES, IMG_DEFECTO);
            }
            ServletUtil.manejarError(response, e);
        }
    }

    private void ejecutarEditar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String nombreImagenNueva = null;
        try {
            Coche c = mapearRequestACoche(request);
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del coche");
            c.setId(id);
            
            String imagenActual = request.getParameter("imagen_actual");
            Part imagenPart = request.getPart("imagen");
            
            if (imagenPart != null && imagenPart.getSize() > 0) {
                nombreImagenNueva = ImagenUtil.guardarArchivo(imagenPart, PATH_IMAGENES, IMG_DEFECTO);
                c.setImagen(nombreImagenNueva);
            } else {
                c.setImagen(imagenActual);
            }

            cocheService.guardarCoche(c);

            if (nombreImagenNueva != null && imagenActual != null && !imagenActual.equals(IMG_DEFECTO)) {
                ImagenUtil.borrarArchivo(imagenActual, PATH_IMAGENES, IMG_DEFECTO);
            }

            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Coche actualizado"));
        } catch (Exception e) {
            if (nombreImagenNueva != null) ImagenUtil.borrarArchivo(nombreImagenNueva, PATH_IMAGENES, IMG_DEFECTO);
            ServletUtil.manejarError(response, e);
        }
    }

    private void ejecutarEliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del coche");
            Coche coche = cocheService.obtenerCoche(id);
            String imagenABorrar = coche.getImagen();

            cocheService.eliminarCoche(id);
            
            if (imagenABorrar != null && !imagenABorrar.equals(IMG_DEFECTO)) {
                ImagenUtil.borrarArchivo(imagenABorrar, PATH_IMAGENES, IMG_DEFECTO);
            }

            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Coche eliminado"));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }

    // Helper para no repetir código en alta y editar
    private Coche mapearRequestACoche(HttpServletRequest request) throws Exception {
        Coche c = new Coche();
        c.setMarca(ServletUtil.sanitizar(request.getParameter("marca")));
        c.setModelo(ServletUtil.sanitizar(request.getParameter("modelo")));
        c.setMatricula(ServletUtil.sanitizar(request.getParameter("matricula")));
        c.setPrecio(ServletUtil.parsearDouble(request.getParameter("precio")));
        c.setColor(ServletUtil.sanitizar(request.getParameter("color")));
        c.setKm(ServletUtil.parsearInt(request.getParameter("km"), "km"));
        c.setAnio(request.getParameter("anio"));
        c.setTipoMotor(TipoMotor.valueOf(request.getParameter("tipoMotor")));
        c.setEstado(EstadoVehiculo.valueOf(request.getParameter("estado")));
        c.setDescripcion(ServletUtil.sanitizar(request.getParameter("descripcion")));
        return c;
    }
    
    
    private boolean esAdmin(HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        modelo.Usuario usu = (modelo.Usuario) session.getAttribute("usuarioLogueado");

        return (usu != null && usu.getRol().getNivel() <= 2);
    }
}