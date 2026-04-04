package servicio;

import dao.VentaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import modelo.*;
import util.JPAUtil;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class VentaService {

    private static VentaService instance = null;
    private VentaService() {}
    public static VentaService getInstance() {
        if (instance == null) instance = new VentaService();
        return instance; 
    }

    // ─── CREAR VENTA (cliente nuevo el admin en la tienda) ───
    public void crearVenta(Usuario nuevoCliente, int idCoche, double importeAbonado) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // usuario
            nuevoCliente.setRol(Rol.CLIENTE); 
            String passPlano = UUID.randomUUID().toString().substring(0, 8);
            nuevoCliente.setPassword(BCrypt.hashpw(passPlano, BCrypt.gensalt()));
            em.persist(nuevoCliente); 

            // coche
            Coche cocheDB = em.find(Coche.class, idCoche, LockModeType.PESSIMISTIC_WRITE);
            if (cocheDB == null || cocheDB.getEstado() != EstadoVehiculo.DISPONIBLE) {
                throw new IllegalStateException("Coche no disponible.");
            }
      
            // en ventas
            Venta v = new Venta();
            v.setUsuario(nuevoCliente);
            v.setCoche(cocheDB);
            v.setMetodoPago(MetodoPago.TRANSFERENCIA); 
            v.setImporteAbonado(importeAbonado); 
            v.setEstado(EstadoVenta.FINALIZADA); 
            v.setTransaccionId(UUID.randomUUID().toString());  
            v.setObservaciones("Venta directa en tienda.");
            
            // actualizamos el coche a vendido
            cocheDB.setEstado(EstadoVehiculo.VENDIDO);

            em.persist(v); 
            
            em.getTransaction().commit(); 
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace(); 
            throw new RuntimeException("Fallo en la transacción: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    
    // ─── CANCELAR (admin) ───
  
    public void cancelar(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
      
            Venta v = em.find(Venta.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (v == null) throw new IllegalArgumentException("Venta no encontrada.");
            
            if (!v.isPendiente()) {
                throw new IllegalStateException("Solo se pueden cancelar ventas en estado PENDIENTE. " +
                                               "Esta venta está actualmente: " + v.getEstado());
            }

            v.setEstado(EstadoVenta.CANCELADA);
            v.setImporteAbonado(0);

 
            if (v.getReserva() != null) {
                Reserva r = v.getReserva();
                r.setEstado(EstadoReserva.CANCELADA);
            }

            if (v.getCoche() != null) {
                v.getCoche().setEstado(EstadoVehiculo.DISPONIBLE);
            }
            
            em.getTransaction().commit();
            System.out.println("DEBUG: Venta ID " + id + " cancelada y coche liberado.");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al cancelar la venta: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ── ELIMINAR PERMANENTEMENTE (superuser) ───
    
    public void borradoPermanenteVenta(int idVenta) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // buscamos la Venta con bloqueo pesimista
            Venta venta = em.find(Venta.class, idVenta, LockModeType.PESSIMISTIC_WRITE);

            if (venta != null) {
                if (venta.getCoche() != null) {
                    Coche coche = venta.getCoche();
                    coche.setEstado(EstadoVehiculo.DISPONIBLE);
                    em.merge(coche); 
                }

                if (venta.getReserva() != null) {
                    Reserva reservaAsociada = venta.getReserva();
                    // borrar también la reserva asociada
                    em.remove(reservaAsociada);
                }

                // borrado de la Venta
                em.remove(venta);
                
                System.out.println("DEBUG: Venta [" + idVenta + "] eliminada permanentemente del sistema.");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Error en el borrado permanente de la venta: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ─── LISTAR VENTAS (admin) ─── 
    
    public Map<String, Object> ListarAdmin(String busqueda, String estadoParam, int pagina, int porPagina) {

        EstadoVenta estado = (estadoParam != null && !estadoParam.isBlank()) 
                             ? EstadoVenta.desdeTexto(estadoParam) : null;

        List<Venta> listaVentas = VentaDAO.getInstance().listarAdmin(busqueda, estado, pagina, porPagina);

        for (Venta v : listaVentas) {
            // Limpiamos el usuario directo de la venta
            if (v.getUsuario() != null) {
                v.getUsuario().setPassword(null);
                v.getUsuario().setReservas(null);
                v.getUsuario().setConsultas(null);
            }
             // gestionamos la reserva asociada
            if (v.getReserva() != null) {
                v.getReserva().setVenta(null); 
                
                if (v.getReserva().getUsuario() != null) {
                    v.getReserva().getUsuario().setPassword(null);
                    v.getReserva().getUsuario().setReservas(null);
                }
            }
        }

        long totalRegistros = VentaDAO.getInstance().contarAdmin(busqueda, estado); 
        int totalPaginas = (int) Math.ceil((double) totalRegistros / porPagina);

        return Map.of(
            "ventas", listaVentas,
            "totalPaginas", totalPaginas,
            "paginaActual", pagina
        ); 
    }
    
    
    // ─── LISTAR VENTAS (cliente) ───
    public List<Venta> listarPorUsuario(int idUsuario, int pagina, int porPagina) {
        return VentaDAO.getInstance().listarPorUsuario(idUsuario, pagina, porPagina);  
    }

    public long contarPorUsuario(int idUsuario) {
        return VentaDAO.getInstance().contarPorUsuario(idUsuario);
    }
 
    
    // ─── DETALLE ───

	public Venta obtenerPorId(int id) {
	    Venta v = VentaDAO.getInstance().obtenerPorId(id);
	
	    if (v != null) {
	        // limpiamos el usuario directo de la Venta
	        if (v.getUsuario() != null) {
	            v.getUsuario().setPassword(null);
	            v.getUsuario().setReservas(null);
	            v.getUsuario().setConsultas(null);
	        }
	
	        // gestionamos la reserva asociada
	        if (v.getReserva() != null) {
	            // cortamos la referencia circular hacia atrás
	            v.getReserva().setVenta(null); 
	
	            // limpiamos el Usuario que está dentro de esa reserva
	            if (v.getReserva().getUsuario() != null) {
	                v.getReserva().getUsuario().setPassword(null);
	                v.getReserva().getUsuario().setReservas(null); 
	            }
	        }
	    }
	    return v;
	}

}