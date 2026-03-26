<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Usuario" %>

<%
    // 1. Aquí no hay niveles, solo miramos que esté logueado
    Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
    
    if (u == null) {
        response.sendRedirect("login.jsp");
        return; 
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Mi Perfil | <%= u.getUsuario() %></title>
    <link rel="stylesheet" href="styles.css">
</head>
<body class="home-body">

    <main class="content-area">
        <div class="container" style="max-width: 700px; margin-top: 40px;">
            <div class="feature-card" style="background: white; padding: 40px; border-radius: 15px; box-shadow: 0 10px 25px rgba(0,0,0,0.05);">
                
                <h1 style="font-size: 2em; margin-bottom: 25px; color: #333; text-align: center;">
                    ✏️ Editar <span style="color: #764ba2;">mis datos personales</span>
                </h1>

                <%-- Mensajes de error o éxito --%>
                <% if (session.getAttribute("mensaje") != null) { %>
                    <div class="message-info" style="margin-bottom: 20px; text-align: center;">
                        <%= session.getAttribute("mensaje") %>
                        <% session.removeAttribute("mensaje"); %>
                    </div>
                <% } %>

                <form action="ActualizarPerfilServlet" method="post">
                    
                    <%-- Los datos básicos --%>
                    <div class="form-group" style="margin-bottom: 20px;">
                        <label style="font-weight: bold; color: #555;">Nombre de Usuario:</label>
                        <input type="text" name="usuario" value="<%= u.getUsuario() %>" required 
                               style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; margin-top: 8px;">
                    </div>

                    <div class="form-group" style="margin-bottom: 20px;">
                        <label style="font-weight: bold; color: #555;">Correo Electrónico:</label>
                        <input type="email" name="email" value="<%= u.getEmail() %>" required 
                               style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; margin-top: 8px;">
                    </div>

                    <hr style="border: 0; border-top: 1px solid #eee; margin: 30px 0;">
                    
                    <p style="color: #888; font-size: 0.9em; margin-bottom: 15px;">
                        💡 Deja estos campos vacíos si no deseas cambiar tu contraseña.
                    </p>

                    <div style="display: flex; gap: 20px; flex-wrap: wrap;">
                        <div class="form-group" style="flex: 1; min-width: 250px; margin-bottom: 20px;">
                            <label style="font-weight: bold; color: #555;">Nueva Contraseña:</label>
                            <input type="password" name="password" placeholder="********" 
                                   style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; margin-top: 8px;">
                        </div>

                        <div class="form-group" style="flex: 1; min-width: 250px; margin-bottom: 20px;">
                            <label style="font-weight: bold; color: #555;">Confirmar Contraseña:</label>
                            <input type="password" name="passwordConfirm" placeholder="********" 
                                   style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; margin-top: 8px;">
                        </div>
                    </div>

                    <%-- Botones centrados --%>
                    <div class="form-buttons" style="display: flex; justify-content: center; gap: 15px; margin-top: 30px;">
                        <button type="submit" class="btn btn-success" style="background: #764ba2; border: none; padding: 12px 30px;">
                            💾 Guardar Cambios
                        </button>
                        <a href="perfil.jsp" class="btn btn-back" style="padding: 12px 30px;">
                            ❌ Cancelar
                        </a>
                    </div>

                </form>
            </div>
        </div>
    </main>

</body>
</html>