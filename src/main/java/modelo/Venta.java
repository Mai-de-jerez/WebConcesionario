package modelo;


import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "ventas")
public class Venta implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @OneToOne 
    @JoinColumn(name = "id_reserva", nullable = true) 
    private Reserva reserva;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario; 
    
    @ManyToOne
    @JoinColumn(name = "id_coche", nullable = false)
    private Coche coche;

    @Column(name = "importe_abonado", nullable = false)
    private double importeAbonado;
      
    // ─── FECHAS ───
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
     
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = true)
    private MetodoPago metodoPago;

    // ─── ESTADO ───
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_venta")
    private EstadoVenta estado = EstadoVenta.PENDIENTE;

    // ─── EXTRA ───
    @Column(name = "transaccion_id")
    private String transaccionId;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
    

    // ─── CONSTRUCTORES ───
    public Venta() {}

    // Cliente registrado
    public Venta(Reserva reserva, Usuario usuario, Coche coche, double importeAbonado, MetodoPago metodoPago) {
		this.reserva = reserva;
		this.usuario = usuario;
		this.coche = coche;
		this.importeAbonado = importeAbonado;
		this.metodoPago = metodoPago;
	}
    
    // Cliente DE TIENDA (venta directa sin reserva previa)
    public Venta(Usuario usuario, Coche coche, double importeAbonado, MetodoPago metodoPago) {
		this.usuario = usuario;
		this.coche = coche;
		this.importeAbonado = importeAbonado;
		this.metodoPago = metodoPago;
    					
    }

    // ─── LIFECYCLE ───
    @PrePersist
    public void prePersist() {
        this.fechaPago = LocalDateTime.now();
    }

    // ─── MÉTODOS DE CONVENIENCIA ───
    public boolean isPendiente() {
        return estado == EstadoVenta.PENDIENTE;
    }

    public boolean isFinalizada() {
        return estado == EstadoVenta.FINALIZADA; 
    }

    public boolean isPenalizada() {
        return estado == EstadoVenta.PENALIZADA;
    }

    public boolean isCancelada() {
        return estado == EstadoVenta.CANCELADA;  
    }

    // ─── GETTERS Y SETTERS ───
    public int getId() { return id; }
    
    public Reserva getReserva() { return reserva; }
	public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Coche getCoche() { return coche; }
    public void setCoche(Coche coche) { this.coche = coche; }
    
	public LocalDateTime getFechaPago() { return fechaPago; }
	public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public double getImporteAbonado() { return importeAbonado; }
    public void setImporteAbonado(double importeAbonado) { this.importeAbonado = importeAbonado; }
    
	public MetodoPago getMetodoPago() { return metodoPago; }
	public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    
	public EstadoVenta getEstado() { return estado; }
	public void setEstado(EstadoVenta estado) { this.estado = estado; }

    public String getTransaccionId() { return transaccionId; }
    public void setTransaccionId(String transaccionId) { this.transaccionId = transaccionId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
		return "Venta{" +
				"id=" + id +
				", reserva=" + (reserva != null ? reserva.getId() : "null") +
				", usuario=" + (usuario != null ? usuario.getId_usuario() : "null") + 
				", coche=" + (coche != null ? coche.getId() : "null") +
				", importeAbonado=" + importeAbonado +
				", fechaPago=" + fechaPago +
				", metodoPago=" + metodoPago + 
				", estado=" + estado +
				", transaccionId='" + transaccionId + '\'' +
				", observaciones='" + observaciones + '\'' +  
	            '}';
    }
}