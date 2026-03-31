package util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import modelo.ReservaPedido;
import modelo.EstadoPedido;
import modelo.EstadoVehiculo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@WebListener
public class ExpiracionScheduler implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::expirarVencidos, 0, 30, TimeUnit.MINUTES);
    }
    
    
    private void expirarVencidos() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            List<ReservaPedido> vencidos = em.createQuery(
                "SELECT rp FROM ReservaPedido rp JOIN FETCH rp.coche " +
                "WHERE rp.estado = :estado " +
                "AND rp.fechaExpiracion < :ahora", ReservaPedido.class)
                .setParameter("estado", EstadoPedido.PENDIENTE)
                .setParameter("ahora", LocalDateTime.now())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) 
                .getResultList();

            for (ReservaPedido rp : vencidos) {
                rp.setEstado(EstadoPedido.EXPIRADO);

                rp.setImporteFinalAbonado(rp.getImporteSenal());

                rp.getCoche().setEstado(EstadoVehiculo.DISPONIBLE);
                
                rp.setObservaciones("Reserva expirada automáticamente por falta de pago.");
            }

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("ERROR en Scheduler: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) scheduler.shutdownNow();
    }
}