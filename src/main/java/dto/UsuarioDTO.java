package dto;

import java.io.Serializable;
import modelo.Usuario;

public class UsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_usuario;
    private String nombre;      
    private String apellidos;
    private String nombreCompleto;
    private String email;
    private String usuario;
    private String rol;
    private String foto;
    private String telefono;
    private String direccion;

    // Constructor que "mapea" la entidad pesada al mensajero ligero
    public UsuarioDTO(Usuario u) {
        if (u != null) {
            this.id_usuario = u.getId_usuario();
            this.usuario = u.getUsuario();
            this.nombre = u.getNombre();       
            this.apellidos = u.getApellidos();
            this.nombreCompleto = u.getNombre() + " " + u.getApellidos();
            this.email = u.getEmail();
            this.rol = (u.getRol() != null) ? u.getRol().name() : null;
            this.foto = u.getFoto();
            this.telefono = u.getTelefono();
            this.direccion = u.getDireccion();
        }
    }

    // Solo Getters porque el DTO es de solo lectura (no queremos que nadie modifique su estado)
    public int getId_usuario() { return id_usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public String getUsuario() { return usuario; }
    public String getRol() { return rol; }
    public String getFoto() { return foto; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
}