<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
    // 1. Recuperamos al usuario
    modelo.Usuario usu = (modelo.Usuario) session.getAttribute("usuarioLogueado");
    
    // 2. Si no hay usuario o es un Cliente (Nivel > 2), lo mandamos al login 
    if (usu == null || usu.getRol().getNivel() > 2) {
        response.sendRedirect("login.jsp");
        return; // 
    }

    // 3. Capturamos el parámetro de login ok
    String loginStatus = request.getParameter("login");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de administrador</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container">
        <h1>🚗 Panel de administrador</h1>
        
        <%-- Mostramos el mensaje de éxito solo si acaba de loguearse --%>
        <% if ("ok".equals(loginStatus)) { %>
	        <div class="alert-success">
	            ✅ ¡Hola de nuevo, <strong><%= usu.getUsuario() %></strong>! Has iniciado sesión correctamente.
	        </div>
	    <% } %>
        
        <div class="welcome-section">
	        <p>Estás gestionando el sistema como: <strong><%= usu.getNombreRol() %></strong></p>
        </div>

        <div class="feature-cards">
            <div class="feature-card" onclick="location.href='ListarVehiculos'" style="cursor: pointer;">
			    <h3>🚗 Gestión Vehículos</h3>
			    <p>Administra tus vehículos de manera eficiente</p>
			</div>
            <div class="feature-card" onclick="location.href='ListarUsuariosServlet'" style="cursor: pointer;">
                <h3>👤 Gestión de usuarios</h3>
                <p>Administra usuarios de tu sitio web de manera eficiente.</p>
            </div>
            <div class="feature-card" onclick="location.href='ListarPedidos'" style="cursor: pointer;">
                <h3>📦 Gestión de Pedidos</h3>
                <p>Administra pedidos de manera eficiente.</p>
            </div>
        </div>

            
        <div class="nav-buttons">
		    <%-- Ya no preguntamos si usu es null, porque ya sabemos que NO lo es --%>
		    <a href="LogoutServlet" class="btn btn-primary">Cerrar Sesión</a>
		    <a href="index.jsp" class="btn btn-back">Volver al Inicio</a>
		</div>      
    </div>
</body>
</html>
