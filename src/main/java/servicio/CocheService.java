package servicio;

import dao.CocheDAO;
import modelo.Coche;
import java.util.List;

public class CocheService {

    private CocheDAO cocheDAO = CocheDAO.getInstance();

    // Lógica para el ADMIN (ve todo y gestiona)
    public List<Coche> listarParaAdmin(String busqueda, int pagina, int porPagina) {
        return cocheDAO.listarAdmin(busqueda, pagina, porPagina);
    }

    public long totalCochesAdmin(String busqueda) {
        return cocheDAO.contarAdmin(busqueda);
    }

    // Lógica para la TIENDA (solo lo disponible)
    public List<Coche> listarParaTienda(String busqueda, int pagina, int porPagina) {
        return cocheDAO.listarTienda(busqueda, pagina, porPagina);
    }

    public long totalCochesTienda(String busqueda) {
        return cocheDAO.contarTienda(busqueda);
    }

    // Gestión pura
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
