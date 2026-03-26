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
import modelo.Coche;
import modelo.EstadoVehiculo;
import modelo.TipoMotor;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import dao.CocheDAO;

/**
 * Servlet implementation class EditarVehiculo
 */

@WebServlet("/EditarVehiculo")
@MultipartConfig( 
    fileSizeThreshold = 1024 * 1024 * 1,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 100
)
public class EditarVehiculo extends HttpServlet {
	private static final long serialVersionUID = 1L;  
	private String pathFiles = "C:\\Users\\carme\\Proyectos_Java\\WebConcesionario\\src\\main\\webapp\\img";
    private File uploads = new File(pathFiles);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditarVehiculo() {
        super();
    }
    
 // --- 🎲 EL MISMO MÉTODO QUE EN ALTA ---
    private String generarNombreUnico(String originalName) {
        if (originalName == null || originalName.isEmpty()) return "sin-foto.png";
        int dotIndex = originalName.lastIndexOf('.');
        String nombre = (dotIndex == -1) ? originalName : originalName.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : originalName.substring(dotIndex);
        int aleatorio = new Random().nextInt(100000); 
        return nombre + "_" + aleatorio + extension;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
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
            TipoMotor motorEnum = TipoMotor.valueOf(request.getParameter("tipoMotor"));     
            EstadoVehiculo estadoEnum = EstadoVehiculo.valueOf(request.getParameter("estado"));

        
            String fileName; 
            Part part = request.getPart("imagen");

            if (part != null && part.getSize() > 0) {
                String originalFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                fileName = generarNombreUnico(originalFileName);
                
                File file = new File(uploads, fileName);
                try (InputStream input = part.getInputStream()) {
                    Files.copy(input, file.toPath());
                    System.out.println("✅ Nueva foto guardada: " + file.getAbsolutePath());
                } 
            } else {
   
                fileName = request.getParameter("imagenActual");
            }

            Coche coche = new Coche(id, marca, nombreModelo, matricula, color, precio, km, anio, motorEnum, numPuertas, estadoEnum, fileName, descripcion);
         
            CocheDAO.getInstance().actualizar(coche);
            
            response.sendRedirect("ListarVehiculos?msj=ok");         

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ListarVehiculos?error=catch");
        }
    }
}
