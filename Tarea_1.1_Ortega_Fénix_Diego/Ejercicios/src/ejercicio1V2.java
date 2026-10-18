import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;


public class ejercicio1V2 {
    // Variables para controlar el número máximo de resultados y la profundidad máxima
    private static int resultadosMostrados = 0;
    private static int maxResultados = Integer.MAX_VALUE;

    public static void main(String[] args){
        // Obtener la ruta del directorio o fichero desde los argumentos de línea de comandos
        String ruta = (args.length > 0) ? args[0] : ".";
        int profundidadMaxima = Integer.MAX_VALUE;
        Path fich = Path.of(System.getProperty("user.home"));
        System.out.println(fich);

        if (!Files.exists(fich)){
            System.out.printf(
                "No existe el fichero o directorio (%s)",
                ruta
            );
            return;
        }

        // Comprobar si es un fichero o un directorio
        try{
            // Comprobar si se ha proporcionado un argumento para el número máximo de resultados
            if (args.length > 0){
                maxResultados = Integer.parseInt(args[0]);
                if (maxResultados <= 0){
                    System.out.println("El número máximo de resultados debe ser mayor o igual a 0.");
                    return;
                }
            }
            
            // Comprobar si se ha proporcionado un argumento para la profundidad máxima
            if (args.length > 1){
                profundidadMaxima = Integer.parseInt(args[1]);
                if (profundidadMaxima < 0){
                    System.out.println("La profundidad máxima debe ser mayor o igual a 0.");
                    return;
                }
            }
        } catch (NumberFormatException e){
            System.out.println("Error: Los argumentos deben ser números enteros.");
            return;
        }

        // Comprobar si es un fichero o un directorio
        if (Files.isRegularFile(fich)){
            System.out.printf(
                "%s es un fichero.\n",
                ruta
            );
        } else if (Files.isDirectory(fich)){
            System.out.printf(
                "%s es un directorio.\n",
                ruta
            );
        }
        // Llamada al método recursivo para listar ficheros y directorios
        listarRecursivo(fich, fich, profundidadMaxima, 0);
    }

    /*
    *Método recursivo para listar ficheros y directorios
    */
    public static void listarRecursivo(Path raiz, Path dirActual, int profundidadMaxima, int profundidadActual) {
        if (profundidadActual > profundidadMaxima || resultadosMostrados >= maxResultados) {
            return;
        }

        // Listar los ficheros y directorios en el directorio actual
        try (Stream<Path> ficheros = Files.list(dirActual)) {
            ficheros.forEach(f -> {
                if (resultadosMostrados >= maxResultados) {
                    return;
                }
                
                // Evitar mostrar ficheros ocultos y enlaces simbólicos
                try {
                    if (Files.isHidden(f) || Files.isSymbolicLink(f)) {
                        return; 
                    }
                    Path rutaRelativa = raiz.relativize(f);
                    
                    // Mostrar información del archivo o directorio
                    mostrarInformacion(f, rutaRelativa);
                    resultadosMostrados++;
                    
                    // Llamada recursiva si es un directorio y no se ha alcanzado la profundidad máxima
                    if (Files.isDirectory(f) && profundidadActual < profundidadMaxima) {
                        listarRecursivo(raiz, f, profundidadMaxima, profundidadActual + 1);
                    }
                } catch (IOException e) {
                    // Manejo de errores de lectura por archivo individual
                }
            });
        } catch (IOException e) {
            System.out.println("Error al acceder al directorio: " + e.getMessage());
        }
    }
    /*
    *Metodo para mostrar la información del archivo o directorio
    */
    public static void mostrarInformacion(Path f, Path rutaRelativa) {
        try {
            if (Files.isDirectory(f)) {
                System.out.println(rutaRelativa);
            } else if (Files.isRegularFile(f)) {
                FileTime tiempo = Files.getLastModifiedTime(f);
                LocalDateTime fecha = LocalDateTime.ofInstant(tiempo.toInstant(), ZoneId.systemDefault());
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                
                String propietario = Files.getOwner(f).getName();
                long bytes = Files.size(f);
                String tamano = formatearBytes(bytes);
                
                // Mostrar la información en formato tabular
                System.out.printf("%s | %s | %s | %s%n", 
                    rutaRelativa, 
                    fecha.format(formato), 
                    propietario, 
                    tamano);
            }
        } catch (IOException e) {
            System.out.println("Error al obtener información de " + rutaRelativa);
        }
    }

    /*
    *Método para calcular los bytes
    */
    public static String formatearBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        
        String[] unidades = {"B", "KiB", "MiB", "GiB", "TiB"};
        double valor = bytes;
        int unidadIndex = 0;
        
        while (valor >= 1024 && unidadIndex < unidades.length - 1) {
            valor /= 1024;
            unidadIndex++;
        }
        
        return String.format("%.2f %s", valor, unidades[unidadIndex]);
    }
}
