package filtros;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;

import java.io.IOException;

/**
 * Servlet Filter implementation class ClienteAuthFilter
 */
@WebFilter(urlPatterns = {
		"/clientes",
	    "/Checkout_Sv",
	    "/Cliente_Sv",
	    "/LogoutServlet"
	})
public class ClienteAuthFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpFilter#HttpFilter()
     */
    public ClienteAuthFilter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response; 
        
        // pillamos la sesión (si existe)
        HttpSession sesion = req.getSession(false);
        Usuario u = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        // acceso:
        if (u == null) {
            // Si no está logueado, lo mandamos al login para que se identifique
            res.sendRedirect("login.html");
        } else {
            // si está logueado adelante
            chain.doFilter(request, response);
        }
    }

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
