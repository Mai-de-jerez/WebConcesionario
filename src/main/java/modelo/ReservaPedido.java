package modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "reservas_pedido", indexes = {
    @Index(name = "idx_estado_expiracion", columnList = "estado, fecha_expiracion"),
    @Index(name = "idx_usuario", columnList = "id_usuario"),
    @Index(name = "idx_email", columnList = "email_contacto")
})
public class ReservaPedido implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // ─── CLIENTE ───
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = true) 
    private Usuario usuario;

    @Column(name = "email_contacto", nullable = false)
    private String emailContacto; 

    // ─── COCHE ───
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_coche", nullable = false)
    private Coche coche;
    
    // ─── FECHAS ───
    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago; // cuando el admin cobra en tienda

    // ─── DINERO ───
    @Column(name = "importe_senal")
    private double importeSenal = 0.0; // lo que paga al reservar

    @Column(name = "importe_total")
    private double importeTotal = 0.0; // precio final del coche

    // ─── ESTADO ───
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    // ─── EXTRA ───
    @Column(name = "transaccion_id")
    private String transaccionId;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // ─── CONSTRUCTORES ───
    public ReservaPedido() {}

    // Cliente registrado
    public ReservaPedido(Usuario usuario, Coche coche, double importeSenal) {
        this.usuario = usuario;
        this.emailContacto = usuario.getEmail();
        this.coche = coche;
        this.importeSenal = importeSenal;
        this.importeTotal = coche.getPrecio();
    }

    // Admin sin usuario registrado
    public ReservaPedido(String emailContacto, Coche coche, double importeSenal) {
        this.emailContacto = emailContacto;
        this.coche = coche;
        this.importeSenal = importeSenal;
        this.importeTotal = coche.getPrecio();
    }

    // ─── LIFECYCLE ───
    @PrePersist
    public void prePersist() {
        this.fechaReserva = LocalDateTime.now();
        this.fechaExpiracion = this.fechaReserva.plusDays(3);
    }

    // ─── MÉTODOS DE CONVENIENCIA ───
    public boolean isPendiente() {
        return estado == EstadoPedido.PENDIENTE;
    }

    public boolean isCompletada() {
        return estado == EstadoPedido.ABONADO;
    }

    public boolean isExpirada() {
        return estado == EstadoPedido.EXPIRADO;
    }

    public boolean isCancelada() {
        return estado == EstadoPedido.CANCELADO;
    }

    public double getImporteRestante() {
        return this.importeTotal - this.importeSenal;
    }

    // ─── GETTERS Y SETTERS ───
    public int getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }

    public Coche getCoche() { return coche; }
    public void setCoche(Coche coche) { this.coche = coche; }

    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public double getImporteSenal() { return importeSenal; }
    public void setImporteSenal(double importeSenal) { this.importeSenal = importeSenal; }

    public double getImporteTotal() { return importeTotal; }
    public void setImporteTotal(double importeTotal) { this.importeTotal = importeTotal; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public String getTransaccionId() { return transaccionId; }
    public void setTransaccionId(String transaccionId) { this.transaccionId = transaccionId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "ReservaPedido [id=" + id + ", email=" + emailContacto +
               ", coche=" + coche.getMarca() + " " + coche.getModelo() +
               ", estado=" + estado + ", senal=" + importeSenal + "]";
    }
}