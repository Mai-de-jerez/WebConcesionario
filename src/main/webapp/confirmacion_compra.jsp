<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Usuario" %>
<%
    // Sacamos al usuario de la sesión para saber su nivel
    Usuario usuLogueado = (Usuario) session.getAttribute("usuarioLogueado");
    
    // Comprobamos si es personal (Nivel 1 o 2)
    boolean esPersonalTienda = (usuLogueado != null && usuLogueado.getRol().getNivel() <= 2);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>¡Compra Completada! | Concesionario May</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container" style="max-width: 700px; margin-top: 50px; text-align: center;">
        
        <h1 style="color: #28a745;">✨ ¡Enhorabuena, Compra Realizada!</h1>

        <div style="background: #f9f9f9; padding: 30px; border-radius: 10px; margin-bottom: 30px; border-left: 5px solid #28a745; box-shadow: 0 4px 15px rgba(0,0,0,0.05);">
            
            <div style="font-size: 60px; margin-bottom: 10px;">✅</div>
            
            <h2 style="color: #764ba2; margin-top: 0;">¡El coche ya es oficialmente tuyo!</h2>
            
            <p style="font-size: 1.1em; color: #555; line-height: 1.6;">
                Hemos recibido el pago correctamente. La operación se ha completado de forma segura y el vehículo ha sido retirado del catálogo para ti.
            </p>

            <div style="margin: 25px 0; padding: 15px; background: white; border-radius: 8px; display: inline-block; border: 1px solid #ddd;">
                <span style="color: #666;">Localizador de Pedido:</span> 
                <strong style="color: #764ba2; font-size: 1.2em; margin-left: 10px;">#${param.id}</strong>
            </div>
            
            <p style="color: #888; font-size: 0.9em;">
                Te hemos enviado un correo electrónico con los detalles del contrato y los pasos para la recogida en nuestras instalaciones.
            </p>
        </div>
        
        <div class="form-buttons" style="display: flex; justify-content: center; align-items: center; gap: 20px; margin-top: 30px;">
    
		    <a href="Tienda" class="btn btn-success" 
		       style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center; height: 45px; margin: 0;">
		       🛒 Volver a la Tienda
		    </a>
		    
		    <a href="ListarPedidos?origen=perfil" class="btn btn-back" 
		       style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center; height: 45px; margin: 0;">
		       <%= esPersonalTienda ? "📊 Ver mis ventas" : "📦 Ver mis pedidos" %>
		    </a>
		    
		</div>
        
        <p style="margin-top: 30px; color: #aaa; font-size: 0.8em;">
            Gracias por confiar en el <strong>Concesionario May</strong>. ¡Disfruta de tu nuevo coche!
        </p>
    </div>
</body>
</html>