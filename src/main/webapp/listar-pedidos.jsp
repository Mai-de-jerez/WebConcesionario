<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Pedido" %>
<%@ page import="modelo.Usuario" %> 
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
    // si no hay nadie logueado al login lo mando
    if (usu == null) {
        response.sendRedirect("login.jsp");
        return; 
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    String origen = (String) request.getAttribute("origen");
    if (origen == null) {
        origen = request.getParameter("origen");
    }
    
    // Si estamos venimos de nuestro perfil
    boolean esModoPerfil = "perfil".equals(origen);

    // Si es nivel 1 o 2 es personal
    boolean esPersonal = (usu.getRol().getNivel() <= 2);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista de Pedidos - Concesionario May</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container">
        
        <div class="header-actions">
	
		    <h1>
                <%= (esModoPerfil && esPersonal) ? "📊 Mis Ventas Realizadas" : (esModoPerfil ? "📦 Mis Pedidos" : "📋 Listado de Pedidos") %>
            </h1>
	
		    <% if (!esModoPerfil && usu.getRol().getNivel() <= 2) { %>
		        <a href="Tienda" class="btn btn-success">➕ Nuevo Pedido</a>
		    <% } %>
		</div>
  
       
        <div class="table-container">
            <table id="tablaPedidos">
                <thead>
                    <tr>
                        <th><%= esPersonal ? "ID Venta" : "Num. Pedido" %></th>
                        <th>ID Cliente</th>
                        <th>ID Coche</th>
                        <th>Fecha de Reserva</th>
                        <th>Total Abonado</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        // Recuperamos la lista que viene del Servlet ListarPedidos
                        @SuppressWarnings("unchecked")
                        List<Pedido> lista = (List<Pedido>) request.getAttribute("listaPedidos");
                        
                        if (lista != null && !lista.isEmpty()) {
                            for (Pedido p : lista) {
                    %>
                    <tr>
                        <td style="font-weight: bold; color: #764ba2;"><%= p.getId() %></td>					    
						<td style="font-weight: bold; color: #764ba2;"><%= p.getIdUsuario() %></td>
						<td style="font-weight: bold; color: #764ba2;"><%= p.getIdCoche() %></td>					
					    <td><%= (p.getFechaReserva() != null) ? sdf.format(p.getFechaReserva()) : "---" %></td>
                        <td style="font-weight: bold;"><%= String.format("%.2f", p.getImporteAbonado()) %> €</td>
                        <td>
                            <span class="message message-success" style="padding: 5px 10px; font-size: 0.8em;">
                                <%= p.getEstado().getTexto() %>
                            </span>
                        </td>	
						
						<td class="actions" style="text-align: center;">
    
						    <%-- 1. VER MÁS: Este lo ven todos los usuarios siempre --%>
						    <a href="DetallePedido?id=<%= p.getId() %><%= esModoPerfil ? "&origen=perfil" : "" %>" 
						       class="row-btn btn-success btn-small">Ver más</a>
						
						    <% 
						        // 2. ELIMINAR: Solo si es Super User (Nivel 1) y NO estamos en vista de perfil
						        if (usu.getRol().getNivel() == 1 && !esModoPerfil) { 
						    %>
						        <a href="EliminarPedido?id=<%= p.getId() %>" 
						           class="row-btn btn-secondary btn-small" 
						           style="background-color: #d9534f;" <%-- Un color rojizo para que sepa que es una acción crítica --%>
						           onclick="return confirm('¿Seguro que quieres eliminar el pedido nº <%= p.getId() %> y liberar el coche?')">
						           Cancelar pedido
						        </a>
						    <% } %>
						    
						</td>				
                    </tr>
                    <% 
                            }
                        } else { 
                            // Calculamos cuántas columnas mostrar en el mensaje de error
                            int columnas = (usu.getRol().getNivel() <= 2) ? 7 : 6;
                    %>
                    <tr>
                        <td colspan="<%= columnas %>" style="text-align:center;">
                        	<%= (esModoPerfil && esPersonal) ? "Todavía no has registrado ninguna venta." : "No se han encontrado registros." %>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

       
        <div class="nav-buttons">
		    <% if (esModoPerfil) { %>
		        <%-- Si estamos viendo nuestros pedidos, volvemos al perfil --%>
		        <a href="perfil.jsp" class="btn btn-back">⬅️ Volver a mi Perfil</a>
		    <% } else { %>
		        <%-- Si estamos en gestión, volvemos al panel de control --%>
		        <a href="admin-panel.jsp" class="btn btn-back">⬅️ Volver al Panel de Administración</a>
		    <% } %>
		</div>
    </div>
</body>
</html>