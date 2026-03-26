package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Coche;
import dao.CocheDAO; // Importamos el DAO
import java.io.IOException;
import java.util.List;

/**
 * Servlet de la Tienda de Vehículos
 */
@WebServlet("/Tienda")
public class Tienda extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public Tienda() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        try {
            // CORRECCIÓN: Llamamos al Singleton del DAO y a su método listar()
            List<Coche> lista = CocheDAO.getInstance().listar(); 
       
            // Pasamos los datos a la vista
            request.setAttribute("listaCoches", lista);
  
            // Forward al JSP de la tienda
            request.getRequestDispatcher("tienda.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Mensaje amigable de error
            request.setAttribute("error", "No se han podido cargar los vehículos. Inténtalo de nuevo más tarde.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirigimos al GET para que la lógica de carga sea la misma
        doGet(request, response);
    }
}