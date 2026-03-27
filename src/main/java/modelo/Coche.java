package modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;// Importante: usamos jakarta
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "coche") 
public class Coche implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;

    private String marca;
    private String modelo;
    
    @Column(unique = true)
    private String matricula;
    
    private String color;
    private Double precio;
    private int km;
    private String anio;

    @Column(name = "tipo_motor")
    @Enumerated(EnumType.STRING) 
    private TipoMotor tipoMotor;

    private int numPuertas;

    @Enumerated(EnumType.STRING) 
    private EstadoVehiculo estado;

    private String imagen;

    @Column(columnDefinition = "TEXT") 
    private String descripcion;


    // Relación con reservas
    @OneToMany(mappedBy = "coche", cascade = CascadeType.ALL, orphanRemoval = true)
    private transient List<Reserva> reservas;
    
    // CONSTRUCTOR VACÍO: Obligatorio para JPA
    public Coche() {
        super();
    }

    // Tu constructor con parámetros se queda igual...
    public Coche(int id, String marca, String modelo, String matricula, String color, Double precio, int km,
            String anio, TipoMotor tipoMotor, int numPuertas, EstadoVehiculo estado, String imagen, String descripcion) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.matricula = matricula;
        this.color = color;
        this.precio = precio;
        this.km = km;
        this.anio = anio;
        this.tipoMotor = tipoMotor;
        this.numPuertas = numPuertas;
        this.estado = estado;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }
    
 // Tu constructor con parámetros se queda igual...
    public Coche(String marca, String modelo, String matricula, String color, Double precio, int km,
            String anio, TipoMotor tipoMotor, int numPuertas, EstadoVehiculo estado, String imagen, String descripcion) {
   
        this.marca = marca;
        this.modelo = modelo;
        this.matricula = matricula;
        this.color = color;
        this.precio = precio;
        this.km = km;
        this.anio = anio;
        this.tipoMotor = tipoMotor;
        this.numPuertas = numPuertas;
        this.estado = estado;
        this.imagen = imagen;
        this.descripcion = descripcion;
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
	 * @return el marca
	 */
	public String getMarca() {
		return marca;
	}

	/**
	 * @param marca el marca a establecer
	 */
	public void setMarca(String marca) {
		this.marca = marca;
	}

	/**
	 * @return el modelo
	 */
	public String getModelo() {
		return modelo;
	}

	/**
	 * @param modelo el modelo a establecer
	 */
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	/**
	 * @return el matricula
	 */
	public String getMatricula() {
		return matricula;
	}

	/**
	 * @param matricula el matricula a establecer
	 */
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	/**
	 * @return el color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color el color a establecer
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return el precio
	 */
	public Double getPrecio() {
		return precio;
	}

	/**
	 * @param precio el precio a establecer
	 */
	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	/**
	 * @return el km
	 */
	public int getKm() {
		return km;
	}

	/**
	 * @param km el km a establecer
	 */
	public void setKm(int km) {
		this.km = km;
	}

	/**
	 * @return el anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * @param anio el anio a establecer
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	/**
	 * @return el tipoMotor
	 */
	public TipoMotor getTipoMotor() {
		return tipoMotor;
	}

	/**
	 * @param tipoMotor el tipoMotor a establecer
	 */
	public void setTipoMotor(TipoMotor tipoMotor) {
		this.tipoMotor = tipoMotor;
	}

	/**
	 * @return el numPuertas
	 */
	public int getNumPuertas() {
		return numPuertas;
	}

	/**
	 * @param numPuertas el numPuertas a establecer
	 */
	public void setNumPuertas(int numPuertas) {
		this.numPuertas = numPuertas;
	}

	/**
	 * @return el estado
	 */
	public EstadoVehiculo getEstado() {
		return estado;
	}

	/**
	 * @param estado el estado a establecer
	 */
	public void setEstado(EstadoVehiculo estado) {
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
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion el descripcion a establecer
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; } 
	
	// Métodos personales
	
	public String getPrecioFormateado() {
        return String.format("%.2f €", this.precio);
    }
	
	public boolean isDisponible() {
        return this.estado == EstadoVehiculo.DISPONIBLE;
    }

    public boolean isReservado() {
        return this.estado == EstadoVehiculo.RESERVADO;
    }

    public boolean isVendido() {
        return this.estado == EstadoVehiculo.VENDIDO;
    }
    
      

	@Override
	public String toString() {
		return "Coche [id=" + id + ", marca=" + marca + ", modelo=" + modelo + ", matricula=" + matricula + ", color="
				+ color + ", precio=" + precio + ", km=" + km + ", anio=" + anio + ", tipoMotor=" + tipoMotor
				+ ", numPuertas=" + numPuertas + ", estado=" + estado + ", imagen=" + imagen + ", descripcion="
				+ descripcion + "]";
	}
    	
}

 




