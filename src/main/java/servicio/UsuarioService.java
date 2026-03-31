package servicio;

import dao.UsuarioDAO;
import jakarta.servlet.http.Part;
import modelo.Rol;
import modelo.Usuario;
import util.ImagenUtil;

import java.util.List;

public class UsuarioService {
	
    private UsuarioDAO usuarioDAO = UsuarioDAO.getInstance();
    private final String PATH_IMAGENES = System.getenv("PATH_FOTOS");
    private static final String IMG_DEFECTO = "sin-foto.png";

    public List<Usuario> listar(String busqueda, int pagina, int porPagina) {
        return usuarioDAO.listar(busqueda, pagina, porPagina);
    }

    public long total(String busqueda) {
        return usuarioDAO.contarTodos(busqueda);
    }

    public Usuario obtener(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    
    public void registrar(Usuario nuevo, Usuario ejecutor, Part imagenPart) throws Exception {
        
        // si es admin nivel 2 solo puede crear clientes
        if (ejecutor.getRol().getNivel() != 1) {
            nuevo.setRol(Rol.CLIENTE);
        }

        if (imagenPart != null && imagenPart.getSize() > 0) {
            String nombreFinal = ImagenUtil.guardarArchivo(imagenPart, PATH_IMAGENES, IMG_DEFECTO);
            nuevo.setFoto(nombreFinal);
        } else {
            nuevo.setFoto(IMG_DEFECTO); 
        }

        // A la base de datos
        usuarioDAO.registrar(nuevo); 
    }
    
    
    public void actualizar(Usuario editado, Usuario ejecutor, Part imagenPart, String fotoActual) throws Exception {
    	// Primero obtenemos el usuario original para comparar y mantener datos si es necesario
        Usuario original = usuarioDAO.obtenerPorId(editado.getId_usuario());
        if (original == null) throw new Exception("El usuario no existe.");
        // Si no se proporciona una nueva contraseña, mantenemos la antigua
        if (editado.getPassword() == null || editado.getPassword().trim().isEmpty()) {
            editado.setPassword(original.getPassword()); 
        } 

        // No puedes cambiarte tu propio rol
        if (ejecutor.getId_usuario() == editado.getId_usuario()) {
            editado.setRol(original.getRol());
        }
        // Solo superuser puede cambiar roles ajenos
        if (ejecutor.getRol().getNivel() != 1 && !original.getRol().equals(editado.getRol())) {
            throw new Exception("No tienes permiso para cambiar el rol de este usuario.");
        }

        if (imagenPart != null && imagenPart.getSize() > 0) {
            // Guardamos la nueva usando la variable de entorno PATH_IMAGENES
            String nuevaFoto = ImagenUtil.guardarArchivo(imagenPart, PATH_IMAGENES, IMG_DEFECTO);
            editado.setFoto(nuevaFoto);

            // Si la foto antigua no era la de por defecto, la borramos del disco
            if (fotoActual != null && !fotoActual.equals(IMG_DEFECTO)) {
                ImagenUtil.borrarArchivo(fotoActual, PATH_IMAGENES, IMG_DEFECTO);
            }
        } else {
            // Si no sube nada, mantenemos la que ya tenía
            editado.setFoto(fotoActual);
        }

        usuarioDAO.actualizar(editado);
    }
 

    
    public void eliminar(int id, Usuario ejecutor) throws Exception {
        // Solo el SuperUser (Nivel 1) puede borrar
        if (ejecutor.getRol().getNivel() != 1) {
            throw new Exception("Solo el SuperUser puede eliminar usuarios.");
        }

        // No puedes borrarte a ti mismo
        if (ejecutor.getId_usuario() == id) {
            throw new Exception("No puedes eliminar tu propia cuenta.");
        }

        usuarioDAO.eliminar(id);
    }
}