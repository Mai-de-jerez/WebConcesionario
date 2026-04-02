package servicio;

import dao.ReservaPedidoDAO;
import jakarta.persistence.EntityManager;
import modelo.*;
import util.JPAUtil;

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
    public void crear(Usuario usuario, int idCoche, double importeSenal) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Coche cocheDB = em.find(Coche.class, idCoche);
            
            if (cocheDB == null || !cocheDB.isDisponible())
                throw new IllegalStateException("El coche no está disponible.");

            ReservaPedido rp = new ReservaPedido(usuario, cocheDB, importeSenal);
            rp.setTransaccionId(UUID.randomUUID().toString());
         
            cocheDB.setEstado(EstadoVehiculo.RESERVADO);

            em.persist(rp); 
            
            em.getTransaction().commit(); 
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    


    // ─── CREAR (admin sin usuario registrado) ───
    
    public void crearVentaDirecta(String emailContacto, int idCoche, double importeTotal, LocalDateTime fechaPago) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            
            Coche cocheDB = em.find(Coche.class, idCoche);
            if (cocheDB == null || !cocheDB.isDisponible())
                throw new IllegalStateException("El coche no está disponible.");

            ReservaPedido rp = new ReservaPedido(emailContacto, cocheDB, 0);
            rp.setEstado(EstadoPedido.ABONADO);
            rp.setImporteTotal(importeTotal);
            rp.setImporteFinalAbonado(importeTotal);
            rp.setFechaPago(fechaPago);
            rp.setTransaccionId(UUID.randomUUID().toString());
            cocheDB.setEstado(EstadoVehiculo.VENDIDO);

            em.persist(rp);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    
    // ─── COMPLETAR (admin cobra en tienda) ───
    public void completar(int id) { 
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            ReservaPedido rp = em.find(ReservaPedido.class, id);
            
            if (rp == null) throw new IllegalArgumentException("Pedido no encontrado.");
            if (!rp.isPendiente()) throw new IllegalStateException("Solo se pueden completar pedidos PENDIENTES.");           
            // Actualizamos 
            rp.setEstado(EstadoPedido.ABONADO);
            rp.setImporteFinalAbonado(rp.getImporteTotal());
            rp.setFechaPago(LocalDateTime.now());
            rp.getCoche().setEstado(EstadoVehiculo.VENDIDO);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    // ─── CANCELAR (admin) ───
    
    public void cancelar(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            ReservaPedido rp = em.find(ReservaPedido.class, id);
            if (rp == null) throw new IllegalArgumentException("Pedido no encontrado.");
            if (!rp.isPendiente()) throw new IllegalStateException("Solo se pueden cancelar pedidos PENDIENTES.");
            rp.setEstado(EstadoPedido.CANCELADO);
            rp.setImporteFinalAbonado(0);
            rp.getCoche().setEstado(EstadoVehiculo.DISPONIBLE);
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    // ─── EDITAR (admin puede tocar importe y observaciones) ───
    
    public void editar(int id, String observaciones) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            ReservaPedido rp = em.find(ReservaPedido.class, id);
            
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
        // pedimos todo a la base de datos y luego limpiamos datos sensibles para evitar fugas de información
        ReservaPedido rp = ReservaPedidoDAO.getInstance().obtenerPorId(id);

     
        if (rp != null && rp.getUsuario() != null) {
            rp.getUsuario().setPassword(null);     
            rp.getUsuario().setConsultas(null);      
            rp.getUsuario().setReservasPedido(null); 
        }

        return rp;
    }
}
