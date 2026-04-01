package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Consultas;
import modelo.Usuario;
import util.EmailUtil;
import util.ServletUtil;

import java.io.IOException;
import java.util.Map;

import dao.ConsultasDAO;

/**
 * Servlet implementation class EnviarMensaje
 */
@WebServlet("/EnviarMensaje")
public class EnviarMensaje extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EnviarMensaje() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.sendRedirect("contacto.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    request.setCharacterEncoding("UTF-8");
	    
	    // Pillamos lo que viene del formulario
	    String nombre = request.getParameter("nombre");
	    String email = request.getParameter("email");
	    String mensaje = request.getParameter("mensaje");


	    // Intentamos ver si hay sesión y si el usuario está logueado para priorizar esos datos
	    HttpSession session = request.getSession(false);
	    if (session != null && session.getAttribute("usuarioLogueado") != null) {
	        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
	        // si cliente logueado, priorizamos sus datos de la sesión
	        nombre = u.getNombre();
	        email = u.getEmail();
	    }

	    // 3. Validación de seguridad para que JPA no pete
	    if (nombre == null || email == null || mensaje == null || 
	        nombre.trim().isEmpty() || email.trim().isEmpty() || mensaje.trim().isEmpty()) {
	        
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        ServletUtil.enviarRespuesta(response, Map.of("resultado", "ERROR", "mensaje", "Por favor, rellena todos los campos."));
	        return;
	    }

	    try {
	        // Creamos el objeto
	        Consultas nuevaConsulta = new Consultas(nombre, email, mensaje);

	        // Si hay sesión, vinculamos el objeto Usuario a la consulta
	        if (session != null && session.getAttribute("usuarioLogueado") != null) {
	            Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
	            nuevaConsulta.setUsuario(u); 
	        } 

	        ConsultasDAO.getInstance().insertar(nuevaConsulta);
	
	        EmailUtil.enviarConfirmacion(nuevaConsulta);

	        ServletUtil.enviarRespuesta(response, Map.of("resultado", "OK", "mensaje", "¡Gracias " + nombre + "! Mensaje enviado correctamente."));
	        
	        System.out.println("✅ Consulta guardada en BD: " + nombre + (nuevaConsulta.getUsuario() != null ? " (Usuario ID: " + nuevaConsulta.getUsuario().getId_usuario() + ")" : " (Invitado)"));
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        ServletUtil.manejarError(response, e); 
	    }
	}
}
