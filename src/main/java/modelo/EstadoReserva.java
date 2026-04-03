package modelo;


public enum EstadoReserva {
    ACTIVA("Activa"), 
    FINALIZADA("Finalizada"),
	CANCELADA("Cancelada"),
	EXPIRADA("Expirada");
	

    private final String texto;

    EstadoReserva(String texto) {
        this.texto = texto;
    }
 
    public String getTexto() {
        return texto;
    }

    // Método para convertir el String de la base de datos al Enum de Java
    public static EstadoReserva desdeTexto(String texto) {
        if (texto == null) return ACTIVA;
        for (EstadoReserva er : EstadoReserva.values()) {
            if (er.name().equalsIgnoreCase(texto) || er.texto.equalsIgnoreCase(texto)) {
                return er;
            }
        }
        return ACTIVA; 
    }
}
