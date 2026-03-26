package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import dao.ConsultasDAO;
import modelo.Consultas;
import util.EmailUtil;

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
		response.sendRedirect("contacto.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8"); 
	
		String nombre = request.getParameter("nombre");
		String email = request.getParameter("email");
		String mensaje = request.getParameter("mensaje");

		Consultas nuevaConsulta = new Consultas(nombre, email, mensaje);

		try {
  
			ConsultasDAO.getInstance().insertar(nuevaConsulta);
						
		    EmailUtil.enviarConfirmacion(nuevaConsulta); 

			request.setAttribute("mensaje", "¡Gracias " + nombre + "! Hemos recibido tu consulta correctamente.");
			
		} catch (Exception e) { 
			e.printStackTrace();
			request.setAttribute("error", "Lo sentimos, no hemos podido guardar tu mensaje.");
		}

		request.getRequestDispatcher("contacto.jsp").forward(request, response);
	}

}
