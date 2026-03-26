<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Usuario" %>
<%@ page import="modelo.Rol" %>
<%
    // 1. Recuperamos el usuario que el Servlet "DetalleUsuarioServlet" ha buscado
    Usuario uDetalle = (Usuario) request.getAttribute("usuarioFicha");
    Usuario miUsuario = (Usuario) session.getAttribute("usuarioLogueado");

    // May, no lo sé, si el usuario no existe volvemos a la lista
    if (uDetalle == null) {
        response.sendRedirect("ListarUsuariosServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Perfil de <%= uDetalle.getUsuario() %> | Gestión de Usuarios</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body class="home-body">

    <main class="content-area">
        <div class="container" style="max-width: 900px;">
            <div style="display: flex; flex-wrap: wrap; gap: 40px; align-items: center;">
                
                <%-- Espacio del Avatar (Sin foto, solo inicial) --%>
                <div style="flex: 1; min-width: 250px; text-align: center;">
                    <div class="feature-card" style="padding: 40px; background: #f3e5f5; border-radius: 50%; width: 200px; height: 200px; margin: 0 auto; display: flex; align-items: center; justify-content: center; box-shadow: 0 10px 20px rgba(0,0,0,0.1);">
                        <span style="font-size: 80px; color: #764ba2; font-weight: bold;">
                            <%= uDetalle.getUsuario().substring(0, 1).toUpperCase() %>
                        </span>
                    </div>
                    <p style="margin-top: 15px; font-weight: bold; color: #666;">ID de Usuario: #<%= uDetalle.getId() %></p>
                </div>

                <div style="flex: 1.5; min-width: 300px;">
                    
                    <h1 style="font-size: 2.5em; margin-bottom: 20px;">
                        <span style="color: #333;"><%= uDetalle.getUsuario() %></span>
                    </h1>
                    
                    <div class="table-container" style="margin: 0;">
                        <table style="font-size: 0.95em;">
                            <tbody>
                                <tr>
                                    <td><strong>Estado de Cuenta</strong></td>
                                    <td>
                                        <span class="message-success" style="padding: 2px 10px; border-radius: 5px; background: #e8f5e9; color: #2e7d32;">
                                            Activo
                                        </span>
                                    </td>
                                </tr>
                                <tr><td>Nombre de Usuario</td><td><%= uDetalle.getUsuario() %></td></tr>
                                <tr><td>Correo Electrónico</td><td><%= uDetalle.getEmail() %></td></tr>
                                <tr><td>Rol Asignado</td><td><strong><%= uDetalle.getRol() %></strong></td></tr>
                                <tr><td>Nivel de Acceso</td><td>Nivel <%= uDetalle.getRol().getNivel() %></td></tr>
                                
                      
                            </tbody>
                        </table>
                    </div>
                    
                    <div class="form-buttons" style="justify-content: flex-start; margin-top: 25px;">    
                        
                        <% if (miUsuario.getRol().getNivel() == 1 && uDetalle.getId() != miUsuario.getId()) { %>
						    <a href="EliminarUsuario?id=<%= uDetalle.getId() %>" 
						       class="btn btn-secondary" 
						       onclick="return confirm('¿Estás totalmente segura de eliminar a este usuario?')">🗑️ Eliminar</a>
						<% } %>
                        
                        <a href="ListarUsuariosServlet" class="btn btn-back">Volver a la Lista</a>
                    </div>
                                 
                </div>
            </div>
        </div>
    </main>

</body>
</html>