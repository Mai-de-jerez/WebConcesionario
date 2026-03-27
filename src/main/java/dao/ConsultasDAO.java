package dao;

import jakarta.persistence.EntityManager;
import modelo.Consultas;
import util.JPAUtil;

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
     * Inserta una nueva consulta en la base de datos.
     * @param c La consulta a insertar
     */
    public void insertar(Consultas c) {
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

}