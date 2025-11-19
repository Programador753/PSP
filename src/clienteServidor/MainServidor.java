package clienteServidor;

import java.io.IOException;

/**
 * Clase principal para iniciar el servidor en el sistema cliente-servidor.
 *
 * Esta clase valida los argumentos de línea de comandos y crea una instancia
 * del servidor que escuchará en el puerto especificado.
 *
 * El servidor puede atender hasta 10 clientes de forma concurrente utilizando threads.
 *
 * Uso desde línea de comandos:
 * java clienteServidor.MainServidor puerto
 *
 * Ejemplo:
 * java clienteServidor.MainServidor 6000
 *
 * @author Sistema PSP
 * @version 1.0
 */
public class MainServidor {

    /**
     * Método principal que inicia el servidor.
     *
     * @param args Array de argumentos: args[0] = puerto de escucha
     * @throws IOException Si ocurre un error al iniciar el servidor
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Uso: java clienteServidor.MainServidor <puerto>");
            System.out.println("Ejemplo: java clienteServidor.MainServidor 6000");
            System.exit(1);
        }

        try {
            int puerto = Integer.parseInt(args[0]);
            Servidor serv = new Servidor(puerto);

            System.out.println("Iniciando servidor en puerto " + puerto + "\n");
            serv.startServer();
            System.out.println("Servidor finalizado");
        } catch (NumberFormatException e) {
            System.out.println("Error: El puerto debe ser un número válido");
            System.exit(1);
        }
    }
}