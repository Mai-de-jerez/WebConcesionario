<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Usuario" %>
<%
    // Sacamos al usuario de la sesión
    Usuario usuLogueado = (Usuario) session.getAttribute("usuarioLogueado");
    
    // Creamos un booleano: ¿Es el jefe o el empleado?
    boolean esPersonalTienda = (usuLogueado != null && usuLogueado.getRol().getNivel() <= 2);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pasarela de Pago Segura | Concesionario May</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container">
        <h1>💳 Confirmar Reserva y Pago</h1>

        <div style="background: #f9f9f9; padding: 20px; border-radius: 10px; margin-bottom: 30px; border-left: 5px solid #764ba2;">
            
            <h2 style="margin: 10px 0 0 0; color: #764ba2;">Vehículo: ${marcaCoche} ${modeloCoche}</h2>
            
            <h2 style="margin: 10px 0 0 0; color: #764ba2;">Total a pagar: ${precioCoche} €</h2>
        </div>

        <form action="FinalizarCompra" method="post">
            <input type="hidden" name="idCoche" value="${idCoche}">

            <div class="form-grid">
                
				<% if (!esPersonalTienda) { %>
				
					<%-- Importe a pagar --%>
					<div class="form-group">
	                    <label>¿Cuánto quieres pagar de señal?</label>
    					<input type="number" name="importeElegido" step="0.01" required> 
	                </div>
                
	                <%-- Número de Tarjeta --%>
	                <div class="form-group">
	                    <label for="tarjeta">Número de Tarjeta: *</label>
	                    <input type="text" id="tarjeta" name="tarjeta" 
	                           pattern="[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}" 
	                           placeholder="1234-5678-9012-3456" 
	                           title="Formato: 1111-2222-3333-4444" 
	                           required>
	                </div>
	
	                <%-- Caducidad --%>
	                <div class="form-group">
	                    <label for="caducidad">Caducidad (MM/YY): *</label>
	                    <input type="text" id="caducidad" name="caducidad" 
	                           pattern="(0[1-9]|1[0-2])\/([2][6-9]|[3-9][0-9])" 
	                           placeholder="08/26" 
	                           title="Mes/Año (Año 26 o superior)" 
	                           required>
	                </div>
	
	                <%-- CVV --%>
	                <div class="form-group">
	                    <label for="cvv">CVV: *</label>
	                    <input type="text" id="cvv" name="cvv" 
	                           pattern="[0-9]{3}" 
	                           maxlength="3" 
	                           placeholder="123" 
	                           title="3 dígitos de seguridad" 
	                           required>
	                </div>
					
		        <% } %>

            </div>

            <div class="form-buttons" style="margin-top: 30px;">
                <button type="submit" class="btn btn-success">✅ Reservar y Pagar</button>
                <a href="CancelarPedido?idCoche=${idCoche}" class="btn btn-back">❌ Cancelar Pago</a>
            </div>
        </form>
        
    </div>
</body>
</html>