// utils.js
// -- MANEJO DE MENSAJES DE EXITO ERROR --


function mostrarMensaje(texto, tipo = 'success') {
    const div = document.getElementById('notificacion');
    
    if (div) {
        div.textContent = texto;
        div.className = `message message-${tipo}`; 
        div.style.display = 'block';

        div.scrollIntoView({ behavior: 'smooth', block: 'start' });

        setTimeout(() => {
            div.style.display = 'none';
        }, 4000);
    }
}

// --- MANEJO DE ERRORES ESPECÍFICOS EN CAMPOS ---

function mostrarErrorEnCampo(idCampo, texto) {
    const campo = document.getElementById(idCampo);
    const spanError = document.getElementById('error-' + idCampo);
    if (!campo) return;

    campo.classList.add('input-error');
    if (spanError) {
        spanError.textContent = "❌ " + texto;
    }
}

function limpiarPantallaErrores() {
    document.querySelectorAll('.mensaje-error-campo').forEach(span => span.textContent = "");
    document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
}

function configurarLimpiezaErrores(selectorForm) {
    const form = document.querySelector(selectorForm);
    if (!form) return;

    const campos = form.querySelectorAll('input, textarea, select');
    campos.forEach(campo => {
        campo.addEventListener('input', function() {
            this.classList.remove('input-error');
            const spanError = document.getElementById('error-' + this.id);
            if (spanError) spanError.textContent = "";
        });
    });
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