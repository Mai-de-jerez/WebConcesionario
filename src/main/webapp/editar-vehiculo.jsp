<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Coche" %>
<%@ page import="modelo.TipoMotor" %>
<%@ page import="modelo.Usuario" %>

<%
    // 1. EL PORTERO DE SEGURIDAD: COMPROBAMOS ROL
    Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
    
    if (usu == null || usu.getRol().getNivel() > 2) {
        session.setAttribute("mensaje", "Acceso denegado: Se requiere nivel de empleado o superior.");
        response.sendRedirect("login.jsp");
        return; 
    }

    // 2. RECUPERAR DATOS
    Coche c = (Coche) request.getAttribute("coche");
    if (c == null) {
        response.sendRedirect("ListarVehiculos");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Vehículo - Sistema de Gestión</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container">
        <h1>✏️ Editar Vehículo (ID: <%= c.getId() %>)</h1>

        <form id="formVehiculo" action="EditarVehiculo" method="post" enctype="multipart/form-data">
            
            <input type="hidden" name="id" value="<%= c.getId() %>">
            	        
            <div class="form-grid">
		        		        
                <div class="form-group">
                    <label for="matricula">Matrícula: *</label>
                    <input type="text" id="matricula" name="matricula" required value="<%= c.getMatricula() %>">
                </div>

                <div class="form-group">
                    <label for="marca">Marca: *</label>
                    <input type="text" id="marca" name="marca" required value="<%= c.getMarca() %>">
                </div>

                <div class="form-group">
                    <label for="modelo">Modelo: *</label>
                    <input type="text" id="modelo" name="modelo" required value="<%= c.getModelo() %>">
                </div>

                <div class="form-group">
                    <label for="color">Color:</label>
                    <input type="text" id="color" name="color" value="<%= c.getColor() %>">
                </div>

                <div class="form-group">
                    <label for="precio">Precio (€): *</label>
                    <input type="number" step="0.01" id="precio" name="precio" required value="<%= c.getPrecio() %>">
                </div>

                <div class="form-group">
                    <label for="km">Kilómetros: *</label>
                    <input type="number" id="km" name="km" required value="<%= c.getKm() %>">
                </div>

                <div class="form-group">
                    <label for="anio">Año:</label>
                    <input type="text" id="anio" name="anio" value="<%= c.getAnio() %>">
                </div>

                
                <div class="form-group">
				    <label for="tipoMotor">Tipo de Motor: *</label>
				    <select id="tipoMotor" name="tipoMotor" required>
				        <% for (TipoMotor motor : TipoMotor.values()) { %>
				            <option value="<%= motor.getTexto() %>" <%= motor == c.getTipoMotor() ? "selected" : "" %>>
				                <%= motor.getTexto() %>
				            </option>
				        <% } %>
				    </select>
				</div>


                <div class="form-group">
                    <label for="numPuertas">Número de Puertas:</label>
                    <select id="numPuertas" name="numPuertas">
                        <% for (int i = 2; i <= 5; i++) { %>
							<option value="<%= i %>" <%= i == c.getNumPuertas() ? "selected" : "" %>>
                                <%= i %> puertas
                            </option>
                        <% } %>
                    </select>
                </div>
				
				<div class="form-group">
				    <label for="estado">Estado:</label>
				    <select id="estado" name="estado">
				        <option value="Disponible" <%= c.isDisponible() ? "selected" : "" %>>Disponible</option>
				        <option value="Reservado" <%= c.isReservado() ? "selected" : "" %>>Reservado</option>
				        <option value="Vendido" <%= c.isVendido() ? "selected" : "" %>>Vendido</option>
				    </select>
				</div>				                                             
            </div>
            <div class="form-group"> 
				<div style="flex: 0 0 auto;"> 			 
					<% String foto = c.getImagen(); if(foto == null || foto.isEmpty()) foto = "sin-foto.png"; %> 
					<img src="img/<%= foto %>" alt="Actual" style="width: 100px; height: 70px; object-fit: cover; border-radius: 5px; border: 1px solid #ddd;"> 
					<input type="hidden" name="imagenActual" value="<%= c.getImagen() %>" style="display: block; width: 100%;"> </div> <div style="flex: 1 1 250px;"> 
					<label for="imagen">¿Cambiar foto?:</label> <input type="file" id="imagen" name="imagen" accept="image/*" class="form-control" style="display: block; width: 100%; box-sizing: border-box;"> 
					<small>Deja este campo vacío para mantener la foto actual.</small> 
				</div> 
			</div>
            <div class="form-group">
			    <label for="descripcion">Descripción: *</label>
			    <input type="text" id="descripcion" name="descripcion" 
		           required value="<%= c.getDescripcion() %>"
		           placeholder="Ej: Único dueño, siempre en garaje..."> 
			</div>

            <div class="form-buttons">
           
                <button type="submit" class="btn btn-success">💾 Actualizar Vehículo</button>
                <button type="reset" class="btn btn-secondary">🔄 Limpiar Formulario</button>
                <a href="ListarVehiculos" class="btn btn-back">❌ Cancelar</a>
                
            </div>
        </form>
    </div>
</body>
</html>