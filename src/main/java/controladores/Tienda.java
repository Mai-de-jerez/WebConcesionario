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
            String busqueda = request.getParameter("busqueda");
            String paginaParam = request.getParameter("pagina");
            int pagina = (paginaParam != null) ? Integer.parseInt(paginaParam) : 1;
            int porPagina = 9;

            List<Coche> lista = CocheDAO.getInstance().listarTienda(busqueda, pagina, porPagina);
            long total = CocheDAO.getInstance().contarTienda(busqueda);
            int totalPaginas = (int) Math.ceil((double) total / porPagina);

            request.setAttribute("listaCoches", lista);
            request.setAttribute("busqueda", busqueda);
            request.setAttribute("paginaActual", pagina);
            request.setAttribute("totalPaginas", totalPaginas);

            request.getRequestDispatcher("tienda.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "No se han podido cargar los vehículos. Inténtalo de nuevo más tarde.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}