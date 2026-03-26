package modelo;

public enum EstadoVehiculo {
    DISPONIBLE("Disponible"), 
    RESERVADO("Reservado"), 
    VENDIDO("Vendido");

    private final String texto;

    // Constructor del Enum
    EstadoVehiculo(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    // Método para encontrar el Enum por su texto
    public static EstadoVehiculo desdeTexto(String texto) {
        for (EstadoVehiculo ev : EstadoVehiculo.values()) {
            if (ev.texto.equalsIgnoreCase(texto)) {
                return ev;
            }
        }
        return DISPONIBLE;
    }
}
