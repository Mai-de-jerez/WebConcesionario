package modelo;

public enum EstadoPedido {
    PENDIENTE("Pendiente"), 
    ABONADO("Abonado"),
	CANCELADO("Cancelado"),
	EXPIRADO("Expirado");

    private final String texto;

    EstadoPedido(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    // Método para convertir el String de la base de datos al Enum de Java
    public static EstadoPedido desdeTexto(String texto) {
        if (texto == null) return PENDIENTE;
        for (EstadoPedido ep : EstadoPedido.values()) {
            if (ep.name().equalsIgnoreCase(texto) || ep.texto.equalsIgnoreCase(texto)) {
                return ep;
            }
        }
        return PENDIENTE; 
    }
}
