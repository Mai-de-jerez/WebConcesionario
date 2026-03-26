package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.TipoMotor;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import modelo.Coche;
import modelo.EstadoVehiculo;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.File;
import java.util.Random;


/**
 * Servlet implementation class AltaVehiculo
 */
@WebServlet("/AltaVehiculo")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 1,  
	    maxFileSize = 1024 * 1024 * 10,       
	    maxRequestSize = 1024 * 1024 * 100    
	)

public class AltaVehiculo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String pathFiles = "C:\\Users\\carme\\Proyectos_Java\\WebConcesionario\\src\\main\\webapp\\img";
    private File uploads = new File(pathFiles);
    
    
    // --- 🎲 MÉTODO PARA GENERAR EL NOMBRE ALEATORIO ---
    private String generarNombreUnico(String originalName) {
        if (originalName == null || originalName.isEmpty()) return "sin-foto.png";
        
        int dotIndex = originalName.lastIndexOf('.');
        String nombre = (dotIndex == -1) ? originalName : originalName.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : originalName.substring(dotIndex);
        
        int aleatorio = new Random().nextInt(100000); // Número entre 0 y 99999
        return nombre + "_" + aleatorio + extension;
    }

    /**
     * Default constructor. 
     */
    public AltaVehiculo() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("alta-vehiculo.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		    
		    try {
		        // 1. Recogemos los datos (Igual que antes)
		        String marca = request.getParameter("marca");
		        String nombreModelo = request.getParameter("modelo");
		        String matricula = request.getParameter("matricula");
		        String color = request.getParameter("color");
		        double precio = Double.parseDouble(request.getParameter("precio"));
		        int km = Integer.parseInt(request.getParameter("km"));
		        String anio = request.getParameter("anio");
		        int numPuertas = Integer.parseInt(request.getParameter("numPuertas"));     
		        String descripcion = request.getParameter("descripcion");
		        
		        // 2. Enums: Ahora los sacamos directamente por su nombre (valueof)
		        // Ojo: Asegúrate de que el "value" del <select> en el JSP coincida con el nombre del Enum
		        TipoMotor motorEnum = TipoMotor.valueOf(request.getParameter("tipoMotor"));     
		        EstadoVehiculo estadoEnum = EstadoVehiculo.valueOf(request.getParameter("estado")); 
		        
		        // 3. Gestión de la imagen (Tu lógica de archivos se queda igual)
		        Part part = request.getPart("imagen");
		        String originalFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
		        String fileName = generarNombreUnico(originalFileName);
		        
		        File file = new File(uploads, fileName); 
		        try (InputStream input = part.getInputStream()) {
		            Files.copy(input, file.toPath());
		        } catch (Exception e) {
		            System.out.println("⚠️ Nota: La foto no se copió: " + e.getMessage());
		        }

		        Coche coche = new Coche(0, marca, nombreModelo, matricula, color, precio, km, anio, motorEnum, numPuertas, estadoEnum, fileName, descripcion);

		        dao.CocheDAO.getInstance().insertar(coche);

		        response.sendRedirect("ListarVehiculos?msj=ok");
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		        request.setAttribute("error", "Error al dar de alta: " + e.getMessage());
		        request.getRequestDispatcher("alta-vehiculo.jsp").forward(request, response);
		    }
		}

}
