package modelo;

public enum TipoMotor {

	GASOLINA("Gasolina"),
	DIESEL("Diésel"),
	ELECTRICO("Eléctrico"),
	HIBRIDO("Híbrido");
	

    private final String texto;

    // Constructor del Enum
    TipoMotor(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
    
    
    public static TipoMotor desdeTexto(String texto) {
        if (texto == null) return GASOLINA;

        for (TipoMotor tm : TipoMotor.values()) {
            // Intentamos comparar ignorando tildes comparando con el NOMBRE en mayúsculas
            if (tm.name().equalsIgnoreCase(texto)) {
                return tm;
            }
            // Intentamos comparar con el texto bonito
            if (tm.texto.equalsIgnoreCase(texto)) {
                return tm;
            }
        }
        
        // Si el texto contiene la raíz de la palabra sin tildes
        String normalizado = texto.toUpperCase();
        if (normalizado.contains("ELEC")) return ELECTRICO;
        if (normalizado.contains("HIBR")) return HIBRIDO;
        if (normalizado.contains("DIES")) return DIESEL;
        
        return GASOLINA;
    }
}
