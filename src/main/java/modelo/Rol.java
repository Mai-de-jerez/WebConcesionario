package modelo;

public enum Rol {
    // Definimos los tres estados posibles
    // Entre paréntesis ponemos el "nivel" numérico que quiere tu profe
    SUPERUSER(1), 
    EMPLEADO(2), 
    CLIENTE(3);

    private final int nivel;

    // El constructor del Enum asocia el nombre con el número
    private Rol(int nivel) {
        this.nivel = nivel;
    }

    // Método que usaremos en los "if" para comparar niveles
    public int getNivel() {
        return nivel;
    }
}
