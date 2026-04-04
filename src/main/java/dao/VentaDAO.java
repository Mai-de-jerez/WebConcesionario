package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import modelo.*;
import util.JPAUtil;
import java.util.List;

public class VentaDAO {

    private static VentaDAO instance = null;
    private VentaDAO() {}
    public static VentaDAO getInstance() {
        if (instance == null) instance = new VentaDAO();
        return instance;
    }

    // ─── OBTENER POR ID ───
    public Venta obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT v FROM Venta v " +
                          "LEFT JOIN FETCH v.usuario " +
                          "LEFT JOIN FETCH v.coche " +
                          "LEFT JOIN FETCH v.reserva " + 
                          "WHERE v.id = :id";
            
            return em.createQuery(jpql, Venta.class)
                     .setParameter("id", id)
                     .getSingleResult();
        } catch (Exception e) {
            return null; 
        } finally {
            em.close();
        }
    }

    // ─── LISTAR TODOS (Admin con paginación y filtros) ───
    public List<Venta> listarAdmin(String busqueda, EstadoVenta estado, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            boolean esSoloNumero = (busqueda != null && busqueda.matches("\\d+"));
            
            // Traemos Usuario y Coche para evitar consultas extra en el bucle
            String jpql = "SELECT v FROM Venta v JOIN FETCH v.usuario JOIN FETCH v.coche WHERE 1=1";

            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    jpql += " AND v.id = :idExacto";
                } else {
                    // Buscamos por nombre de usuario y marca de coche
                    jpql += " AND (LOWER(v.usuario.nombre) LIKE :b OR LOWER(v.coche.marca) LIKE :b)";
                }
            }

            if (estado != null) jpql += " AND v.estado = :estado";
            
            jpql += " ORDER BY v.fechaPago DESC";

            TypedQuery<Venta> query = em.createQuery(jpql, Venta.class);

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

    // ─── CONTAR TODOS (Para la paginación del Admin) ───
    public long contarAdmin(String busqueda, EstadoVenta estado) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            boolean esSoloNumero = (busqueda != null && busqueda.matches("\\d+"));
            
            String jpql = "SELECT COUNT(v) FROM Venta v WHERE 1=1";
            
            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    jpql += " AND v.id = :idExacto";
                } else {
                    jpql += " AND (LOWER(v.usuario.nombre) LIKE :b OR LOWER(v.coche.marca) LIKE :b)";
                }
            }
            
            if (estado != null) jpql += " AND v.estado = :estado";

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

    // ─── LISTAR POR USUARIO (Para que el cliente vea sus compras) ───
    public List<Venta> listarPorUsuario(int idUsuario, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT v FROM Venta v JOIN FETCH v.coche WHERE v.usuario.id_usuario = :id ORDER BY v.fechaPago DESC",
                Venta.class)
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
                "SELECT COUNT(v) FROM Venta v WHERE v.usuario.id_usuario = :id", Long.class)
                .setParameter("id", idUsuario)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}
