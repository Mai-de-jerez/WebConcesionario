package servicio;

import dao.CocheDAO;
import modelo.Coche;
import java.util.List;

public class CocheService {

    private static CocheService instance = null;

    private final CocheDAO cocheDAO = CocheDAO.getInstance();

    private CocheService() {}

    public static CocheService getInstance() {
        if (instance == null) {
            instance = new CocheService();
        }
        return instance;
    }

    public List<Coche> listarParaAdmin(String busqueda, int pagina, int porPagina) {
        return cocheDAO.listarAdmin(busqueda, pagina, porPagina);
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

    public void guardarCoche(Coche c) {
        if (c.getId() == 0) {
            cocheDAO.insertar(c);
        } else {
            cocheDAO.actualizar(c);
        }
    }

    public void eliminarCoche(int id) {
        cocheDAO.eliminar(id);
    }

    public Coche obtenerCoche(int id) {
        return cocheDAO.obtenerPorId(id);
    }

    public List<Coche> obtenerNovedades() {
        return cocheDAO.obtenerTresUltimos();
    }
}


