package modelo;

public enum EstadoVenta { 
	PENDIENTE("Pendiente"),
    FINALIZADA("Finalizada"),
    PENALIZADA("Penalizada"),
	CANCELADA("Cancelada");
	

    private final String texto;

    EstadoVenta(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    // Método para convertir el String de la base de datos al Enum de Java
    public static EstadoVenta desdeTexto(String texto) {
        if (texto == null) return PENDIENTE;
        for (EstadoVenta ep : EstadoVenta.values()) {
            if (ep.name().equalsIgnoreCase(texto) || ep.texto.equalsIgnoreCase(texto)) {
                return ep;
            }
        }
        return PENDIENTE; // Valor por defecto 
    }
}
