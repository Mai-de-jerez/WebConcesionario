<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Usuario" %>

<%
    // 1. Solo necesitamos al dueño de la sesión
    Usuario u = (Usuario) session.getAttribute("usuarioLogueado");

    // May, no lo sé, si no hay nadie logueado, al login de cabeza
    if (u == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Lógica para saber si es personal de la tienda
    int nivel = u.getRol().getNivel();
    boolean esPersonal = (nivel <= 2);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil | <%= u.getUsuario() %></title>
    <link rel="stylesheet" href="styles.css"> </head>
<body class="home-body">

    <main class="content-area">
        <div class="container" style="max-width: 900px; margin-top: 40px;">
            <div style="display: flex; flex-wrap: wrap; gap: 40px; align-items: center; background: white; padding: 40px; border-radius: 15px; box-shadow: 0 10px 25px rgba(0,0,0,0.05);">
                
                <%-- Espacio del Avatar (Inicial en grande con tu estilo) --%>
                <div style="flex: 1; min-width: 250px; text-align: center;">
                    <div class="feature-card" style="padding: 40px; background: #f3e5f5; border-radius: 50%; width: 180px; height: 180px; margin: 0 auto; display: flex; align-items: center; justify-content: center; box-shadow: 0 10px 20px rgba(0,0,0,0.1);">
                        <span style="font-size: 80px; color: #764ba2; font-weight: bold;">
                            <%= u.getUsuario().substring(0, 1).toUpperCase() %>
                        </span>
                    </div>
                    <p style="margin-top: 15px; font-weight: bold; color: #764ba2; letter-spacing: 1px;">
                        USUARIO: <%= u.getUsuario().toUpperCase() %>
                    </p>
                </div>

                <%-- Información detallada --%>
                <div style="flex: 1.5; min-width: 300px;">
                    
                    <h1 style="font-size: 2.5em; margin-bottom: 10px; color: #333;">
                        <span style="color: #764ba2;">
                            <%= esPersonal ? "Perfil de Vendedor" : "Perfil de usuario" %>
                        </span>
                    </h1>
                    
                    
                    <div class="table-container" style="margin: 0;">
                        <table style="font-size: 0.95em; width: 100%;">
                            <tbody>
                                <tr>
                                    <td><strong>Estado</strong></td>                      
                                    <td>
                                        <% if (esPersonal) { %>
                                            <span style="padding: 4px 12px; border-radius: 20px; background: #fff3e0; color: #e65100; font-size: 0.85em; font-weight: bold;">
                                                ⭐ Vendedor Nivel <%= nivel %>
                                            </span>
                                        <% } else { %>
                                            <span style="padding: 4px 12px; border-radius: 20px; background: #e8f5e9; color: #2e7d32; font-size: 0.85em; font-weight: bold;">
                                                Cliente Verificado
                                            </span>
                                        <% } %>
                                    </td>                                  
                                </tr>
                                <tr><td>Nombre de Usuario</td><td><%= u.getUsuario() %></td></tr>
                                <tr><td>Correo Electrónico</td><td><%= u.getEmail() %></td></tr>
                           
                            </tbody>
                        </table>
                    </div>                                                    
                </div>
            </div>
            <%-- Botones de acción --%>
       
            <div class="form-buttons" style="display: flex; justify-content: center; margin-top: 30px; gap: 15px;">
                <%-- Botón dinámico: cambia de Pedidos a Ventas --%>
                <a href="ListarPedidos?origen=perfil" class="btn btn-success" style="background: <%= esPersonal ? "#2e7d32" : "#764ba2" %>; border: none;">
                    <%= esPersonal ? "📊 Ver mis Ventas" : "🛒 Ver mis Pedidos" %>
                </a>
                
                <%-- En perfil.jsp, cambia el enlace por este --%>
				<a href="ActualizarPerfilServlet" class="btn btn-primary">✏️ Editar Perfil</a>
                <a href="index.jsp" class="btn btn-back">Volver</a>
            </div>
        </div>
    </main>

</body>
</html>