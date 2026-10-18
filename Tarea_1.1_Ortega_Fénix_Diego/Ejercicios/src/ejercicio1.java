import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;


public class ejercicio1 {
    public static void main(String[] args){
        // Obtener la ruta del directorio o fichero desde los argumentos de línea de comandos
        String ruta = (args.length > 0) ? args[0] : ".";
        Path fich = Path.of(System.getProperty("user.home"));
        System.out.println(fich);

        // Comprobar si el fichero o directorio existe
        if (!Files.exists(fich)){
            System.out.printf(
                "No existe el fichero o directorio (%s)",
                ruta
            );
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
                "%s es un fichero.\n",
                ruta
            );

            // Listar los ficheros y directorios en el directorio actual
            try (Stream<Path> ficheros = Files.list(fich)){
                ficheros.forEach(f ->{
                    mostrarInformacion(f);
                });
            }catch (IOException e){
                System.out.println(
                    "Error al acceder al directorio: " + e.getMessage()
                );
            }
        }
    }
    
    /*
    *Metodo para mostrar la información del archivo o directorio
    */
    public static void mostrarInformacion(Path f){
        try{
            System.out.print(f.getFileName());
            if (Files.isDirectory(f)){
                System.out.print("/");
            }
            if (Files.isRegularFile(f)){
                long bytes = Files.size(f);
                System.out.print("\t" + formatearBytes(bytes));
            }
            // Obtener la fecha de última modificación
            FileTime tiempo = Files.getLastModifiedTime(f);
            LocalDateTime fecha = LocalDateTime.ofInstant(tiempo.toInstant(), ZoneId.systemDefault());
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            System.out.print("\t" + fecha.format(formato));
            // Obtener el propietario del archivo o directorio
            System.out.print("\t" + Files.getOwner(f));
            System.out.println();
        } catch (IOException e){
            System.out.println("Error al obtener información de " + f.getFileName());
        }
    }

    /*
    *Método para calcular los bytes
    */
    public static String formatearBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        
        String[] unidades = {"B", "KB", "MB", "GB", "TB"};
        double valor = bytes;
        int unidadIndex = 0;
        
        while (valor >= 1024 && unidadIndex < unidades.length - 1) {
            valor /= 1024;
            unidadIndex++;
        }
        
        return String.format("%.2f %s", valor, unidades[unidadIndex]);
    }
}
