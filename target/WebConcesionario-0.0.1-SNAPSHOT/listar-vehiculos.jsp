<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Coche" %>
<%@ page import="modelo.Usuario" %> 
<%
    // 1. Recuperamos al sujeto (Solo una vez)
    Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
    
    // 2. SEGURIDAD: Solo niveles 1 (SUPERUSER) y 2 (EMPLEADO)
    if (usu == null || usu.getRol().getNivel() > 2) {
        session.setAttribute("mensaje", "Acceso denegado: Área exclusiva para personal.");
        response.sendRedirect("login.jsp");
        return; 
    }

%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista de Vehículos - Concesionario May</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container">
        <div class="header-actions">
            <h1>🚗 Lista de Vehículos</h1>
            <% 
			    // Recuperamos el mensaje si existe
			    String mensaje = (String) session.getAttribute("mensaje");
			    if (mensaje != null) {
			%>
			    <div class="message message-success">
			        <%= mensaje %>
			    </div>
			<% 
			        // Lo borramos para que no vuelva a salir al refrescar
			        session.removeAttribute("mensaje");
			    } 
			%>
			
			<% if (usu != null) { %>
		        <a href="alta-vehiculo.jsp" class="btn btn-success">➕ Nuevo Vehículo</a>
		    <% } %>
		    
        </div>

        <div class="table-container">
            <table id="tablaVehiculos">
                <thead>
                    <tr>
                    	<th>ID</th>
                        <th>Matrícula</th>
                        <th>Marca</th>
                        <th>Modelo</th>
                        <th>Año</th>
                        <th>Color</th>
                        <th>Kilómetros</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    	@SuppressWarnings("unchecked")
                        List<Coche> lista = (List<Coche>) request.getAttribute("listaCoches");
                        
                        if (lista != null && !lista.isEmpty()) {
                            for (Coche v : lista) {
                    %>
                    <tr>
                    	<td style="font-weight: bold; color: #764ba2;"><%= v.getId() %></td>
                        <td><%= v.getMatricula() %></td>
                        <td><%= v.getMarca() %></td>
                        <td><%= v.getModelo() %></td>
                        <td><%= v.getAnio() %></td>
                        <td><%= v.getColor() %></td>
                        <td><%= String.format("%, d", v.getKm()) %></td> 
                                               
                        <td class="actions">
						    <a href="DetalleCocheServlet?id=<%= v.getId() %>&admin=true" class="row-btn btn-success btn-small">Ver más</a>					    
						    <a href="EditarVehiculo?id=<%= v.getId() %>" class="row-btn btn-primary btn-small">Editar</a>
						    <a href="EliminarVehiculo?id=<%= v.getId() %>" class="row-btn btn-secondary btn-small" onclick="return confirm('¿Estás seguro de querer eliminar este vehículo?')">Eliminar</a>
						</td>
                        
                    </tr>
                    <% 
                            }
                        } else { 
                    %>
                    <tr>
                        <td colspan="7" style="text-align:center;">No hay vehículos registrados todavía.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <div class="nav-buttons">
            <a href="admin-panel.jsp" class="btn btn-back">Volver al Panel</a>
        </div>
    </div>

    </body>
</html>
