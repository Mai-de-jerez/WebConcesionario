<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nueva Contraseña - Sistema de Gestión</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container login-container">
        <h1>Nueva Contraseña</h1>

        <form action="RestablecerPassword" method="POST">
            <input type="hidden" name="token" value="${token}">
            
            <div class="form-group">
                <label for="pass1">Nueva contraseña:</label>
                <input type="password" id="pass1" name="pass1" required placeholder="Escribe tu nueva clave">
            </div>

            <div class="form-group">
                <label for="pass2">Confirmar contraseña:</label>
                <input type="password" id="pass2" name="pass2" required placeholder="Repite la clave">
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">
                Actualizar Contraseña
            </button>
        </form>

        <a href="login.jsp" class="btn btn-back" style="width: 100%; display: block; margin-top: 15px; text-align: center; text-decoration: none;">
            Cancelar
        </a>
    </div>
</body>
</html>