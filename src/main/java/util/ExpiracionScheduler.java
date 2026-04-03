package util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import modelo.Reserva;
import modelo.Venta;
import modelo.EstadoReserva;
import modelo.EstadoVehiculo;
import modelo.EstadoVenta;

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

            List<Reserva> vencidos = em.createQuery(
            	"SELECT r FROM Reserva r " +
                "JOIN FETCH r.coche " +
                "LEFT JOIN FETCH r.venta " +
                "WHERE r.estado = :estado " +
                "AND r.fechaExpiracion < :ahora", Reserva.class)
                .setParameter("estado", EstadoReserva.ACTIVA)
                .setParameter("ahora", LocalDateTime.now())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) 
                .getResultList();

            for (Reserva r : vencidos) {
                // expiramos la reserva
                r.setEstado(EstadoReserva.EXPIRADA);
                r.setObservaciones("Reserva expirada automáticamente. El cliente no completó el pago.");

                // castigo por no pagar (a pagar, a pagar!)
                Venta v = r.getVenta(); 
                if (v != null) {                   
                    // penalizamos esa venta
                    v.setEstado(EstadoVenta.PENALIZADA);                  
                    // La fecha de pago de la venta la igualamos a la de la reserva
                    v.setFechaPago(r.getFechaReserva());
                }

                // liberamos el auto
                if (r.getCoche() != null) {
                    r.getCoche().setEstado(EstadoVehiculo.DISPONIBLE);
                }
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