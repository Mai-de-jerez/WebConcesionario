package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Coche;
import modelo.Usuario;
import java.io.IOException;

import dao.CocheDAO;



/**
 * Servlet implementation class Carrito
 */
@WebServlet("/Carrito")
public class Carrito extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Carrito() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    HttpSession session = request.getSession();
	    Usuario user = (Usuario) session.getAttribute("usuarioLogueado");

	    if (user == null) {
	        response.sendRedirect("login.jsp");
	        return;
	    }

	    try {
	        int idCoche = Integer.parseInt(request.getParameter("idCoche"));
	        Coche coche = CocheDAO.getInstance().obtenerPorId(idCoche);

	        if (coche != null && coche.isDisponible()) {

	            request.setAttribute("idCoche", idCoche);
	            request.setAttribute("marcaCoche", coche.getMarca());
	            request.setAttribute("modeloCoche", coche.getModelo());
	            request.setAttribute("precioCoche", coche.getPrecio());
	            request.setAttribute("idUsuario", user.getId_usuario());
	            
	            request.getRequestDispatcher("/carrito.jsp").forward(request, response);
	        } else {
	            response.sendRedirect("Tienda?error=no_disponible");
	        }
	    } catch (Exception e) {
	        response.sendRedirect("Tienda?error=error_inesperado");
	    }
	}

		
}


