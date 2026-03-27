<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Coche" %>
<%@ page import="modelo.Usuario" %>
<%
    Usuario usu = (Usuario) session.getAttribute("usuarioLogueado");
    if (usu == null || usu.getRol().getNivel() > 2) {
        session.setAttribute("mensaje", "Acceso denegado: Área exclusiva para personal.");
        response.sendRedirect("login.jsp");
        return;
    }

    @SuppressWarnings("unchecked")
    List<Coche> lista = (List<Coche>) request.getAttribute("listaCoches");
    String busqueda = (String) request.getAttribute("busqueda");
    int paginaActual = (int) request.getAttribute("paginaActual");
    int totalPaginas = (int) request.getAttribute("totalPaginas");
    if (busqueda == null) busqueda = "";
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
            String mensaje = (String) session.getAttribute("mensaje");
            if (mensaje != null) {
        %>
            <div class="message message-success"><%= mensaje %></div>
        <%
                session.removeAttribute("mensaje");
            }
        %>
        <% if (usu != null) { %>
            <a href="alta-vehiculo.jsp" class="btn btn-success">➕ Nuevo Vehículo</a>
        <% } %>
    </div>

    <%-- BUSCADOR con búsqueda instantánea --%>
    <div class="search-form">
        <input type="text" id="inputBusqueda" value="<%= busqueda %>"
               placeholder="Buscar por marca, modelo, matrícula o motor...">
        <button id="btnLimpiar" class="btn btn-secondary"
                style="<%= busqueda.isEmpty() ? "display:none" : "" %>"
                onclick="limpiar()">Limpiar</button>
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
            <tbody id="cuerpoTabla">
                <% if (lista != null && !lista.isEmpty()) {
                    for (Coche v : lista) { %>
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
                        <a href="EliminarVehiculo?id=<%= v.getId() %>" class="row-btn btn-secondary btn-small"
                           onclick="return confirm('¿Estás seguro de querer eliminar este vehículo?')">Eliminar</a>
                    </td>
                </tr>
                <% } } else { %>
                <tr>
                    <td colspan="8" style="text-align:center;">No hay vehículos registrados todavía.</td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <%-- PAGINACIÓN --%>
    <div id="paginacion">
        <% if (totalPaginas > 1) { %>
            <% if (paginaActual > 1) { %>
                <a href="ListarVehiculos?pagina=<%= paginaActual - 1 %>&busqueda=<%= busqueda %>" class="btn btn-secondary">← Anterior</a>
            <% } %>
            <% for (int i = 1; i <= totalPaginas; i++) { %>
                <a href="ListarVehiculos?pagina=<%= i %>&busqueda=<%= busqueda %>"
                   class="btn <%= i == paginaActual ? "btn-primary" : "btn-secondary" %>">
                    <%= i %>
                </a>
            <% } %>
            <% if (paginaActual < totalPaginas) { %>
                <a href="ListarVehiculos?pagina=<%= paginaActual + 1 %>&busqueda=<%= busqueda %>" class="btn btn-secondary">Siguiente →</a>
            <% } %>
        <% } %>
    </div>

    <div class="nav-buttons">
        <a href="admin-panel.jsp" class="btn btn-back">Volver al Panel</a>
    </div>
</div>

<script>
    let timeout = null;
    const input = document.getElementById("inputBusqueda");
    const btnLimpiar = document.getElementById("btnLimpiar");

    // Búsqueda instantánea con debounce (espera 400ms tras dejar de escribir)
    input.addEventListener("input", function () {
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            buscar(input.value, 1);
            btnLimpiar.style.display = input.value ? "inline-block" : "none";
        }, 400);
    });

    function limpiar() {
        input.value = "";
        btnLimpiar.style.display = "none";
        buscar("", 1);
    }

    function buscar(busqueda, pagina) {
        fetch(`ListarVehiculos?formato=json&busqueda=${encodeURIComponent(busqueda)}&pagina=${pagina}`)
            .then(res => res.json())
            .then(data => {
                actualizarTabla(data.coches);
                actualizarPaginacion(data.totalPaginas, data.paginaActual, busqueda);
            });
    }

    function actualizarTabla(coches) {
        const tbody = document.getElementById("cuerpoTabla");
        if (coches.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;">No hay vehículos registrados todavía.</td></tr>`;
            return;
        }
        tbody.innerHTML = coches.map(v => `
            <tr>
                <td style="font-weight: bold; color: #764ba2;">${v.id}</td>
                <td>${v.matricula}</td>
                <td>${v.marca}</td>
                <td>${v.modelo}</td>
                <td>${v.anio}</td>
                <td>${v.color}</td>
                <td>${v.km.toLocaleString()}</td>
                <td class="actions">
                    <a href="DetalleCocheServlet?id=${v.id}&admin=true" class="row-btn btn-success btn-small">Ver más</a>
                    <a href="EditarVehiculo?id=${v.id}" class="row-btn btn-primary btn-small">Editar</a>
                    <a href="EliminarVehiculo?id=${v.id}" class="row-btn btn-secondary btn-small"
                       onclick="return confirm('¿Estás seguro de querer eliminar este vehículo?')">Eliminar</a>
                </td>
            </tr>`).join("");
    }

    function actualizarPaginacion(totalPaginas, paginaActual, busqueda) {
        const div = document.getElementById("paginacion");
        if (totalPaginas <= 1) { div.innerHTML = ""; return; }

        let html = "";
        if (paginaActual > 1)
            html += `<a href="#" onclick="buscar('${busqueda}', ${paginaActual - 1})" class="btn btn-secondary">← Anterior</a>`;
        for (let i = 1; i <= totalPaginas; i++)
            html += `<a href="#" onclick="buscar('${busqueda}', ${i})" class="btn ${i === paginaActual ? 'btn-primary' : 'btn-secondary'}">${i}</a>`;
        if (paginaActual < totalPaginas)
            html += `<a href="#" onclick="buscar('${busqueda}', ${paginaActual + 1})" class="btn btn-secondary">Siguiente →</a>`;

        div.innerHTML = html;
    }
</script>
</body>
</html>
