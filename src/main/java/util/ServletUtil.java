package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Rol;
import modelo.Usuario;

public class ServletUtil {
    
	private static final Gson gson = new GsonBuilder()
		    .registerTypeAdapter(LocalDateTime.class,
		        (JsonSerializer<LocalDateTime>)
		        (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
		    .create();

    // Tu método estrella: Limpia etiquetas HTML para evitar ataques XSS básicos
    public static String sanitizar(String texto) {
        if (texto == null) return "";
        return texto.replaceAll("<[^>]*>", "").trim();
    }

    public static double parsearDouble(String valor) {
        try {
            return (valor == null || valor.isEmpty()) ? 0 : Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número decimal válido.");
        }
    }

    public static int parsearInt(String valor, String campo) {
        try {
            return (valor == null || valor.isEmpty()) ? 0 : Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser un número entero.");
        }
    }

    // Sobrecarga para paginación (tu lógica de libros, muy útil)
    public static int parsearInt(String valor, int defecto) {
        try {
            int v = (valor == null || valor.isEmpty()) ? defecto : Integer.parseInt(valor);
            return v < 1 ? defecto : v;
        } catch (NumberFormatException e) {
            return defecto;
        }
    }

    /**
     * NUEVO: Validación de Kilómetros. 
     * Un coche no puede tener km negativos y, para tu negocio, quizá no aceptes más de 1.000.000
     */
    public static int validarKm(String kmStr) {
        int km = parsearInt(kmStr, "kilómetros");
        if (km < 0) throw new IllegalArgumentException("Los kilómetros no pueden ser negativos.");
        return km;
    }

    /**
     * Centraliza el manejo de errores enviando el status HTTP correcto y JSON
     */
    public static void manejarError(HttpServletResponse response, Exception e) {
        e.printStackTrace(); 
        
        int status = (e instanceof IllegalArgumentException) 
                     ? HttpServletResponse.SC_BAD_REQUEST 
                     : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        
        response.setStatus(status);
        
        try {
            enviarRespuesta(response, Map.of(
                "resultado", "ERROR",
                "mensaje", (e.getMessage() != null ? e.getMessage() : "Error inesperado en el sistema")
            ));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    /**
     * Convierte cualquier objeto a JSON y lo escupe al navegador
     */
    public static void enviarRespuesta(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(data));
            out.flush(); // Aseguramos que se envíe todo
        }
    }
    
    // Método para validar que un usuario esté logueado y obtener su objeto Usuario
    public static Usuario mapearRequestAUsuario(HttpServletRequest request) {
        Usuario u = new Usuario();
        u.setUsuario(sanitizar(request.getParameter("usuario")));
        u.setNombre(sanitizar(request.getParameter("nombre")));
        u.setApellidos(sanitizar(request.getParameter("apellidos")));
        u.setEmail(sanitizar(request.getParameter("email")));
        u.setTelefono(sanitizar(request.getParameter("telefono")));
        u.setDireccion(sanitizar(request.getParameter("direccion")));
        u.setPassword(request.getParameter("password")); 
        
        String rolParam = request.getParameter("rol");
        if (rolParam != null && !rolParam.isBlank()) {
            try {
                u.setRol(Rol.valueOf(rolParam));
            } catch (Exception e) {
            }
        }
        return u;
    }
}
