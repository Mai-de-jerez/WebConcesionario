package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import modelo.*;
import util.JPAUtil;
import java.util.List;

public class ReservaDAO {

    private static ReservaDAO instance = null;
    private ReservaDAO() {}
    public static ReservaDAO getInstance() {
        if (instance == null) instance = new ReservaDAO();
        return instance;
    }

  
    
    // ─── CREAR RESERVA ───
    public void crear(Reserva r) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Coche coche = em.find(Coche.class, r.getCoche().getId());
            coche.setEstado(EstadoVehiculo.RESERVADO);

            em.persist(r);
            em.flush(); 

            Pedido pedido = new Pedido(r, 0.0, EstadoPedido.PENDIENTE);
            em.persist(pedido);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ─── LISTAR TODAS (admin) con búsqueda por usuario, coche, estado y paginación ───
    public List<Reserva> listarAdmin(String busqueda, EstadoReserva estado, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT r FROM Reserva r WHERE 1=1";

            if (busqueda != null && !busqueda.isBlank()) {
                jpql += " AND (LOWER(r.usuario.usuario) LIKE :b" +
                        " OR LOWER(r.coche.marca) LIKE :b" +
                        " OR LOWER(r.coche.modelo) LIKE :b)";
            }

            if (estado != null) {
                jpql += " AND r.estado = :estado";
            }

            jpql += " ORDER BY r.fechaReserva DESC";

            TypedQuery<Reserva> query = em.createQuery(jpql, Reserva.class);

            if (busqueda != null && !busqueda.isBlank()) {
                query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
            }
            if (estado != null) {
                query.setParameter("estado", estado);
            }

            query.setFirstResult((pagina - 1) * porPagina);
            query.setMaxResults(porPagina);
            return query.getResultList();

        } finally {
            em.close();
        }
    }

    // ─── CONTAR TODAS (admin) ───
    public long contarAdmin(String busqueda, EstadoReserva estado) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(r) FROM Reserva r WHERE 1=1";

            if (busqueda != null && !busqueda.isBlank()) {
                jpql += " AND (LOWER(r.usuario.usuario) LIKE :b" +
                        " OR LOWER(r.coche.marca) LIKE :b" +
                        " OR LOWER(r.coche.modelo) LIKE :b)";
            }
            if (estado != null) {
                jpql += " AND r.estado = :estado";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);

            if (busqueda != null && !busqueda.isBlank()) {
                query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
            }
            if (estado != null) {
                query.setParameter("estado", estado);
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    // ─── MIS RESERVAS (cliente) sin buscador, paginadas ───
    public List<Reserva> listarPorUsuario(int idUsuario, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Reserva> query = em.createQuery(
                "SELECT r FROM Reserva r WHERE r.usuario.id_usuario = :id ORDER BY r.fechaReserva DESC",
                Reserva.class);
            query.setParameter("id", idUsuario);
            query.setFirstResult((pagina - 1) * porPagina);
            query.setMaxResults(porPagina);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // ─── CONTAR MIS RESERVAS (cliente) ───
    public long contarPorUsuario(int idUsuario) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(r) FROM Reserva r WHERE r.usuario.id_usuario = :id",
                Long.class)
                .setParameter("id", idUsuario)
                .getSingleResult();
        } finally {
            em.close();
        }
    }

    // ─── DETALLE  ───
    public Reserva obtenerPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Reserva.class, id);
        } finally {
            em.close();
        }
    }
    
    // CANCELAR
    public void cancelar(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Reserva r = em.find(Reserva.class, id);
            if (r != null) {
                r.setEstado(EstadoReserva.CANCELADA);
                r.getCoche().setEstado(EstadoVehiculo.DISPONIBLE); 
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}