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

    @Column(name = "id_usuario")
    private int idUsuario;

    @Column(name = "id_coche")
    private int idCoche;    

    @Column(name = "fecha_reserva", insertable = false, updatable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fechaReserva;           

    @Column(name = "fecha_pago")
    private Timestamp fechaPago;

    @Column(name = "fecha_limite_pago")
    private Timestamp fechaLimitePago; 

    @Column(name = "importe_abonado")
    private double importeAbonado;
    
    @Column(name = "transaccion_id")
    private String transaccionId;    

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido")
    private EstadoPedido estado;

    private String imagen; 
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Constructor vacío
    public Pedido() {
        super();
    }

    // Constructor para nuevos pedidos (sin ID ni fechas automáticas)
    public Pedido(int idUsuario, int idCoche, double importeAbonado, EstadoPedido estado, String imagen) {
        this.idUsuario = idUsuario;
        this.idCoche = idCoche;
        this.importeAbonado = importeAbonado;
        this.estado = estado;
        this.imagen = imagen;
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
	 * @return el idUsuario
	 */
	public int getIdUsuario() {
		return idUsuario;
	}

	/**
	 * @param idUsuario el idUsuario a establecer
	 */
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	/**
	 * @return el idCoche
	 */
	public int getIdCoche() {
		return idCoche;
	}

	/**
	 * @param idCoche el idCoche a establecer
	 */
	public void setIdCoche(int idCoche) {
		this.idCoche = idCoche;
	}

	/**
	 * @return el fechaReserva
	 */
	public Timestamp getFechaReserva() {
		return fechaReserva;
	}

	/**
	 * @param fechaReserva el fechaReserva a establecer
	 */
	public void setFechaReserva(Timestamp fechaReserva) {
		this.fechaReserva = fechaReserva;
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
	 * @return el fechaLimitePago
	 */
	public Timestamp getFechaLimitePago() {
		return fechaLimitePago;
	}

	/**
	 * @param fechaLimitePago el fechaLimitePago a establecer
	 */
	public void setFechaLimitePago(Timestamp fechaLimitePago) {
		this.fechaLimitePago = fechaLimitePago;
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
	 * @return el imagen
	 */
	public String getImagen() {
		return imagen;
	}

	/**
	 * @param imagen el imagen a establecer
	 */
	public void setImagen(String imagen) {
		this.imagen = imagen;
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

	@Override
	public String toString() {
		return "Pedido [id=" + id + ", idUsuario=" + idUsuario + ", idCoche=" + idCoche + ", fechaReserva="
				+ fechaReserva + ", fechaPago=" + fechaPago + ", fechaLimitePago=" + fechaLimitePago
				+ ", importeAbonado=" + importeAbonado + ", transaccionId=" + transaccionId + ", estado=" + estado
				+ ", imagen=" + imagen + ", observaciones=" + observaciones + "]";
	}		
    
}
