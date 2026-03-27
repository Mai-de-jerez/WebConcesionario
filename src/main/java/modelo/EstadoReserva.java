package modelo;

public enum EstadoReserva {
    ACTIVA("Activa"), 
    EXPIRADA("Expirada"), 
    COMPLETADA("Completada"),
    CANCELADA("Cancelada");

    private final String texto;

    // Constructor del Enum
    EstadoReserva(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    // Método para encontrar el Enum por su texto
    public static EstadoReserva desdeTexto(String texto) {
        for (EstadoReserva ev : EstadoReserva.values()) {
            if (ev.texto.equalsIgnoreCase(texto)) {
                return ev;
            }
        }
        return ACTIVA; 
    }
}
