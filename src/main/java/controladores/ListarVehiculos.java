package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Coche;
import java.io.IOException;
import java.util.List;


/**
 * Servlet implementation class ListarVehiculos
 */
@WebServlet("/ListarVehiculos")
public class ListarVehiculos extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListarVehiculos() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    try {
	    	
	        List<Coche> lista = dao.CocheDAO.getInstance().listar(); 

	        request.setAttribute("listaCoches", lista);
	
	        request.getRequestDispatcher("listar-vehiculos.jsp").forward(request, response); 

	    } catch (Exception e) {

	        e.printStackTrace();
	        request.setAttribute("error", "Error al cargar el inventario: " + e.getMessage());
	        request.getRequestDispatcher("index.jsp").forward(request, response);
	    }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
