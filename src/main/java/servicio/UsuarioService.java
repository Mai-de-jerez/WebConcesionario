package servicio;

import dao.UsuarioDAO;
import modelo.Usuario;
import java.util.List;

public class UsuarioService {
    private UsuarioDAO usuarioDAO = UsuarioDAO.getInstance();

    public List<Usuario> listar(String busqueda, int pagina, int porPagina) {
        return usuarioDAO.listar(busqueda, pagina, porPagina);
    }

    public long total(String busqueda) {
        return usuarioDAO.contarTodos(busqueda);
    }

    public Usuario obtener(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    public void registrar(Usuario u) {
        usuarioDAO.registrar(u);
    }

    public void actualizar(Usuario u) {
        usuarioDAO.actualizar(u);
    }

    public void eliminar(int id) {
        usuarioDAO.eliminar(id);
    }
}