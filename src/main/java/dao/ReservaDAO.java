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
  

    // ─── OBTENER POR ID ───
    
    public Reserva obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT r FROM Reserva r " +
                          "LEFT JOIN FETCH r.usuario " +
                          "LEFT JOIN FETCH r.coche " +
                          "LEFT JOIN FETCH r.venta " + 
                          "WHERE r.id = :id";
            
            return em.createQuery(jpql, Reserva.class)
                     .setParameter("id", id)
                     .getSingleResult();
        } catch (Exception e) {
            return null; 
        } finally {
            em.close();
        }
    }


    // --- LISTAR TODOS (admin) ---
    public List<Reserva> listarAdmin(String busqueda, EstadoReserva estado, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            boolean esSoloNumero = (busqueda != null && busqueda.matches("\\d+"));
            
            // Añadimos FETCH r.usuario porque ya no hay email en Reserva
            String jpql = "SELECT r FROM Reserva r JOIN FETCH r.coche JOIN FETCH r.usuario WHERE 1=1";

            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    jpql += " AND r.id = :idExacto";
                } else {
                    jpql += " AND (LOWER(r.usuario.email) LIKE :b OR LOWER(r.coche.marca) LIKE :b OR LOWER(r.coche.modelo) LIKE :b)";
                }
            }

            if (estado != null) jpql += " AND r.estado = :estado";
            jpql += " ORDER BY r.fechaReserva DESC";

            TypedQuery<Reserva> query = em.createQuery(jpql, Reserva.class);

            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    query.setParameter("idExacto", Integer.parseInt(busqueda));
                } else {
                    query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
                }
            }

            if (estado != null) query.setParameter("estado", estado);
            query.setFirstResult((pagina - 1) * porPagina);
            query.setMaxResults(porPagina);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    //  ─── CONTAR TODOS (admin) con búsqueda y filtro de estado ───
    public long contarAdmin(String busqueda, EstadoReserva estado) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            boolean esSoloNumero = (busqueda != null && busqueda.matches("\\d+"));
            
            String jpql = "SELECT COUNT(r) FROM Reserva r WHERE 1=1";
            
            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    jpql += " AND r.id = :idExacto";
                } else {
                    jpql += " AND (LOWER(r.usuario.email) LIKE :b OR LOWER(r.coche.marca) LIKE :b OR LOWER(r.coche.modelo) LIKE :b)";
                }
            }
            
            if (estado != null) jpql += " AND r.estado = :estado";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            
            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    query.setParameter("idExacto", Integer.parseInt(busqueda));
                } else {
                    query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
                }
            }
            
            if (estado != null) query.setParameter("estado", estado);

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }


    // ─── LISTAR POR USUARIO (cliente) ───
    public List<Reserva> listarPorUsuario(int idUsuario, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Reserva r WHERE r.usuario.id_usuario = :id ORDER BY r.fechaReserva DESC",
                Reserva.class)
                .setParameter("id", idUsuario)
                .setFirstResult((pagina - 1) * porPagina)
                .setMaxResults(porPagina)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // ─── CONTAR POR USUARIO ───
    public long contarPorUsuario(int idUsuario) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(r) FROM Reserva r WHERE r.usuario.id_usuario = :id", Long.class)
                .setParameter("id", idUsuario)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}