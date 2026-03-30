// --- PATRONES REGEX ---
const regexMatricula = /^[0-9]{4}[A-Z]{3}$/;
const regexTexto = /^[a-zA-ZÀ-ÿ0-9\s\-\.\,\(\)\[\]]{3,100}$/;
const regexPrecio = /^\d+(\.\d{1,2})?$/;
const esTextoValido = (texto) => /^[a-zA-ZÀ-ÿ0-9@\s\-\.\,\:\;\!\?\(\)\'\"]{3,100}$/.test(texto.trim());
const esEmailValido = (email) => /^[\w.-]+@[\w.-]+\.[a-z]{2,8}$/.test(email.trim());
const esPasswordSegura = (pass) => /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/.test(pass);

// --- VALIDACIÓN DE ARCHIVOS  ---
const validarArchivoFoto = (elInput) => {
    if (!elInput || !elInput.files || elInput.files.length === 0) return { valido: true, msg: "" };
    
    const archivo = elInput.files[0];
    const tiposPermitidos = ['image/jpeg', 'image/png', 'image/webp'];
    const tamañoMax = 2 * 1024 * 1024; // 2MB

    if (!tiposPermitidos.includes(archivo.type)) {
        return { valido: false, msg: "Solo se permiten jpg, png y webp." };
    }
    if (archivo.size > tamañoMax) {
        return { valido: false, msg: "La foto no puede superar 2MB." };
    }
    return { valido: true, msg: "" };
};

// --- FUNCIÓN PRINCIPAL DE VALIDACIÓN DE COCHES ---
function validarFormularioCoche() {
    limpiarPantallaErrores();
    let esValido = true;

    // Capturamos campos
    const matricula = document.getElementById('matricula');
    const marca = document.getElementById('marca');
    const modelo = document.getElementById('modelo');
    const precio = document.getElementById('precio');
    const km = document.getElementById('km');
    const anio = document.getElementById('anio');
	const color = document.getElementById('color');
	const tipo_motor = document.getElementById('tipoMotor');
	const num_puertas = document.getElementById('numPuertas');
	const estado = document.getElementById('estado');
    const desc = document.getElementById('descripcion');
    const img = document.getElementById('imagen');
    
    // Matrícula
    if (!regexMatricula.test(matricula.value.toUpperCase().trim())) {
        mostrarErrorEnCampo('matricula', "Formato inválido (Ej: 1234BBB)");
        if (esValido) matricula.focus();
        esValido = false;
    }

    // Marca y Modelo
    if (!regexTexto.test(marca.value)) {
        mostrarErrorEnCampo('marca', "Marca obligatoria (3-50 caracteres)");
        if (esValido) marca.focus();
        esValido = false;
    }
    if (!regexTexto.test(modelo.value)) {
        mostrarErrorEnCampo('modelo', "Modelo obligatorio (2-50 caracteres)");
        if (esValido) modelo.focus();
        esValido = false;
    }

    // Precio 
    if (parseFloat(precio.value) <= 0 || !regexPrecio.test(precio.value)) {
        mostrarErrorEnCampo('precio', "Introduce un precio válido mayor que 0");
        if (esValido) precio.focus();
        esValido = false;
    }
	
	//km
    if (parseInt(km.value) < 0 || km.value === "") {
        mostrarErrorEnCampo('km', "Kilómetros no pueden ser negativos");
        if (esValido) km.focus();
        esValido = false;
    }

    // Año
    const anioActual = new Date().getFullYear();
    const a = parseInt(anio.value);
    if (isNaN(a) || a < 1900 || a > anioActual + 1) {
        mostrarErrorEnCampo('anio', `Año entre 1900 y ${anioActual + 1}`);
        if (esValido) anio.focus();
        esValido = false;
    }
	
	// Color
    if (!regexTexto.test(color.value)) {
        mostrarErrorEnCampo('color', "Color obligatorio (3-50 caracteres)");
        if (esValido) color.focus();
        esValido = false;
    }
	
    // Tipo de motor
    if (!tipo_motor.value || tipo_motor.value === "") {
        mostrarErrorEnCampo('tipoMotor', "Selecciona un motor");
        if (esValido) tipo_motor.focus();
        esValido = false;
    }
	
    // Número de puertas
    if (!num_puertas.value || num_puertas.value === "") {
        mostrarErrorEnCampo('numPuertas', "Selecciona n.º de puertas");
        if (esValido) num_puertas.focus();
        esValido = false;
    }
	
    // Estado 
    if (!estado.value || estado.value === "") {
        mostrarErrorEnCampo('estado', "Selecciona disponibilidad");
        if (esValido) estado.focus();
        esValido = false;
    }
	
	

    // Imagen
    const checkImg = validarArchivoFoto(img);
    if (!checkImg.valido) {
        mostrarErrorEnCampo('imagen', checkImg.msg);
        if (esValido) img.focus();
        esValido = false;
    }

    // Descripción
    if (desc.value.trim().length < 10) {
        mostrarErrorEnCampo('descripcion', "Descripción demasiado corta (mín. 10 carac.)");
        if (esValido) desc.focus();
        esValido = false;
    }

    return esValido;
}

// --- VALIDADOR PARA LOGIN ---
function validarFormularioLogin() {
    limpiarPantallaErrores(); 
    let esValido = true;

    const elUsuario = document.getElementById('usuario');
    const elPassword = document.getElementById('password');

    const errorUsuario = validarNombre(elUsuario);
    if (errorUsuario) {
        mostrarErrorEnCampo('usuario', errorUsuario);
        if (esValido) elUsuario.focus();
        esValido = false;
    }

    const errorPass = validarPassword(elPassword);
    if (errorPass) {
        mostrarErrorEnCampo('password', errorPass);
        if (esValido) elPassword.focus();
        esValido = false;
    }

    return esValido;
}


function validarFormularioUsuarios() {
    limpiarPantallaErrores();
    let esValido = true;

	const elNombre = document.getElementById('nombre');
	const elApellidos = document.getElementById('apellidos');
    const elUsuario = document.getElementById('usuario');
    const elEmail = document.getElementById('email');
    const elPassword = document.getElementById('password');
    const elRol = document.getElementById('rol');
	const elTelefono = document.getElementById('telefono');
	const elDireccion = document.getElementById('direccion');
    const elFoto = document.getElementById('foto');
    const accion = document.getElementById('accion-form').value;

	// Nombre
	if (!esTextoValido(elNombre.value)) {
	    mostrarErrorEnCampo('nombre', "Nombre obligatorio (3-100 caracteres)");
	    if (esValido) elNombre.focus();
	    esValido = false;
	}

	// Apellidos
	if (!esTextoValido(elApellidos.value)) {
	    mostrarErrorEnCampo('apellidos', "Apellidos obligatorios (3-100 caracteres)");
	    if (esValido) elApellidos.focus();
	    esValido = false;
	}

    // Usuario
    const errorUsuario = validarNombre(elUsuario);
    if (errorUsuario) {
        mostrarErrorEnCampo('usuario', errorUsuario);
        if (esValido) elUsuario.focus();
        esValido = false;
    }

    // Email
    const errorEmail = validarEmail(elEmail);
    if (errorEmail) {
        mostrarErrorEnCampo('email', errorEmail);
        if (esValido) elEmail.focus();
        esValido = false;
    }

    // Password
    if (accion === 'alta' || elPassword.value.length > 0) {
        const errorPass = validarPassword(elPassword);
        if (errorPass) {
            mostrarErrorEnCampo('password', errorPass);
            if (esValido) elPassword.focus();
            esValido = false;
        }
    }

    // Rol
    if (!elRol.value || elRol.value === "") {
        mostrarErrorEnCampo('rol', "Selecciona un rol");
        if (esValido) elRol.focus();
        esValido = false;
    }
	

	// Teléfono
	if (!/^[67]\d{8}$/.test(elTelefono.value.trim())) {
	    mostrarErrorEnCampo('telefono', "Teléfono obligatorio (9 dígitos, empieza por 6 o 7)");
	    if (esValido) elTelefono.focus();
	    esValido = false;
	}

	// Dirección
	if (elDireccion.value.trim().length < 5) {
	    mostrarErrorEnCampo('direccion', "Dirección obligatoria (mín. 5 caracteres)");
	    if (esValido) elDireccion.focus();
	    esValido = false;
	}

    // Foto
    const checkFoto = validarArchivoFoto(elFoto);
    if (!checkFoto.valido) {
        mostrarErrorEnCampo('foto', checkFoto.msg);
        if (esValido) elFoto.focus();
        esValido = false;
    }

    return esValido;
}



// --- FUNCIONES AUXILIARES ---
function validarNombre(input) {
    const valor = input.value.trim();

    if (valor.length < 3 || valor.length > 100) {
        input.classList.add('input-error');
        return "El nombre debe tener entre 3 y 100 caracteres.";
    }

    if (!esTextoValido(valor)) {
        input.classList.add('input-error');
        return "Has introducido caracteres especiales no aceptados.";
    }

    return "";
}

function validarEmail(input) {
    if (!esEmailValido(input.value)) {
        input.classList.add('input-error');
        return "Formato de email no válido.";
    }
    return "";
}

function validarPassword(input) {
    if (!esPasswordSegura(input.value)) {
        input.classList.add('input-error');
        return "Mínimo 6 caracteres, una mayúscula y un número.";
    }
    return "";
}

function validarConfirmPassword(pass, confirm) {
    if (pass.value !== confirm.value || confirm.value === "") {
        confirm.classList.add('input-error');
        return "Las contraseñas no coinciden.";
    }
    return "";
}