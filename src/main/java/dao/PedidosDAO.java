package dao;

import jakarta.persistence.EntityManager;
import modelo.Pedido;
import modelo.EstadoPedido;
import util.JPAUtil;
import java.util.List;

public class PedidosDAO {

    private static PedidosDAO instance = null;
    private PedidosDAO() {}

    public static PedidosDAO getInstance() {
        if (instance == null) instance = new PedidosDAO();
        return instance;
    }

    /**
     * Lista los pedidos de un usuario específico, ordenados por fecha de reserva (más recientes primero)
     * @param email El email del usuario cuyos pedidos se quieren listar
     * @return Lista de pedidos del usuario, ordenados por fecha de reserva descendente
     */
    public List<Pedido> listarPedidosPorEmail(String email) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
  
            String jpql = "SELECT p FROM Pedido p WHERE p.idUsuario IN " +
                          "(SELECT u.id_usuario FROM Usuario u WHERE u.email = :email) " +
                          "ORDER BY p.fechaReserva DESC";
            
            return em.createQuery(jpql, Pedido.class)
                     .setParameter("email", email)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Pedido> listarTodosLosPedidos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Pedido p ORDER BY p.fechaReserva DESC", Pedido.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Reemplaza a insertarPedido
     * Al terminar, el objeto 'p' ya tendrá su ID asignado automáticamente por JPA
     */
    public void insertarPedido(Pedido p) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p); 
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void actualizarEstadoPedido(int idPedido, EstadoPedido nuevoEstado, String observaciones) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Pedido p = em.find(Pedido.class, idPedido);
            if (p != null) {
                p.setEstado(nuevoEstado);
                p.setObservaciones(observaciones);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Pedido obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Pedido.class, id);
        } finally {
            em.close();
        }
    }

    public void eliminarPedido(int idPedido) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Pedido p = em.find(Pedido.class, idPedido);
            if (p != null) {
                em.remove(p);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}