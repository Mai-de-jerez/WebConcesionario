<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Coche" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="modelo.Rol" %>
<%
    // 1. Recuperamos los objetos que nos manda el Servlet
    Coche c = (Coche) request.getAttribute("cocheDetalle");
    Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
    
    // 2. Recuperamos el modo que acabamos de programar
    Boolean modoAdmin = (Boolean) request.getAttribute("modoAdmin");
    if (modoAdmin == null) modoAdmin = false;

    // Seguridad: Si por error llegamos aquí sin coche, volvemos atrás
    if (c == null) {
        response.sendRedirect("Tienda");
        return;
    }
    
    // Determinamos si el usuario es Staff para otras comprobaciones visuales
    boolean esPersonal = (usu != null && usu.getRol().getNivel() <= 2);
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= c.getMarca() %> <%= c.getModelo() %> | Detalle</title>
    <link rel="stylesheet" href="styles.css">
</head>
	<body class="home-body">
	    <main class="content-area">
	
	        <div class="container" style="max-width: 900px;">
	            <div style="display: flex; flex-wrap: wrap; gap: 40px; align-items: start;">
	                
	                <div style="flex: 1; min-width: 300px;">
					    <div class="feature-card" style="padding: 10px; cursor: default;">
					        <%-- 1. Comprobamos si el coche tiene imagen en la BD --%>
					        <% 
					            String nombreFoto = c.getImagen(); 
					            // Si es nulo o vacío, ponemos una imagen de "no disponible"
					            if (nombreFoto == null || nombreFoto.isEmpty()) {
					                nombreFoto = "sin-foto.png"; // Asegúrate de tener esta en la carpeta img
					            }
					        %>
					        <img src="img/<%= nombreFoto %>" 
					             alt="<%= c.getModelo() %>" 
					             style="width:100%; border-radius:15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); min-height: 200px; object-fit: cover;">
					    </div>
					</div>
	
	                <div style="flex: 1.2; min-width: 300px;">
	                    
	                    <h1 style="font-size: 2.5em; display: flex; align-items: baseline; gap: 15px; flex-wrap: wrap;">
					        <span style="color: #764ba2;"><%= c.getMarca() %></span>
					        <span style="color: #333;"><%= c.getModelo() %></span>
					    </h1>
	                    
	                    <div class="table-container" style="margin: 0;">
	                        <table style="font-size: 0.95em;">
	                            <tbody>	                                
	                                <% if (esPersonal) { %>
	                                    <tr>
	                                        <td><strong>ID Vehículo (Interno)</strong></td>
	                                        <td><%= c.getId() %></td>
	                                    </tr>
	                                <% } %>
	
	                                <tr><td><strong>Marca</strong></td><td><%= c.getMarca() %></td></tr>
	   
	                                <tr><td><strong>Modelo</strong></td><td><%= c.getModelo() %></td></tr>
	                          
	                                <tr><td><strong>Matrícula</strong></td><td><%= c.getMatricula() %></td></tr>
	                     
	                                <tr><td><strong>Color</strong></td><td><%= c.getColor() %></td></tr>
	                          
	                                <tr><td><strong>Precio</strong></td><td><%= c.getPrecio() %> €</td></tr>
	                         
	                                <tr><td><strong>Kilómetros</strong></td><td><%= c.getKm() %> km</td></tr>
	                    
	                                <tr><td><strong>Año de fabricación</strong></td><td><%= c.getAnio() %></td></tr>
	                        
	                                <tr><td><strong>Tipo de Motor</strong></td><td><%= c.getTipoMotor().getTexto() %></td></tr>
	                     
	                                <tr><td><strong>Número de Puertas</strong></td><td><%= c.getNumPuertas() %></td></tr>
	                      
	                                <tr><td><strong>Disponibilidad</strong></td><td>
	                                    <span><%= c.getEstado().getTexto() %></span>
	                                </td></tr>
	                                <tr>
									    <td><strong>Descripción</strong></td>
									    <td>
									        <%= (c.getDescripcion() != null && !c.getDescripcion().isEmpty()) 
									            ? c.getDescripcion() 
									            : "Sin descripción adicional disponible." %>
									    </td>
									</tr>
	                            </tbody>
	                        </table>
	                    </div>
						
						<div class="form-buttons" style="justify-content: flex-start; margin-top: 25px; gap: 10px; display: flex; flex-wrap: wrap;">

						    <% if (modoAdmin) { %>
						        <%-- MODO ADMINISTRACIÓN: El Superuser está gestionando --%>
						        <a href="EditarVehiculo?id=<%= c.getId() %>" class="btn btn-primary">✏️ Editar Vehículo</a>
						        <a href="ListarVehiculos" class="btn btn-back">Volver al Panel</a>
						    
						    <% } else { %>
						        <%-- MODO TIENDA: El usuario (o el admin de paseo) está comprando --%>
						        <% if (usu == null) { %>
						            <a href="login.jsp?origen=compra&idRegreso=<%= c.getId() %>" class="btn btn-secondary">Comprar</a>
						        <% } else { %>
						            <a href="Carrito?idCoche=<%= c.getId() %>&precioCoche=<%= c.getPrecio() %>" class="btn btn-success">🛒 Añadir a la cesta</a>
						        <% } %>
						        <a href="Tienda" class="btn btn-back">Volver a Tienda</a>
						    <% } %>
						
						</div>
	                    	                                 
	                </div>
	            </div>
	        </div>
	    </main>

	</body>
</html>