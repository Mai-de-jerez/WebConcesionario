package modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "pedidos")
public class Pedido implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private int id;
 

    @ManyToOne
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @Column(name = "fecha_pago")
    private Timestamp fechaPago;


    @Column(name = "importe_abonado")
    private double importeAbonado;
    
    @Column(name = "transaccion_id")
    private String transaccionId;    

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido")
    private EstadoPedido estado; 
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Constructor vacío
    public Pedido() {
        super();
    }

    // Constructor para nuevos pedidos (sin ID ni fechas automáticas)
    public Pedido(Reserva reserva, double importeAbonado, EstadoPedido estado) {
        this.reserva = reserva;
        this.importeAbonado = importeAbonado;
        this.estado = estado;

    }
    
    // Inicializar fechaPago automáticamente antes de persistir
    @PrePersist
    public void prePersist() {
        if (this.fechaPago == null) {
            this.fechaPago = new Timestamp(System.currentTimeMillis());
        }
    }

    // Getters y Setters
	/**
	 * @return el id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id el id a establecer
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return el idReserva
	 */
	public Reserva getReserva() {
        return reserva;
    }

	/** 
	 * @param reserva
	 */
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
 

	/**
	 * @return el fechaPago
	 */
	public Timestamp getFechaPago() {
		return fechaPago;
	}

	/**
	 * @param fechaPago el fechaPago a establecer
	 */
	public void setFechaPago(Timestamp fechaPago) {
		this.fechaPago = fechaPago;
	}



	/**
	 * @return el importeAbonado
	 */
	public double getImporteAbonado() {
		return importeAbonado;
	}

	/**
	 * @param importeAbonado el importeAbonado a establecer
	 */
	public void setImporteAbonado(double importeAbonado) {
		this.importeAbonado = importeAbonado;
	}

	/**
	 * @return el transaccionId
	 */
	public String getTransaccionId() {
		return transaccionId;
	}

	/**
	 * @param transaccionId el transaccionId a establecer
	 */
	public void setTransaccionId(String transaccionId) {
		this.transaccionId = transaccionId;
	}

	/**
	 * @return el estado
	 */
	public EstadoPedido getEstado() {
		return estado;
	}

	/**
	 * @param estado el estado a establecer
	 */
	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}


	/**
	 * @return el observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * @param observaciones el observaciones a establecer
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	/**
	 * @return el serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * Calcula el importe restante a pagar para completar el pedido, basado en el precio total del coche reservado y el importe ya abonado.
	 * @return el importe restante a pagar
	 */
	public double getImporteRestante() {
	    if (this.reserva != null && this.reserva.getCoche() != null) {
	        double precioTotal = this.reserva.getCoche().getPrecio();
	        return precioTotal - this.importeAbonado;
	    }
	    return 0.0;
	}

	@Override
	public String toString() {
		return "Pedido [id=" + id + ", reserva=" + reserva + ", fechaPago=" + fechaPago + ", importeAbonado=" + importeAbonado + ", transaccionId=" + transaccionId + ", estado=" + estado
				+ ", observaciones=" + observaciones + "]";
	}		
    
}
