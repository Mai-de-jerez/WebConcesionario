package modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "consultas")
public class Consultas implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String email;

    @Column(columnDefinition = "TEXT") 
    private String mensaje;

    
    @Column(name = "fecha")
    private LocalDateTime fecha;

    // Constructor vacío (Obligatorio para JPA)
    public Consultas() {
        super();
    }
    

    // Constructor para insertar
    public Consultas(String nombre, String email, String mensaje) {
        this.nombre = nombre;
        this.email = email;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return "Consultas [id=" + id + ", nombre=" + nombre + ", email=" + email + ", mensaje=" + mensaje + ", fecha="
                + fecha + "]";
    }
}