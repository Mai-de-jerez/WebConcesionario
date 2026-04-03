package servicio;

import dao.ReservaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import modelo.*;
import util.JPAUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReservaService {

    private static ReservaService instance = null;
    private ReservaService() {}
    public static ReservaService getInstance() {
        if (instance == null) instance = new ReservaService();
        return instance;
    }

    // ─── CREAR (cliente registrado) ───
    
    public void crear(Usuario usuario, int idCoche, double importeSenal, MetodoPago metodoDeLaReserva) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            
            Coche cocheDB = em.find(Coche.class, idCoche, LockModeType.PESSIMISTIC_WRITE);
            if (cocheDB == null || !cocheDB.isDisponible())
                throw new IllegalStateException("Coche no disponible.");

            Reserva r = new Reserva(usuario, cocheDB, metodoDeLaReserva);
            r.setTransaccionId(UUID.randomUUID().toString());
            r.setObservaciones("Reserva online pendiente de pago de señal.");

            Venta v = new Venta(r, usuario, cocheDB, importeSenal, null); 
            v.setEstado(EstadoVenta.PENDIENTE);
            v.setObservaciones("Registro de pago pendiente. Método de pago por confirmar.");
            
            r.setVenta(v);
            cocheDB.setEstado(EstadoVehiculo.RESERVADO);

            em.persist(r); 
            em.getTransaction().commit(); 
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    
    // ─── COMPLETAR (admin cobra en tienda) ───
   
    public void completar(int idReserva) { 
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Buscamos la reserva con bloqueo para que nadie la toque mientras cobramos
            Reserva r = em.find(Reserva.class, idReserva, LockModeType.PESSIMISTIC_WRITE);
            
            if (r == null) throw new IllegalArgumentException("Reserva no encontrada.");
            if (!r.isActiva()) throw new IllegalStateException("Solo se pueden completar reservas activas.");           

            Venta v = r.getVenta();
            if (v == null) throw new IllegalStateException("Esta reserva no tiene una venta asociada.");

            // solo permitimos transferencia en la tienda fisica
            v.setMetodoPago(MetodoPago.TRANSFERENCIA); 
            
            String miIdSeguro = UUID.randomUUID().toString();
            
            v.setTransaccionId(miIdSeguro);
            
            // llenamos el campo con el importe final del coche, que es lo que se ha pagado en total
            v.setImporteAbonado(r.getCoche().getPrecio());            
            v.setFechaPago(LocalDateTime.now());           
            
            // venta y reserva pasan a finalizadas
            v.setEstado(EstadoVenta.FINALIZADA);
            v.setObservaciones("Venta completada en tienda física. ID autogenerado.");
            
            r.setEstado(EstadoReserva.FINALIZADA);
            
            // El coche pasa a estar vendido y sale del inventario disponible
            if (r.getCoche() != null) {
                r.getCoche().setEstado(EstadoVehiculo.VENDIDO);
            }

            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al completar la venta: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    // ─── CANCELAR (admin) ───
    
    public void cancelar(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            
            // buscamos la reserva con bloqueo para evitar que se modifique mientras la cancelamos
            Reserva r = em.find(Reserva.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (r == null) throw new IllegalArgumentException("Reserva no encontrada.");
            if (!r.isActiva()) throw new IllegalStateException("Solo se pueden cancelar reservas ACTIVAS.");

            Venta v = r.getVenta();

            r.setEstado(EstadoReserva.CANCELADA);

            // actualizamos ventas
            if (v != null) {
                v.setEstado(EstadoVenta.CANCELADA);
                v.setImporteAbonado(0); // Devolvemos el importe a 0 contablemente
            }

            // coche liberado si existía
            if (r.getCoche() != null) {
                r.getCoche().setEstado(EstadoVehiculo.DISPONIBLE);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al cancelar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    // ─── EDITAR (admin puede tocar importe y observaciones) ───
    
    public void editar(int id, String observaciones) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Reserva rp = em.find(Reserva.class, id, LockModeType.PESSIMISTIC_WRITE);
            
            if (rp == null) {
                throw new IllegalArgumentException("Pedido no encontrado.");
            }

            rp.setObservaciones(observaciones);
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar las observaciones: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    

    // ─── LISTAR (admin) ─── 
    
    public Map<String, Object> ListarAdmin(String busqueda, String estadoParam, int pagina, int porPagina) {
        EstadoReserva estado = null;
        if (estadoParam != null && !estadoParam.isBlank()) { 
            estado = EstadoReserva.desdeTexto(estadoParam);  
        }

        List<Reserva> listaReservas = ReservaDAO.getInstance().listarAdmin(busqueda, estado, pagina, porPagina);

        for (Reserva r : listaReservas) {
            if (r.getUsuario() != null) {
                r.getUsuario().setReservas(null); 
                r.getUsuario().setPassword(null);
            }
            if (r.getVenta() != null) {
                r.getVenta().setReserva(null);  
            }
    
        }

        long totalRegistros = ReservaDAO.getInstance().contarAdmin(busqueda, estado); 
        int totalPaginas = (int) Math.ceil((double) totalRegistros / porPagina);

        return Map.of(
            "reservas", listaReservas,
            "totalPaginas", totalPaginas,
            "paginaActual", pagina
        ); 
    }
    

    public long contarAdmin(String busqueda, EstadoReserva estado) {
        return ReservaDAO.getInstance().contarAdmin(busqueda, estado);
    }

    // ─── LISTAR (cliente) ───
    public List<Reserva> listarPorUsuario(int idUsuario, int pagina, int porPagina) {
        return ReservaDAO.getInstance().listarPorUsuario(idUsuario, pagina, porPagina);
    }

    public long contarPorUsuario(int idUsuario) {
        return ReservaDAO.getInstance().contarPorUsuario(idUsuario);
    }

    
    // ─── DETALLE ───
    
    public Reserva obtenerPorId(int id) {
        Reserva r = ReservaDAO.getInstance().obtenerPorId(id);

        if (r != null) {
            if (r.getUsuario() != null) {
                Usuario u = r.getUsuario();
                u.setPassword(null);
                u.setConsultas(null);
                u.setReservas(null); 
            }

            if (r.getVenta() != null) {
                r.getVenta().setReserva(null); 
            }
        }
        return r;
    }
}
