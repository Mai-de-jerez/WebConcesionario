package modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "reservas", indexes = {
    @Index(name = "idx_estado_expiracion", columnList = "estado, fecha_expiracion"),
    @Index(name = "idx_usuario", columnList = "id_usuario")
})
public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // ─── CLIENTE ───
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false) 
    private Usuario usuario;

    // ─── COCHE ───
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_coche", nullable = false)
    private Coche coche;
      
    // ─── FECHAS ───
    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;
 
    // ─── PAGO ───
    @Enumerated(EnumType.STRING) 
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago;
    
    @Column(name = "pago_reserva")
    private double pagoReserva;

    // ─── ESTADO ───
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoReserva estado = EstadoReserva.ACTIVA;

    // ─── EXTRA ───
    @Column(name = "transaccion_id")
    private String transaccionId;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    // ─── RELACIÓN CON VENTA  ───
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Venta venta;

    // ─── CONSTRUCTORES ───
    public Reserva() {}

    // Cliente registrado
    public Reserva(Usuario usuario, Coche coche, MetodoPago metodoPago, double pagoReserva) {
        this.usuario = usuario;
        this.coche = coche;
        this.metodoPago = metodoPago;
        this.pagoReserva = pagoReserva;
    }


    // ─── GENERAMOS FECHAS DE RESERVA Y EXPIRACIÓN AUTOMÁTICAMENTE ───
    @PrePersist
    public void prePersist() {
        this.fechaReserva = LocalDateTime.now();
        this.fechaExpiracion = this.fechaReserva.plusDays(3);
    }

    // ─── MÉTODOS DE CONVENIENCIA ───
    public boolean isActiva() {
        return estado == EstadoReserva.ACTIVA;
    }

    public boolean isFinalizada() {
        return estado == EstadoReserva.FINALIZADA;
    }

    public boolean isExpirada() {
        return estado == EstadoReserva.EXPIRADA;
    }

    public boolean isCancelada() {
        return estado == EstadoReserva.CANCELADA;
    }

    // ─── GETTERS Y SETTERS ───
    public int getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }


    public Coche getCoche() { return coche; }
    public void setCoche(Coche coche) { this.coche = coche; }

    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }

    
	public MetodoPago getMetodoPago() { return metodoPago; }
	public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    
	public EstadoReserva getEstado() { return estado; }
	public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public String getTransaccionId() { return transaccionId; }
    public void setTransaccionId(String transaccionId) { this.transaccionId = transaccionId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public double getPagoReserva() { return pagoReserva; }
    public void setPagoReserva(double pagoReserva) { this.pagoReserva = pagoReserva; }

    // NUEVOS MÉTODOS
    public Venta getVenta() { return venta; }
    
    public void setVenta(Venta venta) { 
        this.venta = venta; 
        if (venta != null && venta.getReserva() != this) {
            venta.setReserva(this); 
        }
    }
    
    // para calcular el importe pendiente de pago (me interes para la mostrarlo en la ficha)
    
    public double getImportePendiente() {
        if (this.coche == null) return 0;
        
        // podemos sacar así el precio del coche 
        double precioTotal = this.coche.getPrecio(); 
        
        return precioTotal - this.pagoReserva;
    }
    
    
    @Override
    public String toString() {
		return "Reserva{" +
				"id=" + id +
				", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
				", coche=" + (coche != null ? coche.getMarca() + " " + coche.getModelo() : "null") +
				", fechaReserva=" + fechaReserva +
				", fechaExpiracion=" + fechaExpiracion +
				", metodoPago=" + metodoPago +
				", pagoReserva=" + pagoReserva +
				", tieneVenta=" + (venta != null) +
				", estado=" + estado +
				", transaccionId='" + transaccionId + '\'' +
				'}';
	}
}