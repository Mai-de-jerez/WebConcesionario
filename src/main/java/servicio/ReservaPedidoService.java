package servicio;

import dao.ReservaPedidoDAO;
import dao.CocheDAO;
import modelo.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReservaPedidoService {

    private static ReservaPedidoService instance = null;
    private ReservaPedidoService() {}
    public static ReservaPedidoService getInstance() {
        if (instance == null) instance = new ReservaPedidoService();
        return instance;
    }

    // ─── CREAR (cliente registrado) ───
    public void crear(Usuario usuario, Coche coche, double importeSenal) {
        if (!coche.isDisponible())
            throw new IllegalStateException("El coche no está disponible para reservar.");
        if (importeSenal > coche.getPrecio())
            throw new IllegalArgumentException("El importe de la señal no puede superar el precio del coche.");

        coche.setEstado(EstadoVehiculo.RESERVADO);
        CocheDAO.getInstance().actualizar(coche);

        ReservaPedido rp = new ReservaPedido(usuario, coche, importeSenal);
        rp.setTransaccionId(UUID.randomUUID().toString());
        ReservaPedidoDAO.getInstance().guardar(rp);
    }

    // ─── CREAR (admin sin usuario registrado) ───
    public void crearSinUsuario(String emailContacto, Coche coche, double importeSenal) {
        if (!coche.isDisponible())
            throw new IllegalStateException("El coche no está disponible para reservar.");
        if (importeSenal > coche.getPrecio())
            throw new IllegalArgumentException("El importe de la señal no puede superar el precio del coche.");

        coche.setEstado(EstadoVehiculo.RESERVADO);
        CocheDAO.getInstance().actualizar(coche);

        ReservaPedido rp = new ReservaPedido(emailContacto, coche, importeSenal);
        rp.setTransaccionId(UUID.randomUUID().toString());
        ReservaPedidoDAO.getInstance().guardar(rp);
    }

    // ─── COMPLETAR (admin cobra en tienda) ───
    public void completar(int id, double importeTotal, String observaciones) {
        ReservaPedido rp = ReservaPedidoDAO.getInstance().obtenerPorId(id);
        if (rp == null)
            throw new IllegalArgumentException("Pedido no encontrado.");
        if (!rp.isPendiente())
            throw new IllegalStateException("Solo se pueden completar pedidos PENDIENTES.");

        rp.setEstado(EstadoPedido.ABONADO);
        rp.setImporteTotal(importeTotal);
        rp.setFechaPago(LocalDateTime.now());
        rp.setObservaciones(observaciones);
        rp.getCoche().setEstado(EstadoVehiculo.VENDIDO);

        ReservaPedidoDAO.getInstance().actualizar(rp);
    }

    // ─── CANCELAR (admin) ───
    public void cancelar(int id, String observaciones) {
        ReservaPedido rp = ReservaPedidoDAO.getInstance().obtenerPorId(id);
        if (rp == null)
            throw new IllegalArgumentException("Pedido no encontrado.");
        if (!rp.isPendiente())
            throw new IllegalStateException("Solo se pueden cancelar pedidos PENDIENTES.");

        rp.setEstado(EstadoPedido.CANCELADO);
        rp.setObservaciones(observaciones);
        rp.getCoche().setEstado(EstadoVehiculo.DISPONIBLE);

        ReservaPedidoDAO.getInstance().actualizar(rp);
    }

    // ─── EDITAR (admin puede tocar importe y observaciones) ───
    public void editar(int id, double importeSenal, String observaciones) {
        ReservaPedido rp = ReservaPedidoDAO.getInstance().obtenerPorId(id);
        if (rp == null)
            throw new IllegalArgumentException("Pedido no encontrado.");
        if (importeSenal > rp.getImporteTotal())
            throw new IllegalArgumentException("La señal no puede superar el importe total.");

        rp.setImporteSenal(importeSenal);
        rp.setObservaciones(observaciones);

        ReservaPedidoDAO.getInstance().actualizar(rp);
    }



    // ─── EXPIRAR VENCIDOS (llamado por el scheduler) ───
    public void expirarVencidos() {
        List<ReservaPedido> vencidos = ReservaPedidoDAO.getInstance().obtenerVencidos();
        for (ReservaPedido rp : vencidos) {
            rp.setEstado(EstadoPedido.EXPIRADO);
            rp.setImporteTotal(rp.getCoche().getPrecio());
            rp.getCoche().setEstado(EstadoVehiculo.DISPONIBLE);
            rp.setObservaciones("Reserva expirada por falta de pago.");
            ReservaPedidoDAO.getInstance().actualizar(rp);
        }
    }

    // ─── LISTAR (admin) ───
    public List<ReservaPedido> listarAdmin(String busqueda, EstadoPedido estado, int pagina, int porPagina) {
        return ReservaPedidoDAO.getInstance().listarAdmin(busqueda, estado, pagina, porPagina);
    }

    public long contarAdmin(String busqueda, EstadoPedido estado) {
        return ReservaPedidoDAO.getInstance().contarAdmin(busqueda, estado);
    }

    // ─── LISTAR (cliente) ───
    public List<ReservaPedido> listarPorUsuario(int idUsuario, int pagina, int porPagina) {
        return ReservaPedidoDAO.getInstance().listarPorUsuario(idUsuario, pagina, porPagina);
    }

    public long contarPorUsuario(int idUsuario) {
        return ReservaPedidoDAO.getInstance().contarPorUsuario(idUsuario);
    }

    // ─── DETALLE ───
    public ReservaPedido obtenerPorId(int id) {
        return ReservaPedidoDAO.getInstance().obtenerPorId(id);
    }
}
