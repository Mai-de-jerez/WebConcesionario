// -- MANEJO DE NOTIFICACIONES --
function mostrarMensaje(texto, tipo = 'success') {
    const div = document.getElementById('notificacion');
    
    if (div) {
        div.textContent = texto;
        div.className = `message ${tipo}`; 
        div.style.display = 'block';
        setTimeout(() => {
            div.style.display = 'none';
        }, 4000);
    }
}

// -- MANEJO DE ESTADOS DE CARGA, VACÍO Y ERROR --
function setLoading(colspan = 7) {
    const tbody = document.getElementById('cuerpoTabla');
    if (tbody) {
        tbody.innerHTML = `<tr>
            <td colspan="${colspan}" style="text-align:center;">
                Cargando...
            </td>
        </tr>`;
    }
}

function setVacio(mensaje = "No hay elementos que mostrar.", colspan = 7) {
    const tbody = document.getElementById('cuerpoTabla');
    if (tbody) {
        tbody.innerHTML = `<tr>
            <td colspan="${colspan}" style="text-align:center;">
                ${mensaje}
            </td>
        </tr>`;
    }
}

function setError(mensaje) {
    mostrarMensaje(mensaje, 'error');
}


function setLoadingTienda() {
    document.getElementById('contenedor-grid').innerHTML = '<p class="cargando">Cargando libros... 🦎</p>';
}

function setErrorTienda() {
    document.getElementById('contenedor-grid').innerHTML = '<p>Error al conectar.</p>';
}

function setVacioTienda() {
    document.getElementById('contenedor-grid').innerHTML = '<p>No hay libros disponibles.</p>';
}