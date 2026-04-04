package dto;

import modelo.Reserva;
import java.time.format.DateTimeFormatter;

public class ReservaDTO {
	
    private int id;
    private CocheDTO coche; 
    private UsuarioDTO cliente;    
    private String fechaReserva;
    private String fechaExpiracion;
    private String estado;
    private String metodoPago;
    private Double importeAbonado;
    private String transaccionId;
    private String observaciones;

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReservaDTO(Reserva r) {
        this.id = r.getId();
    
        if (r.getCoche() != null) {
            this.coche = new CocheDTO(r.getCoche());
        }
    
        if (r.getUsuario() != null) {
            this.cliente = new UsuarioDTO(r.getUsuario());
        }

        this.fechaReserva = r.getFechaReserva() != null ? r.getFechaReserva().format(fmt) : "-";
        this.fechaExpiracion = r.getFechaExpiracion() != null ? r.getFechaExpiracion().format(fmt) : "-";
        this.estado = r.getEstado().name();
        this.metodoPago = r.getMetodoPago() != null ? r.getMetodoPago().name() : "No definido";
        
        this.importeAbonado = 0.0; 

        if (r.getVenta() != null) {
            this.importeAbonado = r.getVenta().getImporteAbonado();
        }
  
        this.transaccionId = r.getTransaccionId();
        this.observaciones = r.getObservaciones();
    }

    // Getters necesarios para GSON
    public int getId() { return id; }
    public CocheDTO getCoche() { return coche; }
    public UsuarioDTO getCliente() { return cliente; }
    public String getFechaReserva() { return fechaReserva; }
    public String getFechaExpiracion() { return fechaExpiracion; }
    public String getEstado() { return estado; }
    public String getMetodoPago() { return metodoPago; }
    public double getImporteAbonado() { return importeAbonado; }
    public String getTransaccionId() { return transaccionId; }
    public String getObservaciones() { return observaciones; }
    
}