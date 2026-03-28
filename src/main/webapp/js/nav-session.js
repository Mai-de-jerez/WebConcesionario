document.addEventListener("DOMContentLoaded", () => {
    gestionarNavegacion();
});

async function gestionarNavegacion() {
    const navRight = document.getElementById("nav-sesion");
    
    try {
        // Llamamos al Auth_Sv para ver quién está logueado
        const res = await fetch('Login');
        
        if (res.ok) {
            const usuario = await res.json();
            
            // ADMIN O STAFF (Nivel <= 2)
            if (usuario.nivel <= 2) {
                navRight.innerHTML = `
                    <a href="admin-panel.html" class="btn-nav">Panel Admin</a>
                    <a href="LogoutServlet" class="nav-link" style="margin-left:10px;">Salir</a>
                `;
            } 
            // CLIENTE (Nivel > 2)
            else {
                navRight.innerHTML = `
                    <span class="nav-link">Hola, ${usuario.nombre}</span>
                    <a href="perfil.html" class="nav-link" style="margin: 0 15px;">Mi Perfil</a>
                    <a href="LogoutServlet" class="btn-nav">Cerrar Sesión</a>
                `;
            }
        } else {
            // NADIE LOGUEADO (Error 401 o 403)
            navRight.innerHTML = `<a href="login.html" class="btn-nav">Login</a>`;
        }
    } catch (err) {
        // Si el servidor está caído o hay error de red
        navRight.innerHTML = `<a href="login.html" class="btn-nav">Login</a>`;
    }
}