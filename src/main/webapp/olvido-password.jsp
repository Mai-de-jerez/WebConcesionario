<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperar Contraseña - Concesionario</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container login-container">
        <h1 style="color: #764ba2; text-align: center;">¿Olvidaste tu clave?</h1>
       		
		<% if (request.getAttribute("mensaje") != null) { %>
		    <div style="color: white; background-color: #28a745; padding: 10px; border-radius: 5px; margin-bottom: 20px; text-align: center;">
		        <%= request.getAttribute("mensaje") %>
		    </div>
		<% } %>

        <form action="OlvidoPasswordServlet" method="POST">
            <div class="form-group">
                <label for="emailRecuperar">Introduce tu email:</label>
                <input type="email" id="emailRecuperar" name="emailRecuperar" required placeholder="tu@email.com" class="form-control">
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Generar enlace</button>
        </form>

        <a href="login.jsp" class="btn btn-back" style="width: 100%; display: block; margin-top: 15px; text-align: center; text-decoration: none;">Volver al Login</a>
    </div>
</body>
</html>