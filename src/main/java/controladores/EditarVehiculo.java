package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import modelo.Coche;
import modelo.EstadoVehiculo;
import modelo.TipoMotor;
import dao.CocheDAO;

@WebServlet("/EditarVehiculo")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 100
)
public class EditarVehiculo extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // --- Carpeta dinámica dentro de la webapp ---
    private String generarNombreUnico(String originalName) {
        if (originalName == null || originalName.isEmpty()) return "sin-foto.png";
        int dotIndex = originalName.lastIndexOf('.');
        String nombre = (dotIndex == -1) ? originalName : originalName.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : originalName.substring(dotIndex);
        int aleatorio = new Random().nextInt(1000000);
        return nombre + "_" + aleatorio + extension;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Coche coche = CocheDAO.getInstance().obtenerPorId(id);
            request.setAttribute("coche", coche);
            request.getRequestDispatcher("editar-vehiculo.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("ListarVehiculos?error=id_invalido");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String marca = request.getParameter("marca");
            String nombreModelo = request.getParameter("modelo");
            String matricula = request.getParameter("matricula");
            String color = request.getParameter("color");
            double precio = Double.parseDouble(request.getParameter("precio"));
            int km = Integer.parseInt(request.getParameter("km"));
            String anio = request.getParameter("anio");
            int numPuertas = Integer.parseInt(request.getParameter("numPuertas"));
            String descripcion = request.getParameter("descripcion");
            TipoMotor motorEnum = TipoMotor.valueOf(request.getParameter("tipoMotor").toUpperCase());
            EstadoVehiculo estadoEnum = EstadoVehiculo.valueOf(request.getParameter("estado").toUpperCase());

            // --- Manejo de la imagen igual que en AltaVehiculo ---
            Part part = request.getPart("imagen");

            // Carpeta dinámica dentro de webapp
            String uploadDir = getServletContext().getRealPath("/img");
            File uploads = new File(uploadDir);
            if (!uploads.exists()) uploads.mkdirs();

            String fileName = "sin-foto.png"; // valor por defecto
            if (part != null && part.getSize() > 0) {
                String contentType = part.getContentType();
                if (!contentType.startsWith("image/")) {
                    throw new ServletException("El archivo subido no es una imagen válida");
                }

                String originalFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                fileName = generarNombreUnico(originalFileName);

                File file = new File(uploads, fileName);
                try (InputStream input = part.getInputStream()) {
                    Files.copy(input, file.toPath());
                    System.out.println("✅ Nueva foto guardada: " + file.getAbsolutePath());
                } catch (Exception e) {
                    System.out.println("⚠️ La foto no se copió: " + e.getMessage());
                    fileName = request.getParameter("imagenActual"); // fallback a la antigua
                }
            } else {
                // No subió imagen → mantener la actual
                String actual = request.getParameter("imagenActual");
                if (actual != null && !actual.isEmpty()) {
                    fileName = actual;
                }
            }

            // --- Crear objeto Coche y actualizar ---
            Coche coche = new Coche(id, marca, nombreModelo, matricula, color, precio, km, anio,
                                    motorEnum, numPuertas, estadoEnum, fileName, descripcion);

            CocheDAO.getInstance().actualizar(coche);

            response.sendRedirect("ListarVehiculos?msj=ok");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al actualizar vehículo: " + e.getMessage());
            request.getRequestDispatcher("editar-vehiculo.jsp").forward(request, response);
        }
    }
}