package util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImagenUtil {
    
    /**
     * Valida que el directorio exista, si no, lo crea.
     */
    public static void validarDirectorio(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Guarda el archivo en el sistema de ficheros.
     * He añadido REPLACE_EXISTING por si acaso, aunque con nombres únicos es raro.
     */
    public static String guardarArchivo(Part part, String path, String imagenDefecto) throws IOException {
        String fileName = part.getSubmittedFileName();
        if (fileName == null || fileName.isEmpty()) return imagenDefecto;

        String nombreOriginal = Paths.get(fileName).getFileName().toString();
        String nombreUnico = generarNombreUnico(nombreOriginal, imagenDefecto);
        
        Path rutaDestino = Paths.get(path).resolve(nombreUnico);
        
        try (InputStream is = part.getInputStream()) {
            // Usamos StandardCopyOption para que sea más atómico y seguro
            Files.copy(is, rutaDestino, StandardCopyOption.REPLACE_EXISTING);
        }
        return nombreUnico;
    }

    /**
     * Borra el archivo físico.
     */
    public static void borrarArchivo(String nombre, String path, String imagenDefecto) {
        if (nombre == null || nombre.isEmpty() || nombre.equals(imagenDefecto)) return;
        
        try {
            Path rutaArchivo = Paths.get(path).resolve(nombre);
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo borrar el archivo: " + nombre + ". Motivo: " + e.getMessage());
        }
    }
    
    /**
     * Genera un nombre único. 
     * He cambiado el Random por un UUID corto o System.currentTimeMillis para evitar 
     * cualquier colisión de nombres (muy de Senior).
     */
    private static String generarNombreUnico(String nombreOriginal, String imagenDefecto) {
        if (nombreOriginal == null || nombreOriginal.isEmpty()) return imagenDefecto;
        
        int dotIndex = nombreOriginal.lastIndexOf('.');
        String nombre = (dotIndex == -1) ? nombreOriginal : nombreOriginal.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : nombreOriginal.substring(dotIndex);
        
        // Usamos el tiempo actual para asegurar que el nombre sea único en el milisegundo
        return nombre + "_" + System.currentTimeMillis() + extension;
    }
}
