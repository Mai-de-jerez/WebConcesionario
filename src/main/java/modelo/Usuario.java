package modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int id_usuario;

    @Column(unique = true, nullable = false)
    private String usuario;
    private String nombre;
    private String apellidos;
    private String password;
    

    @Enumerated(EnumType.STRING) 
    private Rol rol; 

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;
    private String direccion;
    private String foto;
    
    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "token_expiracion")
    private LocalDateTime tokenExpiracion;
    
    // Relación con ReservaPedido
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private transient List<Reserva> reservas;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private transient List<Consultas> consultas;

    // Constructor vacío (Obligatorio para JPA)
    public Usuario() {}

    // Constructor para Registro (el que ya tenías)
    public Usuario(String usuario, String email, String password, String telefono, String direccion) {
        this.usuario = usuario;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.direccion = direccion;
        this.rol = Rol.CLIENTE; 
        this.foto = "sin-foto.png"; 
    }

   
    
    // Constructor para el login
    
    public Usuario(String usuario, String password) {
		this.usuario = usuario;
		this.password = password;
	}



	public Usuario(int id_usuario, String usuario, String nombre, String apellidos, String password, Rol rol,
			String email, String telefono, String direccion, String foto) {
		super();
		this.id_usuario = id_usuario;
		this.usuario = usuario;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.password = password;
		this.rol = rol;
		this.email = email;
		this.telefono = telefono;
		this.direccion = direccion;
		this.foto = foto;
	}

	// Getters y Setters
    
    /**
	 * @return el id_usuario
	 */
	public int getId_usuario() {
		return id_usuario;
	}

	/**
	 * @param id_usuario el id_usuario a establecer
	 */
	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}

	/**
	 * @return el usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario el usuario a establecer
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return el nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre el nombre a establecer
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return el apellidos
	 */
	public String getApellidos() {
		return apellidos;
	}

	/**
	 * @param apellidos el apellidos a establecer
	 */
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	/**
	 * @return el password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password el password a establecer
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return el rol
	 */
	public Rol getRol() {
		return rol;
	}

	/**
	 * @param rol el rol a establecer
	 */
	public void setRol(Rol rol) {
		this.rol = rol;
	}

	/**
	 * @return el email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email el email a establecer
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return el telefono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * @param telefono el telefono a establecer
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * @return el direccion
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * @param direccion el direccion a establecer
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * @return el foto
	 */
	public String getFoto() {
		return foto;
	}

	/**
	 * @param foto el foto a establecer
	 */
	public void setFoto(String foto) {
		this.foto = foto;
	}


    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
    
	/**
	 * @return el serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public List<Consultas> getConsultas() {
	    return consultas;
	}

	public void setConsultas(List<Consultas> consultas) {
	    this.consultas = consultas;
	}
	


	@Override
	public String toString() {
		return "Usuario [id_usuario=" + id_usuario + ", usuario=" + usuario + ", nombre=" + nombre + ", apellidos="
				+ apellidos + ", rol=" + rol + ", email=" + email + ", telefono=" + telefono
				+ ", direccion=" + direccion + ", foto=" + foto + "]";
	}
   
}






