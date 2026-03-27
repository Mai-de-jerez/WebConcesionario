function mostrarMensaje(texto, tipo = 'success') {
    const div = document.createElement('div');

    div.className = `mensaje-alerta ${tipo}`; 
    div.textContent = texto;

    const contenedor = document.querySelector('.container');
    if (contenedor) {
        contenedor.prepend(div);

        setTimeout(() => div.remove(), 4000);
    }
}