package dto;

import modelo.Venta;
import java.time.format.DateTimeFormatter;

public class VentaDTO {
    private int id;
    private String fechaPago;
    private double importeAbonado;
    private String metodoPago;
    private String estado;
    private String transaccionId;
    private String observaciones;    
    // Relaciones simplificadas
    private UsuarioDTO usuario;
    private CocheDTO coche;
    private Integer idReserva; 

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public VentaDTO(Venta v) {
        this.id = v.getId();
        this.fechaPago = v.getFechaPago() != null ? v.getFechaPago().format(fmt) : "-";
        this.importeAbonado = v.getImporteAbonado();
        this.metodoPago = v.getMetodoPago() != null ? v.getMetodoPago().name() : "N/A";
        this.estado = v.getEstado() != null ? v.getEstado().name() : "PENDIENTE";
        this.transaccionId = v.getTransaccionId();
        this.observaciones = v.getObservaciones();
        
        if (v.getUsuario() != null) this.usuario = new UsuarioDTO(v.getUsuario());
        if (v.getCoche() != null) this.coche = new CocheDTO(v.getCoche());
        if (v.getReserva() != null) this.idReserva = v.getReserva().getId();
    }

    // Getters necesarios para GSON
    public int getId() { return id; }
    public String getFechaPago() { return fechaPago; }
    public String getMetodoPago() { return metodoPago; }
    public String getTransaccionId() { return transaccionId; }
    public double getImporteAbonado() { return importeAbonado; }
    public UsuarioDTO getUsuario() { return usuario; }
    public CocheDTO getCoche() { return coche; }
    public String getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public Integer getIdReserva() { return idReserva; }
}