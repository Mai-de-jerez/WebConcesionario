<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="modelo.Coche" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Usuario" %>
<%
    // RECUPERAMOS LOS DATOS QUE EL SERVLET TIENDA NOS HA DEJADO PREPARADOS
    @SuppressWarnings("unchecked")
    List<Coche> listaCoches = (List<Coche>) request.getAttribute("listaCoches");
    
    // Recuperamos al usuario de la sesión para la personalización
    Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tienda | Concesionario May</title>
    <link rel="stylesheet" href="styles.css">
</head>
	<body class="home-body">
	
	    <nav class="navbar">
	        <div class="nav-left">
	            <a href="index.jsp" class="nav-link">Inicio</a>
	            <a href="Tienda" class="nav-link">Tienda</a>
	            <a href="sobre-nosotros.jsp" class="nav-link">Sobre Nosotros</a>
	            <a href="contacto.jsp" class="nav-link">Contacto</a>
	        </div>
	     		     	
	     	<div class="nav-right">
	            <% 
	                if (usu == null) { 
	                    // CASO 1: Nadie logueado
	            %>
	                <a href="login.jsp" class="btn-nav">Login</a>
	            <% 
	                } else if (usu.getRol().getNivel() <= 2) { 
	                    // CASO 2: Es SUPERUSER (Nivel 1) o EMPLEADO (Nivel 2) - Opciones de admin
	            %>
	            	<a href="perfil.jsp" class="nav-link" style="margin-right:10px;">Mi Perfil</a>
	                <a href="admin-panel.jsp" class="btn-nav">Admin</a>
	                <a href="LogoutServlet" class="nav-link" style="margin-left:10px;">Salir</a>
	            <% 
	                } else { 
	                	// CASO 3: Es CLIENTE (3) - Solo opciones de usuario
	            %>
	                <span class="nav-link">Hola, <%= usu.getUsuario() %></span>
	                <a href="perfil.jsp" class="nav-link" style="margin: 0 15px;">Mi Perfil</a>
	                <a href="LogoutServlet" class="btn-nav">Cerrar Sesión</a>
	            <% 
	                } 
	            %>
        	</div>

	    </nav>
	
	    <main class="content-area">
	        <section class="hero">
	            <h1>Tienda de Vehículos</h1>
	            <p>Explora nuestro catálogo y añade tus favoritos a la cesta.</p>
	        </section>
	
	        <div class="container">
	            <h1>🚗 Unidades Disponibles</h1>
	            
	            <div class="welcome-section">
	                 <p>Haz clic en "Comprar" para añadir tu vehículo al carrito.</p>
	            </div>
	            
	            <div class="feature-cards-shop">
	                <%
	                    // 2. RECORREMOS LA LISTA 
	                    if (listaCoches != null) {
	                        for (Coche c : listaCoches) {
	                            if (c.isDisponible()) {
	                                // Lógica de vista mínima: decidir qué foto poner
	                                String fotoLista = (c.getImagen() == null || c.getImagen().isEmpty()) ? "sin-foto.png" : c.getImagen();
	                %>
	                                <div class="feature-card">
	                                    <img src="img/<%= fotoLista %>" 
	                                         alt="<%= c.getModelo() %>" 
	                                         style="width:100%; border-radius:10px; height:180px; object-fit:cover; margin-bottom:15px;">
	                                    
	                                    <h3><%= c.getMarca() %> <%= c.getModelo() %></h3>
	                                    <p><%= c.getTipoMotor().getTexto() %> | <%= c.getKm() %> km</p>
	                                    <p style="font-weight: bold; color: #764ba2; font-size: 1.3em; margin: 10px 0;">
	                                        <%= c.getPrecio() %> €
	                                    </p>
	                                    
	                                     <div class="actions" style="justify-content: center; flex-direction: column;">
	                                        <a href="DetalleCocheServlet?id=<%= c.getId() %>" class="btn btn-secondary btn-small">Ver más</a>
	                                        
	                                        <% if (usu == null) { %>
	                                            <a href="login.jsp?origen=compra&idRegreso=<%= c.getId() %>" class="btn btn-success btn-small" style="background-color: #6c757d;">
	                                                🛒 Comprar
	                                            </a>
	                                        <% } else { %>
	                                            <a href="Carrito?idCoche=<%= c.getId() %>&precioCoche=<%= c.getPrecio() %>" 
	                                               class="btn btn-success btn-small">🛒 Comprar</a>
	                                               
	                                        <% } %>
	                                        
	                                    </div>
	                                </div>
	                <% 
	                            }
	                        } 
	                    } else { %>
	                        <p>No se han podido cargar los vehículos. Por favor, accede a través de la sección Tienda.</p>
	                <%  } %>
	            </div>
	        </div>
	    </main>
	
	    <footer class="main-footer">
	        <p>&copy; 2026 Concesionario May. Todos los derechos reservados.</p>
	    </footer>
	</body>
</html>


