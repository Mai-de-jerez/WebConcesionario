package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import modelo.Usuario;
import util.JPAUtil;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;

public class UsuarioDAO {

    private static UsuarioDAO instance = null;

    private UsuarioDAO() {}

    
    public static synchronized UsuarioDAO getInstance() {
        if (instance == null) {
            instance = new UsuarioDAO();
        }
        return instance;
    }

    public Usuario validar(String userOrEmail, String passPlano) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT u FROM Usuario u WHERE u.usuario = :dato OR u.email = :dato";
            Usuario u = em.createQuery(jpql, Usuario.class)
                          .setParameter("dato", userOrEmail)
                          .getSingleResult();

            // Verificamos el hash de la contraseña
            if (u != null && BCrypt.checkpw(passPlano, u.getPassword())) {
                return u;
            }
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
        return null;
    }

    
    public List<Usuario> listar(String busqueda, int pagina, int porPagina) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT u FROM Usuario u WHERE 1=1");

            boolean esSoloNumero = (busqueda != null && busqueda.matches("\\d+"));

            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    jpql.append(" AND u.id_usuario = :idExacto");
                } else {
                    jpql.append(" AND (LOWER(u.nombre) LIKE :b OR LOWER(u.apellidos) LIKE :b " +
                            "OR LOWER(u.usuario) LIKE :b OR LOWER(u.email) LIKE :b)");
                }
            }

            jpql.append(" ORDER BY u.id_usuario ASC");

            TypedQuery<Usuario> query = em.createQuery(jpql.toString(), Usuario.class);

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


    
    public long contarTodos(String busqueda) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            boolean esSoloNumero = (busqueda != null && busqueda.matches("\\d+"));
            String jpql = "SELECT COUNT(u) FROM Usuario u WHERE 1=1";

            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    jpql += " AND u.id_usuario = :idExacto";
                } else {
                    jpql += " AND (LOWER(u.nombre) LIKE :b OR LOWER(u.apellidos) LIKE :b " +
                            "OR LOWER(u.usuario) LIKE :b OR LOWER(u.email) LIKE :b)";
                }
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);

            if (busqueda != null && !busqueda.isBlank()) {
                if (esSoloNumero) {
                    query.setParameter("idExacto", Integer.parseInt(busqueda));
                } else {
                    query.setParameter("b", "%" + busqueda.toLowerCase() + "%");
                }
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    // --- 🆕 REGISTRAR ---
    public void registrar(Usuario u) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            // Encriptamos la clave antes de guardar
            String hash = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());
            u.setPassword(hash);
            em.persist(u);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // --- 🔍 OBTENER POR ID ---
    public Usuario obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    // --- 🔄 ACTUALIZAR ---
    public void actualizar(Usuario u) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Lógica de contraseña: si el usuario cambió la clave (no es un hash), encriptamos
            if (!u.getPassword().startsWith("$2a$")) {
                u.setPassword(BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()));
                System.out.println("🔐 Contraseña actualizada y encriptada.");
            }

            em.merge(u);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // --- 🎫 GESTIÓN DE TOKENS (RECUPERACIÓN) ---
    public void guardarToken(String email, String token) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            // Usamos Native Query porque dependemos de funciones de MySQL como DATE_ADD
            String sql = "UPDATE usuarios SET reset_token = ?, token_expiracion = DATE_ADD(NOW(), INTERVAL 30 MINUTE) WHERE email = ?";
            em.createNativeQuery(sql)
              .setParameter(1, token)
              .setParameter(2, email)
              .executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public boolean validarToken(String token) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Buscamos si existe un usuario con ese token que no haya expirado
            String sql = "SELECT id_usuario FROM usuarios WHERE reset_token = ? AND token_expiracion > NOW()";
            Query query = em.createNativeQuery(sql).setParameter(1, token);
            return !query.getResultList().isEmpty();
        } finally {
            em.close();
        }
    }

    public boolean cambiarPasswordConToken(String token, String nuevaPassword) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            String hash = BCrypt.hashpw(nuevaPassword, BCrypt.gensalt());
            
            // Actualizamos la clave y limpiamos el token
            int actualizados = em.createQuery("UPDATE Usuario u SET u.password = :pass, u.resetToken = NULL, u.tokenExpiracion = NULL WHERE u.resetToken = :token")
                                 .setParameter("pass", hash)
                                 .setParameter("token", token)
                                 .executeUpdate();
                                 
            em.getTransaction().commit();
            return actualizados > 0;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }
    
    public boolean existeUsuarioOEmail(String username, String email) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Buscamos si hay alguien tiene ese email o susario ya registrado
            String jpql = "SELECT COUNT(u) FROM Usuario u WHERE u.usuario = :user OR u.email = :email";
            Long conteo = em.createQuery(jpql, Long.class)
                            .setParameter("user", username)
                            .setParameter("email", email)
                            .getSingleResult();
            return conteo > 0;
        } finally {
            em.close();
        }
    }

    // --- 🗑️ ELIMINAR ---
    public void eliminar(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Usuario u = em.find(Usuario.class, id);
            if (u != null) {
                em.remove(u);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}