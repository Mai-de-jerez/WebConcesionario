package servicio;

import dao.CocheDAO;
import jakarta.servlet.http.Part;
import modelo.Coche;
import util.ImagenUtil;

import java.util.List;
import java.util.Map;

public class CocheService {
	
	private final String PATH_IMAGENES = System.getenv("PATH_FOTOS");
	private static final String IMG_DEFECTO = "sin-foto.png";
	private final CocheDAO cocheDAO = CocheDAO.getInstance();
	
    private static CocheService instance = null;   

    private CocheService() {}

    public static CocheService getInstance() {
        if (instance == null) {
            instance = new CocheService();
        }
        return instance;
    }

     
    public Map<String, Object> listarParaAdmin(String busqueda, int pagina, int porPagina) {
        if (pagina < 1) pagina = 1;

      
        List<Coche> lista = cocheDAO.listarAdmin(busqueda, pagina, porPagina);
        long totalCoches = cocheDAO.contarAdmin(busqueda);

     
        int totalPaginas = (int) Math.ceil((double) totalCoches / porPagina);

        return Map.of(
            "coches", lista,
            "totalPaginas", totalPaginas, 
            "paginaActual", pagina
        );
    } 

    public long totalCochesAdmin(String busqueda) {
        return cocheDAO.contarAdmin(busqueda);
    }

    public List<Coche> listarParaTienda(String busqueda, int pagina, int porPagina) {
        return cocheDAO.listarTienda(busqueda, pagina, porPagina);
    }

    public long totalCochesTienda(String busqueda) {
        return cocheDAO.contarTienda(busqueda);
    }
    
    
    public void guardarCoche(Coche c, Part imagenPart) throws Exception {
        String nombreImagenUnico = IMG_DEFECTO;

        try {
            // si hay foto, se guarda, si no, la de por defecto
            if (imagenPart != null && imagenPart.getSize() > 0) {
                nombreImagenUnico = util.ImagenUtil.guardarArchivo(imagenPart, PATH_IMAGENES, IMG_DEFECTO);
                c.setImagen(nombreImagenUnico);
            } else if (c.getImagen() == null) { 
                c.setImagen(IMG_DEFECTO);
            }

            cocheDAO.insertar(c);  

        } catch (Exception e) {
            // Si la base de datos falla, borramos la imagen que acabamos de subir para no dejar basura
            if (!nombreImagenUnico.equals(IMG_DEFECTO)) {
                ImagenUtil.borrarArchivo(nombreImagenUnico, PATH_IMAGENES, IMG_DEFECTO);
            }
            throw e; 
        }
    }
    
    // sobrecargamos método en editar
    public void guardarCoche(Coche c, Part imagenPart, String imagenActual) throws Exception {
        String nombreImagenNueva = null;

        try {
            if (imagenPart != null && imagenPart.getSize() > 0) {
                // guardamos la nueva en el disco
                nombreImagenNueva = ImagenUtil.guardarArchivo(imagenPart, PATH_IMAGENES, IMG_DEFECTO);
                c.setImagen(nombreImagenNueva);

                // si la subida ha ido bien, borramos la vieja (siempre que no sea la de defecto)
                if (imagenActual != null && !imagenActual.equals(IMG_DEFECTO)) {
                    ImagenUtil.borrarArchivo(imagenActual, PATH_IMAGENES, IMG_DEFECTO);
                }
            } else {
                // Si no hay foto nueva en el Part, mantenemos la que ya tenía
                c.setImagen(imagenActual);
            }

            cocheDAO.actualizar(c);

        } catch (Exception e) {
            // Si algo falla, borramos la nueva imagen que acabamos de subir para no dejar basura
            if (nombreImagenNueva != null) {
                ImagenUtil.borrarArchivo(nombreImagenNueva, PATH_IMAGENES, IMG_DEFECTO);
            }
            throw e;
        }
    }

    
    public void eliminarCoche(int id) throws Exception {
        // buscamos el coche para saber que imagen hay que borrar del disco
        Coche coche = cocheDAO.obtenerPorId(id);
        
        if (coche != null) {
            String imagenABorrar = coche.getImagen();

            // borramos de la Base de Datos
            cocheDAO.eliminar(id);

            // si la base de datos ha ido bien, borramos la imagen del disco si no es la de por defecto
            if (imagenABorrar != null && !imagenABorrar.equals(IMG_DEFECTO)) {
                ImagenUtil.borrarArchivo(imagenABorrar, PATH_IMAGENES, IMG_DEFECTO);
            }
        }
    }

    public Coche obtenerCoche(int id) {
        return cocheDAO.obtenerPorId(id);
    }

    public List<Coche> obtenerNovedades() {
        return cocheDAO.obtenerTresUltimos();
    }
}


