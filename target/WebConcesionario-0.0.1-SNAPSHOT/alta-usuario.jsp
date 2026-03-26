<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // 1. Ya no echamos a nadie, solo miramos quién es
    modelo.Usuario usu = (modelo.Usuario) session.getAttribute("usuarioLogueado");
    
    // 2. Creamos una "banderita" para saber si es el Superuser
    boolean esAdmin = (usu != null && usu.getRol().getNivel() == 1);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= esAdmin ? "Gestión de Usuario" : "Registro de Usuario" %></title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container">
        <%-- Título dinámico --%>
        <h1>👤 <%= esAdmin ? "Nuevo Usuario (Gestión Interna)" : "Crear nueva cuenta" %></h1>

        <% if(request.getAttribute("error") != null) { %>
            <div class="message message-error" style="margin-bottom: 20px; color: red;">
                ⚠️ <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form id="formUsuario" action="AltaUsuario" method="post">
            <div class="form-grid">
                
                <div class="form-group">
                    <label for="usuario">Nombre de Usuario: *</label>
                    <input type="text" id="usuario" name="usuario" required placeholder="Ej: carmen_lucena">
                </div>
        
                <div class="form-group">
                    <label for="email">Correo Electrónico: *</label>
                    <input type="email" id="email" name="email" required placeholder="ejemplo@editorial.com">
                </div>
        
                <div class="form-group">
                    <label for="password">Contraseña: *</label>
                    <input type="password" id="password" name="password" required placeholder="Crea una clave segura">
                </div>
        
                <div class="form-group">
                    <label for="password_confirm">Confirmar Contraseña: *</label>
                    <input type="password" id="password_confirm" name="password_confirm" required placeholder="Repite la clave">
                </div>
        
                <%-- LA CLAVE: El admin ve el selector, el usuario no ve nada --%>
                <% if (esAdmin) { %>
                    <div class="form-group">
                        <label for="rol">Asignar Rol: *</label>
                        <select id="rol" name="rol" required>
                            <option value="EMPLEADO">Empleado</option>
                            <option value="SUPERUSER">Superuser</option>
                        </select>
                    </div>
                <% } else { %>
                    <%-- El usuario normal no ve esto, pero el formulario envía "CLIENTE" --%>
                    <input type="hidden" name="rol" value="CLIENTE">
                <% } %>

                <div class="form-group" style="visibility: hidden;">
                    <label>Espaciador</label>
                    <input type="text">
                </div>
            </div>
        
            <div class="form-buttons">
                <button type="submit" class="btn btn-success">
                    💾 <%= esAdmin ? "Crear Usuario" : "Registrarme" %>
                </button>
                <button type="reset" class="btn btn-secondary">🔄 Limpiar </button>
                

                <a href="<%= esAdmin ? "ListarUsuariosServlet" : "index.jsp" %>" class="btn btn-back">❌ Cancelar</a>
            </div>
        </form>
    </div> 
</body>
</html>