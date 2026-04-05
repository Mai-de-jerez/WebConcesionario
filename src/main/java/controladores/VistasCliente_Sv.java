package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class VistasCliente_Sv
 */
@WebServlet("/clientes")
public class VistasCliente_Sv extends HttpServlet {
	private static final long serialVersionUID = 1L;
  

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String vista = request.getParameter("vista");
        if (vista == null) vista = "perfil";
        
        switch (vista) {
            case "checkout":
            	// Pillamos el idCoche de la URL y lo metemos en el "equipaje" (request)
                String idCoche = request.getParameter("idCoche");
                request.setAttribute("idCocheSeleccionado", idCoche); 
                request.getRequestDispatcher("/WEB-INF/clientes/checkout.html").forward(request, response);
                break;
            case "perfil":
                request.getRequestDispatcher("/WEB-INF/clientes/perfil.html").forward(request, response);
                break;
            case "editar-perfil":
                request.getRequestDispatcher("/WEB-INF/clientes/editar-perfil.html").forward(request, response);
                break;
            case "confirmacion":
                request.getRequestDispatcher("/WEB-INF/clientes/confirmacion_reserva.html").forward(request, response);
                break;
            default:
                // Si la vista no existe, lo mandamos al index público
                response.sendRedirect("index.html");
                break;
        }
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
