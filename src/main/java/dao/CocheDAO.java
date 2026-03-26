package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import modelo.Coche;
import modelo.EstadoVehiculo;
import util.JPAUtil;
import java.util.List;

public class CocheDAO {
    
    private static CocheDAO instance = null;

    private CocheDAO() {}

    public static CocheDAO getInstance() {
        if (instance == null) {
            instance = new CocheDAO();
        }
        return instance;
    }

    public List<Coche> listar() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Coche c", Coche.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void insertar(Coche c) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(c); 
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void actualizar(Coche c) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(c); 
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void eliminar(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Coche c = em.find(Coche.class, id);
            if (c != null) {
                em.remove(c);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Coche obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Coche.class, id);
        } finally {
            em.close();
        }
    }

    public void actualizarEstadoCoche(int id, EstadoVehiculo nuevoEstado) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Coche c = em.find(Coche.class, id);
            if (c != null) {
                c.setEstado(nuevoEstado); 
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Coche> obtenerTresUltimos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT c FROM Coche c ORDER BY c.id DESC";
            TypedQuery<Coche> query = em.createQuery(jpql, Coche.class);
            query.setMaxResults(3); 
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}


