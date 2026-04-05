package servicio;

import dao.ReservaDAO;
import dao.UsuarioDAO;
import dto.ReservaDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import modelo.*;
import util.EmailUtil;
import util.JPAUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;

public class ReservaService {

    private static ReservaService instance = null;
    private ReservaService() {}
    public static ReservaService getInstance() {
        if (instance == null) instance = new ReservaService();
        return instance;
    }

    // ─── CREAR (cliente registrado) ───
    
    public void crear(Usuario usuario, int idCoche, double importeReserva, String metodoPagoStr) {
    	
    	if (importeReserva < 500) {
            throw new IllegalArgumentException("El importe mínimo de la señal debe ser de 500€.");       
    	}
    	
    	MetodoPago metodoDeLaReserva;
        try {
            metodoDeLaReserva = MetodoPago.valueOf(metodoPagoStr.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("El método de pago '" + metodoPagoStr + "' no es válido.");
        }
    	
    	EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            
            Coche cocheDB = em.find(Coche.class, idCoche, LockModeType.PESSIMISTIC_WRITE);
            if (cocheDB == null || !cocheDB.isDisponible())
                throw new IllegalStateException("Coche no disponible.");
            
            Reserva r = new Reserva(usuario, cocheDB, metodoDeLaReserva, importeReserva);
            r.setTransaccionId(UUID.randomUUID().toString());
            r.setObservaciones("Importe pendiente de abonar: " + r.getImportePendiente() + "€.");

            Venta v = new Venta(r, usuario, cocheDB, importeReserva, metodoDeLaReserva); 
            v.setEstado(EstadoVenta.PENDIENTE);
            v.setTransaccionId(r.getTransaccionId());
            v.setObservaciones("Importe pendiente de abonar: " + r.getImportePendiente() + "€.");
            
            r.setVenta(v);
            cocheDB.setEstado(EstadoVehiculo.RESERVADO);

            em.persist(r); 
            em.getTransaction().commit(); 
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    

    // ─── CREAR (nueva reserva el admin en la tienda (creando a su vez el nuevo cliente) ───
    public void crearConNuevoUsuario(Usuario nuevoCliente, int idCoche, double importeReserva, String metodoStr) {
    	
    	MetodoPago metodo;
        try {
            metodo = MetodoPago.valueOf(metodoStr.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("El método de pago '" + metodoStr + "' no es válido.");
        }
        
    	// validamos que el usuario o email no existan ya en la base de datos para evitar conflictos
    	if (UsuarioDAO.getInstance().existeUsuarioOEmail(nuevoCliente.getUsuario(), nuevoCliente.getEmail())) {
            throw new IllegalArgumentException("Vaya, ese nombre de usuario o email ya están registrados.");
        }
    	
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
                throw new IllegalStateException("Coche no disponible para reserva.");
            }

            // reserva 
            Reserva r = new Reserva(nuevoCliente, cocheDB, metodo, importeReserva);
            r.setTransaccionId(UUID.randomUUID().toString());  
            r.setEstado(EstadoReserva.ACTIVA);
            r.setObservaciones("Importe pendiente de abonar: " + r.getImportePendiente() + "€.");

            // en ventas
            Venta v = new Venta(r, nuevoCliente, cocheDB, importeReserva, metodo); 
            v.setEstado(EstadoVenta.PENDIENTE); 
            v.setTransaccionId(r.getTransaccionId()); 
            v.setObservaciones("Importe pendiente de abonar: " + r.getImportePendiente() + "€.");

            r.setVenta(v); 
            cocheDB.setEstado(EstadoVehiculo.RESERVADO);

            em.persist(r);             
            em.getTransaction().commit(); 
            
            EmailUtil.enviarBienvenida(nuevoCliente.getEmail(), passPlano, cocheDB.getMarca() + " " + cocheDB.getModelo(), "reserva");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace(); 
            throw new RuntimeException("Fallo en la transacción: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    
    // ─── COMPLETAR (admin cobra en tienda) ───
   
    public void completar(int idReserva) { 
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Buscamos la reserva con bloqueo para que nadie la toque mientras cobramos
            Reserva r = em.find(Reserva.class, idReserva, LockModeType.PESSIMISTIC_WRITE);
            
            if (r == null) throw new IllegalArgumentException("Reserva no encontrada.");
            if (!r.isActiva()) throw new IllegalStateException("Solo se pueden completar reservas activas.");           

            Venta v = r.getVenta();
            if (v == null) throw new IllegalStateException("Esta reserva no tiene una venta asociada.");

            // solo permitimos transferencia en la tienda fisica
            v.setMetodoPago(MetodoPago.TRANSFERENCIA); 
            
            String miIdSeguro = UUID.randomUUID().toString();
            
            v.setTransaccionId(miIdSeguro);
            
            // llenamos el campo con el importe final del coche, que es lo que se ha pagado en total
            v.setImporteAbonado(r.getCoche().getPrecio());            
            v.setFechaPago(LocalDateTime.now());           
            
            // venta y reserva pasan a finalizadas
            v.setEstado(EstadoVenta.FINALIZADA);
            v.setObservaciones("Venta completada en tienda física.");
            
            r.setEstado(EstadoReserva.FINALIZADA);
            
            // El coche pasa a estar vendido y sale del inventario disponible
            if (r.getCoche() != null) {
                r.getCoche().setEstado(EstadoVehiculo.VENDIDO);
            }

            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al completar la venta: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    

    // ─── CANCELAR (admin) ───
    
    public void cancelar(int id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            
            // buscamos la reserva con bloqueo para evitar que se modifique mientras la cancelamos
            Reserva r = em.find(Reserva.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (r == null) throw new IllegalArgumentException("Reserva no encontrada.");
            if (!r.isActiva()) throw new IllegalStateException("Solo se pueden cancelar reservas ACTIVAS.");

            Venta v = r.getVenta();

            r.setEstado(EstadoReserva.CANCELADA);
            r.setObservaciones("Reserva cancelada el " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            // actualizamos ventas
            if (v != null) { 
                v.setEstado(EstadoVenta.CANCELADA);
                v.setImporteAbonado(0); // La venta final sí es 0 porque no se cerró
                v.setObservaciones("Venta cancelada.");
            }

            // coche liberado si existía
            if (r.getCoche() != null) {
                r.getCoche().setEstado(EstadoVehiculo.DISPONIBLE);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al cancelar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    
    // ─── BORRADO PERMANENTE (SUPERUSER) ───
    public void borradoPermanente(int idReserva) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Reserva reserva = em.find(Reserva.class, idReserva, LockModeType.PESSIMISTIC_WRITE);

            if (reserva != null) {

                if (reserva.getCoche() != null) {
                    Coche coche = reserva.getCoche();
                    coche.setEstado(EstadoVehiculo.DISPONIBLE);
          
                    em.merge(coche); 
                }
                em.remove(reserva);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Error en el borrado permanente: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ─── LISTAR (admin) ─── 
    
    public Map<String, Object> ListarAdmin(String busqueda, String estadoParam, int pagina, int porPagina) {
        EstadoReserva estado = null;
        if (estadoParam != null && !estadoParam.isBlank()) { 
            estado = EstadoReserva.desdeTexto(estadoParam);  
        }

        List<Reserva> listaReservas = ReservaDAO.getInstance().listarAdmin(busqueda, estado, pagina, porPagina);

        List<ReservaDTO> listaDTO = listaReservas.stream().map(ReservaDTO::new).collect(Collectors.toList());

        long totalRegistros = ReservaDAO.getInstance().contarAdmin(busqueda, estado); 
        int totalPaginas = (int) Math.ceil((double) totalRegistros / porPagina);    

        return Map.of(
            "reservas", listaDTO, 
            "totalPaginas", totalPaginas,
            "paginaActual", pagina
        ); 
    }
    
    
    // ─── LISTAR (para cliente sus reservas) ───
    
    public List<ReservaDTO> listarPorUsuario(int idUsuario, int pagina, int porPagina) {

        return ReservaDAO.getInstance().listarPorUsuario(idUsuario, pagina, porPagina)
                         .stream()
                         .map(ReservaDTO::new)
                         .collect(Collectors.toList());
    }

    // ─── CONTAR (para paginación en cliente) ───
    
    public long contarPorUsuario(int idUsuario) {
        return ReservaDAO.getInstance().contarPorUsuario(idUsuario);
    }

    
    // ─── DETALLE ───
    
    public ReservaDTO obtenerPorId(int id) {
        Reserva r = ReservaDAO.getInstance().obtenerPorId(id);
        if (r == null) return null;

        return new ReservaDTO(r);
    }
      
}
