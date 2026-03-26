<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Pedido" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    // 1. Recuperamos los objetos
    Pedido p = (Pedido) request.getAttribute("pedido");
    Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
    
    // 2. Buscamos el origen
    String origen = (String) request.getAttribute("origen");
    if (origen == null) {
        origen = request.getParameter("origen");
    }
    
 // 3. Declaramos las variables para el modo y saber si es personal o no
    boolean esModoPerfil = "perfil".equals(origen);
    boolean esPersonal = (usu != null && usu.getRol().getNivel() <= 2);
    boolean esPersonalGestion = (esPersonal && !esModoPerfil);

    // Seguridad
    if (p == null) {
        response.sendRedirect("ListarPedidos" + (esModoPerfil ? "?origen=perfil" : ""));
        return;
    }
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= (esModoPerfil && esPersonal) ? "Detalle Venta" : "Detalle Pedido" %> #<%= p.getId() %></title>
    <link rel="stylesheet" href="styles.css">
</head>
<body class="home-body">
    <main class="content-area">

        <div class="container" style="max-width: 900px;">
            <h2 style="color: #764ba2; margin-bottom: 20px;">
                📄 <%= (esModoPerfil && esPersonal) ? "Resumen de Venta" : "Resumen del Pedido" %> #<%= p.getId() %>
            </h2>
            
            <div style="display: flex; flex-wrap: wrap; gap: 40px; align-items: start;">
                
                <%-- Bloque de la Imagen del Coche Vendido --%>
                <div style="flex: 1; min-width: 300px;">
                    <div class="feature-card" style="padding: 10px; cursor: default;">
                        <% 
                            String nombreFoto = p.getImagen(); 
                            if (nombreFoto == null || nombreFoto.isEmpty()) {
                                nombreFoto = "sin-foto.png";
                            }
                        %>
                        <img src="img/<%= nombreFoto %>" 
                             alt="Imagen del vehículo" 
                             style="width:100%; border-radius:15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); min-height: 200px; object-fit: cover;">
                        <p style="text-align: center; margin-top: 10px; color: #666; font-style: italic;">Vehículo asociado a la venta</p>
                    </div>
                </div>

                <%-- Bloque de Datos del Pedido --%>
                <div style="flex: 1.2; min-width: 300px;">
                    
                    <div class="table-container" style="margin: 0;">
                        <table style="font-size: 0.95em;">
                            <tbody>
                                <tr style="background-color: #f8f9fa;">
                                    <td><strong>Estado del Pago</strong></td>
                                    <td>
                                        <span class="message-success" style="padding: 5px 15px; border-radius: 5px; font-weight: bold;">
                                            <%= p.getEstado().getTexto() %>
                                        </span>
                                    </td>
                                </tr>
                                
                                <tr><td>Cliente</td><td style="color: #764ba2; font-weight: bold;"><%= p.getNombreUsuario() %></td></tr>
                                <tr><td>Email</td><td><%= p.getEmailUsuario() %></td></tr>
                                <tr><td>Teléfono</td><td><%= (p.getTelefono() != null) ? p.getTelefono() : "No facilitado" %></td></tr>
                                
                                <tr><td colspan="2" style="background-color: #eee; height: 1px; padding: 0;"></td></tr>
                                
                                <tr><td>Fecha Compra</td><td><%= (p.getFecha() != null) ? sdf.format(p.getFecha()) : "---" %></td></tr>
                                <tr><td>ID Transacción</td><td style="font-family: monospace; font-size: 0.85em;"><%= p.getTransaccionId() %></td></tr>
                                <tr><td>Total Abonado</td><td style="font-size: 1.2em; font-weight: bold; color: #764ba2;"><%= String.format("%.2f", p.getTotal()) %> €</td></tr>
                                
                                <tr>
                                    <td>Observaciones</td>
                                    <td style="font-style: italic; color: #555;">
                                        <%= (p.getObservaciones() != null && !p.getObservaciones().isEmpty()) ? p.getObservaciones() : "Sin notas adicionales." %>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>     
                                                                                           
                </div>

                <div class="form-buttons" style="display: flex; justify-content: center; gap: 20px;  width: 100%;">
    
				    <a href="ListarPedidos<%= esModoPerfil ? "?origen=perfil" : "" %>" class="btn btn-back">
				        <%= (esModoPerfil && esPersonal) ? "⬅️ Volver a mis Ventas" : (esModoPerfil ? "⬅️ Volver a mis Pedidos" : "⬅️ Volver a la Lista General") %>
				    </a>
				</div>
            </div>
        </div>
    </main>
</body>
</html>