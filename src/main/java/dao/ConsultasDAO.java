package dao;

import jakarta.persistence.EntityManager;
import modelo.Consultas;
import util.JPAUtil;
import java.util.List;

public class ConsultasDAO {

    private static ConsultasDAO instance = null;

    // Ya no necesitamos lanzar excepciones de SQL ni ClassNotFound en el constructor
    private ConsultasDAO() { }

    public static ConsultasDAO getInstance() {
        if (instance == null) {
            instance = new ConsultasDAO();
        }
        return instance;
    }

    /**
     * Guarda la consulta usando JPA
     */
    public void insertar(Consultas c) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(c); // JPA se encarga de generar el INSERT
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Por si necesitas ver los mensajes desde un panel de admin
     */
    public List<Consultas> listar() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Consultas c ORDER BY c.id DESC", Consultas.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}