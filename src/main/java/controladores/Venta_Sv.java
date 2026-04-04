package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import modelo.Venta;
import servicio.VentaService;
import util.ServletUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Servlet implementation class Venta_Sv
 */
@WebServlet("/Venta_Sv")
public class Venta_Sv extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int POR_PAGINA = 6;
    private final VentaService ventaService = VentaService.getInstance(); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Venta_Sv() {
        super();   
        // TODO Auto-generated constructor stub
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
        } else if ("cancelar".equals(accion)) {
            ejecutarCancelar(request, response);
        } else if ("eliminar".equals(accion)) { 
            ejecutarEliminar(request, response);
        }
    }
    
    private void ejecutarListar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Recogemos parámetros de búsqueda y paginación
            String busqueda = request.getParameter("busqueda");
            String estado = request.getParameter("estado");
            int pagina = ServletUtil.parsearInt(request.getParameter("pagina"), "Página");
            if (pagina <= 0) pagina = 1;

            // Llamamos al service
            Map<String, Object> resultado = ventaService.ListarAdmin(busqueda, estado, pagina, POR_PAGINA);

            // Enviamos la respuesta JSON al frontend
            ServletUtil.enviarRespuesta(response, resultado);
            
        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    
    private void ejecutarCrear(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            int idCoche = ServletUtil.parsearInt(request.getParameter("idCoche"), "ID del coche");
            double importeAbonado = ServletUtil.parsearDouble(request.getParameter("importeAbonado"));           

            // creamos el objeto Usuario con los datos del formulario
            Usuario nuevoCliente = new Usuario();
            nuevoCliente.setUsuario(request.getParameter("usuario")); // El login/username
            nuevoCliente.setEmail(request.getParameter("email"));
            nuevoCliente.setNombre(request.getParameter("nombre"));
            nuevoCliente.setApellidos(request.getParameter("apellidos"));
            nuevoCliente.setTelefono(request.getParameter("telefono"));
            nuevoCliente.setDireccion(request.getParameter("direccion"));

            ventaService.crearVenta(nuevoCliente, idCoche, importeAbonado);

            ServletUtil.enviarRespuesta(response, Map.of(
                "resultado", "OK",
                "mensaje", "Cliente registrado y venta finalizada con éxito."
            ));
            
        } catch (NumberFormatException e) {
            ServletUtil.manejarError(response, new Exception("El ID del coche o el importe no son válidos."));
        } catch (Exception e) {

            ServletUtil.manejarError(response, e);
        }
    }
    
    private void ejecutarDetalle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID de venta");
            Venta v = ventaService.obtenerPorId(id);

            if (v == null) {
                throw new Exception("La venta solicitada no existe.");
            }

            ServletUtil.enviarRespuesta(response, Map.of(
                "resultado", "OK",
                "venta", v
            ));

        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    private void ejecutarCancelar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID de venta");
            
            ventaService.cancelar(id);

            ServletUtil.enviarRespuesta(response, Map.of(
                "resultado", "OK",
                "mensaje", "La venta ha sido cancelada y el vehículo liberado."
            ));

        } catch (Exception e) {
            ServletUtil.manejarError(response, e);
        }
    }
    
    private void ejecutarEliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.parsearInt(request.getParameter("id"), "ID de venta");
            
            ventaService.borradoPermanenteVenta(id);

            ServletUtil.enviarRespuesta(response, Map.of(
                "resultado", "OK",
                "mensaje", "Venta eliminada permanentemente del sistema."
            ));

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
