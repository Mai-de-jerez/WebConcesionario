// --- PATRONES REGEX ---
const regexMatricula = /^[0-9]{4}[A-Z]{3}$/;
const regexTexto = /^[a-zA-ZÀ-ÿ0-9\s\-\.\,\(\)\[\]]{3,100}$/;
const regexPrecio = /^\d+(\.\d{1,2})?$/;

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