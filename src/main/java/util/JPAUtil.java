package util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    private static EntityManagerFactory emf;

    static {
        try {
      
            Map<String, String> properties = new HashMap<>();

            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASSWORD");

            if (user != null) properties.put("jakarta.persistence.jdbc.user", user);
            if (pass != null) properties.put("jakarta.persistence.jdbc.password", pass);

            emf = Persistence.createEntityManagerFactory("WebConcesionarioPU", properties);
            
        } catch (Throwable ex) {
            System.err.println("Error inicializando EntityManagerFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void shutdown() {
        if (emf != null) emf.close();
    }
}