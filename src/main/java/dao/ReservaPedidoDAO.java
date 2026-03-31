package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import modelo.*;
import util.JPAUtil;
import java.util.List;

public class ReservaPedidoDAO {

    private static ReservaPedidoDAO instance = null;
    private ReservaPedidoDAO() {}
    public static ReservaPedidoDAO getInstance() {
        if (instance == null) instance = new ReservaPedidoDAO();
        return instance;
    }
  

    // ─── OBTENER POR ID ───
    public ReservaPedido obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(ReservaPedido.class, id);
        } finally {
            em.close();
        }
    }


    // ─── LISTAR TODOS (admin) con búsqueda y paginación ───
    public List<ReservaPedido> listarAdmin(String busqueda, EstadoPedido estado, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT rp FROM ReservaPedido rp JOIN FETCH rp.coche WHERE 1=1";
            if (busqueda != null && !busqueda.isBlank())
                jpql += " AND (LOWER(rp.emailContacto) LIKE :b OR LOWER(rp.coche.marca) LIKE :b OR LOWER(rp.coche.modelo) LIKE :b)";
            if (estado != null)
                jpql += " AND rp.estado = :estado";
            jpql += " ORDER BY rp.fechaReserva DESC";

            TypedQuery<ReservaPedido> query = em.createQuery(jpql, ReservaPedido.class);
            if (busqueda != null && !busqueda.isBlank())
                query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
            if (estado != null)
                query.setParameter("estado", estado);

            query.setFirstResult((pagina - 1) * porPagina);
            query.setMaxResults(porPagina);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
  

    // ─── CONTAR TODOS (admin) ───
    public long contarAdmin(String busqueda, EstadoPedido estado) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(rp) FROM ReservaPedido rp WHERE 1=1";
            if (busqueda != null && !busqueda.isBlank())
                jpql += " AND (LOWER(rp.emailContacto) LIKE :b OR LOWER(rp.coche.marca) LIKE :b OR LOWER(rp.coche.modelo) LIKE :b)";
            if (estado != null)
                jpql += " AND rp.estado = :estado";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (busqueda != null && !busqueda.isBlank())
                query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
            if (estado != null)
                query.setParameter("estado", estado);

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    // ─── LISTAR POR USUARIO (cliente) ───
    public List<ReservaPedido> listarPorUsuario(int idUsuario, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT rp FROM ReservaPedido rp WHERE rp.usuario.id_usuario = :id ORDER BY rp.fechaReserva DESC",
                ReservaPedido.class)
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
                "SELECT COUNT(rp) FROM ReservaPedido rp WHERE rp.usuario.id_usuario = :id", Long.class)
                .setParameter("id", idUsuario)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}