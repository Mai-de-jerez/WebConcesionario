package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import modelo.Coche;

/**
 * Servlet implementation class DetalleCocheServlet
 */
@WebServlet("/DetalleCocheServlet")
public class DetalleCocheServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DetalleCocheServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    // 1. Recuperamos el ID
	    String idStr = request.getParameter("id");    
	    // 2. Capturamos si viene el parámetro admin
	    String adminParam = request.getParameter("admin");
	    boolean esModoAdmin = "true".equals(adminParam);
	    
	    if (idStr != null) {
	        try {
	            int id = Integer.parseInt(idStr);

	            // CAMBIO AQUÍ: Llamamos al DAO en lugar de a la clase Coche
	            Coche coche = dao.CocheDAO.getInstance().obtenerPorId(id); 

	            if (coche != null) {
	                request.setAttribute("cocheDetalle", coche);
	                request.setAttribute("modoAdmin", esModoAdmin);
	                request.getRequestDispatcher("detalle_coche.html").forward(request, response);
	            } else {
	                response.sendRedirect(esModoAdmin ? "Coche_Sv?accion=listar" : "Tienda");
	            }
	            
	        } catch (Exception e) { 
	            e.printStackTrace();
	            response.sendRedirect("Tienda");
	        }
	    } else {
	        response.sendRedirect("Tienda");
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
