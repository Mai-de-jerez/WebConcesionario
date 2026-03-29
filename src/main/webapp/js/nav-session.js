// js/nav-session.js
document.addEventListener("DOMContentLoaded", () => {
    gestionarNavegacion();
});

async function gestionarNavegacion() {
    const navRight = document.getElementById("nav-sesion");
    
    try {
        const res = await fetch('Login', {
            headers: { 'Accept': 'application/json' }
        });
        
        if (res.ok) {
            const datos = await res.json();
            
            // Usamos directamente 'nivel' que viene del Servlet
            if (datos.nivel !== undefined && datos.nivel <= 2) {
                // ADMIN O STAFF
                navRight.innerHTML = `
					<a href="admin?vista=panel" class="btn-nav">Panel Admin</a>
                    <a href="LogoutServlet" class="nav-link" style="margin-left:10px;">Salir</a>
                `;
            } else {
                // CLIENTE
                navRight.innerHTML = `
                    <span class="nav-link">Hola, ${datos.nombre || 'Usuario'}</span>
                    <a href="perfil.html" class="nav-link" style="margin: 0 15px;">Mi Perfil</a>
                    <a href="LogoutServlet" class="btn-nav">Cerrar Sesión</a>
                `;
            }
        } else {
            // Si no hay sesión (401), botón de login
            navRight.innerHTML = `<a href="login.html" class="btn-nav">Login</a>`;
        }
    } catch (err) {
        // Si hay error de red, botón de login por seguridad
        navRight.innerHTML = `<a href="login.html" class="btn-nav">Login</a>`;
    }
}