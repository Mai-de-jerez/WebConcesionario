package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import dao.UsuarioDAO; // Importamos el DAO
import java.io.IOException;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {       
   
        String user = request.getParameter("usuario");
        String pass = request.getParameter("password");
        String origen = request.getParameter("origen");
        String idRegreso = request.getParameter("idRegreso");

        try {
            // CAMBIO CLAVE: Ahora llamamos al Singleton del DAO que usa JPA
            Usuario u = UsuarioDAO.getInstance().validar(user, pass);

            if (u != null) {
                HttpSession sesion = request.getSession();
                sesion.setAttribute("usuarioLogueado", u);
             
                // Lógica de redirección según el rol y el origen
                // Nota: Asegúrate de que tu Enum Rol tenga el método getNivel()
                if (u.getRol().getNivel() <= 2) {
                    response.sendRedirect("admin-panel.jsp");
                    
                } else if ("compra".equals(origen) && idRegreso != null && !idRegreso.isEmpty()) {
                    response.sendRedirect("DetalleCocheServlet?id=" + idRegreso);
                    
                } else {
                    response.sendRedirect("index.jsp"); 
                }
                
            } else {
                // Si el DAO devuelve null, las credenciales son incorrectas
                request.setAttribute("error", "Vaya, parece que el usuario o la clave no coinciden.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error técnico al conectar con el servidor.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}