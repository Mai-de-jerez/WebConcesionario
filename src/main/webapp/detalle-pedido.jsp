<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Pedido" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="modelo.Coche" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    // 1. Recuperamos los objetos
    Pedido p = (Pedido) request.getAttribute("pedido");
    Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
    Usuario cliente = (Usuario) request.getAttribute("cliente");
    modelo.Coche coche = (modelo.Coche) request.getAttribute("coche");
    
    
    // 2. Buscamos el origen
    String origen = (String) request.getAttribute("origen");
    if (origen == null) {
        origen = request.getParameter("origen");
    }
      
 	// 3. Declaramos las variables para el modo y saber si es personal o no
    boolean esModoPerfil = "perfil".equals(origen);
    boolean esPersonal = (usu != null && usu.getRol().getNivel() <= 2);

    // Seguridad
    if (p == null) {
        response.sendRedirect("ListarPedidos" + (esModoPerfil ? "?origen=perfil" : ""));
        return;
    }
 	// 4. CÁLCULOS (Solo si tenemos coche, para evitar errores)
    double precioTotalCoche = 0;
    double abonado = p.getImporteAbonado();
    double pendiente = 0;

    if (coche != null) {
        precioTotalCoche = coche.getPrecio();
        pendiente = precioTotalCoche - abonado;
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

                                
                                <tr><td colspan="2" style="background-color: #eee; height: 1px; padding: 0;"></td></tr>
                                
                                <tr><td><strong>Cliente</strong></td><td style="color: #764ba2; font-weight: bold;"><%= cliente.getNombre() %> <%= cliente.getApellidos() %></td></tr>
								<tr><td><strong>Email</strong></td><td><%= cliente.getEmail() %></td></tr>
								<tr><td><strong>Teléfono</strong></td><td><%= (cliente.getTelefono() != null) ? cliente.getTelefono() : "No facilitado" %></td></tr>
								<tr>
								    <td><strong>Vehículo</strong></td>
								    <td><%= (coche != null) ? (coche.getMarca() + " " + coche.getModelo()) : "Coche no identificado" %></td>
								</tr>
								
								<tr>
								    <td><strong>Precio Total Coche</strong></td>
								    <td><%= String.format("%.2f", precioTotalCoche) %> €</td>
								</tr>
								
								<tr style="background-color: #fff9c4;"> <%-- Un color amarillito para que destaque el dinero que falta --%>
								    <td><strong style="color: #bc5100;">RESTANTE POR PAGAR</strong></td>
								    <td style="font-size: 1.2em; font-weight: bold; color: #bc5100;">
								        <%= String.format("%.2f", pendiente) %> €
								    </td>
								</tr>
								<tr><td colspan="2" style="background-color: #eee; height: 1px; padding: 0;"></td></tr>
								
								<%-- Datos que SÍ son del pedido (p) --%>
								<tr><td><strong>Fecha Reserva</strong></td><td><%= (p.getFechaReserva() != null) ? sdf.format(p.getFechaReserva()) : "---" %></td></tr>
								<tr><td><strong>ID Transacción</strong></td><td style="font-family: monospace; font-size: 0.85em;"><%= p.getTransaccionId() != null ? p.getTransaccionId() : "N/A" %></td></tr>
								<tr><td><strong>Total Abonado</strong></td><td style="font-size: 1.2em; font-weight: bold; color: #764ba2;"><%= String.format("%.2f", p.getImporteAbonado()) %> €</td></tr>
								                                
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