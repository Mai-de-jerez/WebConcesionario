package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class VistasAdmin_Sv
 */
@WebServlet("/admin")
public class VistasAdmin_Sv extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
  

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String vista = request.getParameter("vista");
        if (vista == null) vista = "panel";
        
        switch (vista) { 
	        case "coches":
	            request.getRequestDispatcher("/WEB-INF/admin/coche_admin.html").forward(request, response);
	            break;
	        case "form-vehiculo":
	            request.getRequestDispatcher("/WEB-INF/admin/form-vehiculo.html").forward(request, response);
	            break;
	        case "detalle-coche":
	            request.getRequestDispatcher("/WEB-INF/admin/detalle_coche_admin.html").forward(request, response);
	            break;
	        case "usuarios":
	            request.getRequestDispatcher("/WEB-INF/admin/usuario_admin.html").forward(request, response);
	            break;
	        case "detalle-usuario":
	            request.getRequestDispatcher("/WEB-INF/admin/detalle_usuario_admin.html").forward(request, response);
	            break;
	        case "form-usuario":
	            request.getRequestDispatcher("/WEB-INF/admin/form-usuario.html").forward(request, response);
	            break;
	        case "reservas":
	            request.getRequestDispatcher("/WEB-INF/admin/reserva_admin.html").forward(request, response);
	            break;
	        case "detalle-reserva":
	            request.getRequestDispatcher("/WEB-INF/admin/detalle_reserva_admin.html").forward(request, response);
	            break;
	        case "form-reserva":
	            request.getRequestDispatcher("/WEB-INF/admin/form-reserva.html").forward(request, response);
	            break;
	        default:
	            request.getRequestDispatcher("/WEB-INF/admin/admin-panel.html").forward(request, response);
	            break;
        }
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}
