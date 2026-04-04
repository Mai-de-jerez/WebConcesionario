package dto;

import modelo.Coche;
import java.io.Serializable;

public class CocheDTO implements Serializable {
	private static final long serialVersionUID = 1L;
    
	private int id;
    private String marca;
    private String modelo;
    private String matricula;
    private String color;
    private Double precio;
    private String precioFormateado; 
    private int km;
    private String anio;
    private String tipoMotor; 
    private int numPuertas;
    private String estado;    
    private String imagen;
    private String descripcion;

    // Constructor que recibe la entidad original y mapea sus campos al DTO
    public CocheDTO(Coche c) {
        if (c != null) {
            this.id = c.getId();
            this.marca = c.getMarca();
            this.modelo = c.getModelo();
            this.matricula = c.getMatricula();
            this.color = c.getColor();
            this.precio = c.getPrecio();
            this.precioFormateado = c.getPrecioFormateado(); 
            this.km = c.getKm();
            this.anio = c.getAnio();              
            this.tipoMotor = (c.getTipoMotor() != null) ? c.getTipoMotor().name() : null;
            this.numPuertas = c.getNumPuertas();
            this.estado = (c.getEstado() != null) ? c.getEstado().name() : null;
            this.imagen = c.getImagen();
            this.descripcion = c.getDescripcion();
        }
    }

    // GETTERS (Importantes para que GSON los vea)
    public int getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getMatricula() { return matricula; }
    public String getColor() { return color; }
    public Double getPrecio() { return precio; }
    public String getPrecioFormateado() { return precioFormateado; }
    public int getKm() { return km; }
    public String getAnio() { return anio; }
    public String getTipoMotor() { return tipoMotor; }
    public int getNumPuertas() { return numPuertas; }
    public String getEstado() { return estado; }
    public String getImagen() { return imagen; }
    public String getDescripcion() { return descripcion; }
}
