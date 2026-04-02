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
import java.util.Map;
import modelo.Coche;
import modelo.Usuario;
import util.ServletUtil;
import servicio.CocheService;

@WebServlet(value = "/Coche_Sv", loadOnStartup = 1)
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  
    maxFileSize = 1024 * 1024 * 10,      
    maxRequestSize = 1024 * 1024 * 50     
)
public class Coche_Sv extends HttpServlet {

    private final CocheService cocheService = CocheService.getInstance();
    private static final long serialVersionUID = 1L;


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
            int porPagina = 6; 

            Map<String, Object> respuesta = cocheService.listarParaAdmin(busqueda, pagina, porPagina);

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
        try {
 
            Coche c = ServletUtil.mapearRequestACoche(request);
   
            Part imagenPart = request.getPart("imagen");

            cocheService.guardarCoche(c, imagenPart);

            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Coche creado correctamente"));
            
        } catch (Exception e) {

            ServletUtil.manejarError(response, e);
        }
    }

    
    private void ejecutarEditar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
    
            Coche c = ServletUtil.mapearRequestACoche(request);
            c.setId(ServletUtil.parsearInt(request.getParameter("id"), "ID del coche"));
      
            String imagenActual = request.getParameter("imagen_actual");
            Part imagenPart = request.getPart("imagen");
            // El Service se encarga 
            cocheService.guardarCoche(c, imagenPart, imagenActual);

            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Coche actualizado"));
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    
    private void ejecutarEliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID del coche");

            cocheService.eliminarCoche(id);

            ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "Coche eliminado"));
            
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