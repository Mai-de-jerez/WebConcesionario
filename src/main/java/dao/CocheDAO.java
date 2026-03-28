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

    public static synchronized CocheDAO getInstance() {
        if (instance == null) {
            instance = new CocheDAO();
        }
        return instance;
    }

    
    public List<Coche> listarAdmin(String busqueda, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
   
            StringBuilder jpql = new StringBuilder("SELECT c FROM Coche c WHERE 1=1");
     
            boolean esSoloNumero = (busqueda != null && busqueda.matches("\\d+"));

            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    jpql.append(" AND c.id = :idExacto");
                } else {
                    jpql.append(" AND (LOWER(c.marca) LIKE :b OR LOWER(c.modelo) LIKE :b " +
                                "OR LOWER(c.matricula) LIKE :b OR LOWER(c.tipoMotor) LIKE :b)");
                }
            }

            jpql.append(" ORDER BY c.id DESC");
            TypedQuery<Coche> query = em.createQuery(jpql.toString(), Coche.class);

            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    query.setParameter("idExacto", Integer.parseInt(busqueda));
                } else {
                    query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
                }
            }

            query.setFirstResult((pagina - 1) * porPagina);
            query.setMaxResults(porPagina);
            return query.getResultList();

        } finally {
            em.close();
        }
    }

    public List<Coche> listarTienda(String busqueda, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT c FROM Coche c WHERE c.estado = :estado";

            if (busqueda != null && !busqueda.isBlank()) {
                jpql += " AND (LOWER(c.marca) LIKE :b OR LOWER(c.modelo) LIKE :b " +
                        "OR LOWER(c.matricula) LIKE :b OR LOWER(c.tipoMotor) LIKE :b)";
            }

            jpql += " ORDER BY c.id DESC";

            TypedQuery<Coche> query = em.createQuery(jpql, Coche.class);
            query.setParameter("estado", EstadoVehiculo.DISPONIBLE);

            if (busqueda != null && !busqueda.isBlank()) {
                query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
            }

            query.setFirstResult((pagina - 1) * porPagina);
            query.setMaxResults(porPagina);
            return query.getResultList();

        } finally {
            em.close();
        }
    }

    public long contarAdmin(String busqueda) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(c) FROM Coche c WHERE 1=1";
            if (busqueda != null && !busqueda.isBlank()) {
                jpql += " AND (LOWER(c.marca) LIKE :b OR LOWER(c.modelo) LIKE :b " +
                        "OR LOWER(c.matricula) LIKE :b OR LOWER(c.tipoMotor) LIKE :b)";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (busqueda != null && !busqueda.isBlank()) {
                query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
            }
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public long contarTienda(String busqueda) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(c) FROM Coche c WHERE c.estado = :estado";
            if (busqueda != null && !busqueda.isBlank()) {
                jpql += " AND (LOWER(c.marca) LIKE :b OR LOWER(c.modelo) LIKE :b " +
                        "OR LOWER(c.matricula) LIKE :b OR LOWER(c.tipoMotor) LIKE :b)";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("estado", EstadoVehiculo.DISPONIBLE);
            if (busqueda != null && !busqueda.isBlank()) {
                query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
            }
            return query.getSingleResult();
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
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
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
                em.getTransaction().commit(); 
            }
        } finally {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); 
            }
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
            if (c != null) c.setEstado(nuevoEstado);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Coche> obtenerTresUltimos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Coche c ORDER BY c.id DESC", Coche.class)
                    .setMaxResults(3)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}


