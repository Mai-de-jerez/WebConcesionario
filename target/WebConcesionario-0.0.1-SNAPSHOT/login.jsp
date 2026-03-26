<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // 1. Detectamos el origen antes de pintar nada
    String origen = request.getParameter("origen");
    String tituloLogin = "🔐 Iniciar Sesión";
    String idRegreso = request.getParameter("idRegreso");
    
    if ("compra".equals(origen)) {
        tituloLogin = "🛒 Inicia sesión para comprar";
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema de Gestión de Vehículos</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container login-container">
        <h1><%= tituloLogin %></h1>
       		
		<% 
		    // Cambiamos session por request y "mensajeError" por "error"
		    String msgError = (String) request.getAttribute("error"); 
		    if (msgError != null) { 
		%>
		    <div style="color: white; background-color: #d9534f; padding: 10px; border-radius: 5px; margin-bottom: 20px; text-align: center;">
		        <%= msgError %>
		    </div>
		<% 
		    } 
		%>
        <form id="loginForm" action="Login" method="POST">
        
        	<input type="hidden" name="origen" value="<%= (origen != null) ? origen : "" %>">
        	<input type="hidden" name="idRegreso" value="<%= (idRegreso != null) ? idRegreso : "" %>">
        
            <div class="form-group">
                <label for="usuario">Usuario:</label>
                <input type="text" id="usuario" name="usuario" required placeholder="Ingrese su usuario">
            </div>

            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required placeholder="Ingrese su contraseña">
            </div>
            
            <div class="form-register">
                <span>¿No tienes cuenta?</span>
                <a href="alta-usuario.jsp">Regístrate aquí</a>
            </div>
            <div class="form-register">
		        <a href="olvido-password.jsp">¿Has olvidado tu contraseña?</a>
		    </div>

            <button type="submit" class="btn btn-primary" style="width: 100%;">Ingresar</button>
        </form>

        <a href="index.jsp" class="btn btn-back" style="width: 100%; display: block; margin-top: 15px;">Volver al Inicio</a>
    </div>
</body>
</html>
