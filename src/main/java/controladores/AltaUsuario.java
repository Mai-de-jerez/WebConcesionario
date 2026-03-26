package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Rol;
import modelo.Usuario;
import java.io.IOException;

import dao.UsuarioDAO;


/**
 * Servlet implementation class AltaUsuario
 */
@WebServlet("/AltaUsuario")
public class AltaUsuario extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AltaUsuario() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		Usuario usuLogueado = (Usuario) request.getSession().getAttribute("usuarioLogueado");

	    if (usuLogueado != null && usuLogueado.getRol().getNivel() == 2) {
	        request.getSession().setAttribute("mensaje", "⚠️ No tienes permisos para crear usuarios.");
	        response.sendRedirect("ListarUsuariosServlet");
	        return;
	    }
	    request.getRequestDispatcher("alta-usuario.jsp").forward(request, response);
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setCharacterEncoding("UTF-8");
	    Usuario usuLogueado = (Usuario) request.getSession().getAttribute("usuarioLogueado");

	    if (usuLogueado != null && usuLogueado.getRol().getNivel() == 2) {
	        response.sendRedirect("ListarUsuariosServlet?error=Prohibido");
	        return;
	    }

	    String user = request.getParameter("usuario");
	    String email = request.getParameter("email");
	    String pass = request.getParameter("password");
	    String pass2 = request.getParameter("password_confirm");
	    String rolForm = request.getParameter("rol"); 
	    String telefono = request.getParameter("telefono");
	    String direccion = request.getParameter("direccion");

	    // Lógica de roles
	    Rol rolFinal = Rol.CLIENTE;
	    if (usuLogueado != null && usuLogueado.getRol().getNivel() == 1) {
	        if (rolForm != null && !rolForm.isEmpty()) {
	            rolFinal = Rol.valueOf(rolForm.toUpperCase());
	        }
	    }

	    // 2. Validación de claves y de campos obligatorios
	    if (pass == null || !pass.equals(pass2)) {
	        request.setAttribute("error", "Las contraseñas no coinciden, May.");
	        request.getRequestDispatcher("alta-usuario.jsp").forward(request, response);
	        return;
	    }

	    // Validación "Hacker": Que no nos manden campos vacíos
	    if (telefono == null || telefono.isEmpty() || direccion == null || direccion.isEmpty()) {
	        request.setAttribute("error", "El teléfono y la dirección son obligatorios para el contrato.");
	        request.getRequestDispatcher("alta-usuario.jsp").forward(request, response);
	        return;
	    }

	    try {
	        
	        Usuario nuevo = new Usuario(user, email, pass, telefono, direccion);
	        
	        // Si el Superuser eligió otro rol, lo cambiamos antes de registrar
	        if (rolFinal != Rol.CLIENTE) {
	            nuevo.setRol(rolFinal);
	        }

	        UsuarioDAO.getInstance().registrar(nuevo); 

	        if (usuLogueado != null && usuLogueado.getRol().getNivel() == 1) {
	            response.sendRedirect("ListarUsuariosServlet?mensaje=Usuario creado con éxito.");
	        } else {
	            response.sendRedirect("login.jsp?mensaje=Registro-completado-Ya-puedes-entrar");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        request.setAttribute("error", "Error: El usuario o email ya existen.");
	        request.getRequestDispatcher("alta-usuario.jsp").forward(request, response);
	    }
	}
	
}

	


