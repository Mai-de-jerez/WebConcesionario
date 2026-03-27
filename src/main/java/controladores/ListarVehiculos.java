package controladores;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Coche;
import dao.CocheDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/ListarVehiculos")
public class ListarVehiculos extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ListarVehiculos() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String busqueda = request.getParameter("busqueda");
            String paginaParam = request.getParameter("pagina");
            int pagina = (paginaParam != null) ? Integer.parseInt(paginaParam) : 1;
            int porPagina = 9;

            List<Coche> lista = CocheDAO.getInstance().listarAdmin(busqueda, pagina, porPagina);
            long total = CocheDAO.getInstance().contarAdmin(busqueda);
            int totalPaginas = (int) Math.ceil((double) total / porPagina);

            String formato = request.getParameter("formato");

            if ("json".equals(formato)) {
                // ─── RESPUESTA JSON con Gson ───
                JsonObject respuesta = new JsonObject();
                JsonArray coches = new JsonArray();

                for (Coche c : lista) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", c.getId());
                    obj.addProperty("matricula", c.getMatricula());
                    obj.addProperty("marca", c.getMarca());
                    obj.addProperty("modelo", c.getModelo());
                    obj.addProperty("anio", c.getAnio());
                    obj.addProperty("color", c.getColor());
                    obj.addProperty("km", c.getKm());
                    coches.add(obj);
                }

                respuesta.add("coches", coches);
                respuesta.addProperty("totalPaginas", totalPaginas);
                respuesta.addProperty("paginaActual", pagina);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new Gson().toJson(respuesta));

            } else {
                // ─── RESPUESTA JSP clásica ───
                request.setAttribute("listaCoches", lista);
                request.setAttribute("busqueda", busqueda);
                request.setAttribute("paginaActual", pagina);
                request.setAttribute("totalPaginas", totalPaginas);
                request.getRequestDispatcher("listar-vehiculos.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar el inventario: " + e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}