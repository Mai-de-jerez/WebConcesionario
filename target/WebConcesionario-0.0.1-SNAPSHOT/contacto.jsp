<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Usuario" %>
<%
    // Recuperamos al usuario de la sesión para la barra de navegación
    Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contacto | Concesionario May</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body class="home-body"> 
    <nav class="navbar">
        <div class="nav-left">
            <a href="index.jsp" class="nav-link">Inicio</a>
            <a href="Tienda" class="nav-link">Tienda</a>
            <a href="sobre-nosotros.jsp" class="nav-link">Sobre Nosotros</a>
            <a href="contacto.jsp" class="nav-link">Contacto</a>
        </div>
        
        <div class="nav-right">
            <% if (usu == null) { %>
                <a href="login.jsp" class="btn-nav">Login</a>
            <% } else if (usu.getRol().getNivel() <= 2) { %>
            	<a href="perfil.jsp" class="nav-link" style="margin-right:10px;">Mi Perfil</a>
                <a href="admin-panel.jsp" class="btn-nav">Admin</a>
                <a href="LogoutServlet" class="nav-link" style="margin-left:10px;">Salir</a>
            <% } else { %>
                <span class="nav-link">Hola, <%= usu.getUsuario() %></span>
                <a href="perfil.jsp" class="nav-link" style="margin: 0 15px;">Mi Perfil</a>
                <a href="LogoutServlet" class="btn-nav">Cerrar Sesión</a>
            <% } %>
       	</div>
    </nav>

    <main class="content-area">
        <div class="container" style="max-width: 1100px;">
            <h1>Contacto</h1>
            
            <div class="form-grid">
                <div class="linea_divisoria_vertical" style="padding: 20px; border-right: 1px solid #e1e8ed;">
                    <h2>Nuestros Datos</h2>
                    <div class="feature-cards" style="grid-template-columns: 1fr; margin: 0;">
                            <h3>📍 Dirección</h3>
                            <p>Calle del Motor, 45 (28045 Madrid)</p>
                            <h3>📞 Teléfono</h3>
                            <p>910 00 00 00 - 600 00 00 00</p>
                            <h3>⏰ Horario</h3>
                            <p>L-V: 09:00 a 20:00 - S: 10:00 a 14:00</p>                      
                    </div>
                </div>

                <div>
                    <h2>Envíanos un mensaje</h2>
					<%-- MENSAJES DE ERROR O EXITO --%>
                    <% if(request.getAttribute("mensaje") != null) { %>
                        <div style="color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb; padding: 10px; border-radius: 5px; margin-bottom: 15px;">
                            <%= request.getAttribute("mensaje") %>
                        </div>
                    <% } %>
                    
                    <% if(request.getAttribute("error") != null) { %>
                        <div style="color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 10px; border-radius: 5px; margin-bottom: 15px;">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>
 
                    <form action="EnviarMensaje" method="post">
                    	<%-- CAMPO NOMBRE --%>
				        <div class="form-group">
				            <label>Nombre Completo:</label>
				            <input type="text" name="nombre" placeholder="Escribe tu nombre" 
				                   value="<%= (usu != null) ? usu.getUsuario() : "" %>" 
				                   <%= (usu == null) ? "disabled" : "" %> required>
				        </div>
				        
				        <%-- CAMPO EMAIL --%>
				        <div class="form-group">
				            <label>Email:</label>
				            <input type="email" name="email" 
				                   value="<%= (usu != null) ? usu.getEmail() : "" %>" 
				                   <%= (usu == null) ? "disabled" : "readonly style='background-color: #e9ecef; cursor: not-allowed;'" %> 
				                   placeholder="tu@email.com" required>
				        </div>
				
				        <%-- CAMPO MENSAJE --%>
				        <div class="form-group">
				            <label>Mensaje:</label>
				            <textarea name="mensaje" rows="6" 
				                style="width: 100%; padding: 12px 15px; border: 2px solid #e1e8ed; border-radius: 8px; font-size: 1em; background-color: <%= (usu == null) ? "#eeeeee" : "#f8f9fa" %>; font-family: inherit;" 
				                placeholder="<%= (usu == null) ? "Inicia sesión para escribir aquí" : "¿En qué podemos ayudarte?" %>" 
				                <%= (usu == null) ? "disabled" : "" %> required></textarea>
				        </div>
                               
                        
                        <%-- BOTÓN CONDICIONAL --%>
				        <% if (usu != null) { %>
				            <button type="submit" class="btn btn-primary" style="width: 100%;">
				                Enviar Consulta
				            </button>
				        <% } else { %>
				            <div style="text-align: center; margin-top: 15px;">
				                <p style="color: #d9534f; font-weight: bold; margin-bottom: 10px;">
				                    ⚠️ Debes estar registrado para usar el formulario
				                </p>
				                <a href="login.jsp" class="btn-nav" style="display: block; background-color: #007bff; color: white; text-decoration: none; padding: 10px; border-radius: 5px;">
				                    Ir al Login ahora
				                </a>
				            </div>
				        <% } %>
                    </form>
                </div>
            </div>
        </div>
    </main>

    <footer class="main-footer">
        <p>&copy; 2026 Concesionario May. Todos los derechos reservados.</p>
    </footer>   
</body>
</html>