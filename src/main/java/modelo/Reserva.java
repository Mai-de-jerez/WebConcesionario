package modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservas")
public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Relación con usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación con coche
    @ManyToOne
    @JoinColumn(name = "coche_id", nullable = false)
    private Coche coche;

    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "estado_reserva")
    @Enumerated(EnumType.STRING)
    private EstadoReserva estado = EstadoReserva.ACTIVA;

    // Relación con pedidos
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();

    public Reserva() {}

    // Constructor inicial
    public Reserva(Usuario usuario, Coche coche) {
        this.usuario = usuario;
        this.coche = coche;
    }

    // Calcula fechas automáticamente al persistir
    @PrePersist
    public void prePersist() {
        this.fechaReserva = LocalDateTime.now();
        this.fechaExpiracion = this.fechaReserva.plusDays(3); // +3 días
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Coche getCoche() {
        return coche;
    }

    public void setCoche(Coche coche) {
        this.coche = coche;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    // Métodos de conveniencia
    public boolean isActiva() {
        return estado == EstadoReserva.ACTIVA;
    }

    public boolean isExpirada() {
        return estado == EstadoReserva.EXPIRADA;
    }

    public boolean isCompletada() {
        return estado == EstadoReserva.COMPLETADA;
    }
    
    public boolean isCancelada() {
		return estado == EstadoReserva.CANCELADA;
	}
    
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public String toString() {
        return "Reserva [id=" + id + ", usuario=" + usuario.getUsuario() + ", coche=" + coche.getMarca() + " " + coche.getModelo()
                + ", fechaReserva=" + fechaReserva + ", fechaExpiracion=" + fechaExpiracion + ", estado=" + estado + "]";
    }
}