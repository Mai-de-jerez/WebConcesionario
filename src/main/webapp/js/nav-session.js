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
            
            // botón de Administrar (solo si nivel es 1 o 2)
            let botonAdmin = "";
            if (datos.nivel !== undefined && datos.nivel <= 2) {
                botonAdmin = `<a href="admin?vista=panel" class="nav-link">Administrar</a>`;
            }

            // pintamos la barra para los logueados (Admin, Staff y Cliente)
            navRight.innerHTML = `
                <span class="nav-link purple-bold">Hola, ${datos.nombre || 'Usuario'}</span>
                <a href="perfil.html" class="nav-link">Mi Perfil</a>
				${botonAdmin}
                <a href="LogoutServlet" class="btn-nav">Cerrar Sesión</a>
            `;
            
        } else {				
            // Si no hay sesión al login
            navRight.innerHTML = `<a href="login.html" class="btn-nav">Login</a>`;
        }
    } catch (err) {
        // Si hay error de red, botón de login por seguridad
        navRight.innerHTML = `<a href="login.html" class="btn-nav">Login</a>`;
    }
}