
package clienteServidor;

import java.io.IOException;

/**
 * Clase principal para iniciar un cliente en el sistema cliente-servidor.
 *
 * Esta clase valida los argumentos de línea de comandos y crea una instancia
 * del cliente para conectarse al servidor especificado.
 *
 * Uso desde línea de comandos:
 * java clienteServidor.MainCliente IP_servidor puerto
 *
 * Ejemplos:
 * java clienteServidor.MainCliente 192.168.1.100 6000
 * java clienteServidor.MainCliente localhost 6000
 *
 * @author Sistema PSP
 * @version 1.0
 */
public class MainCliente {

    /**
     * Método principal que inicia el cliente.
     *
     * @param args Array de argumentos: args[0] = IP del servidor, args[1] = puerto
     * @throws IOException Si ocurre un error de conexión con el servidor
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Uso: java clienteServidor.MainCliente <IP_servidor> <puerto>");
            System.out.println("Ejemplo: java clienteServidor.MainCliente 192.168.1.100 6000");
            System.out.println("Para localhost: java clienteServidor.MainCliente localhost 6000");
            System.exit(1);
        }

        String ipServidor = args[0];
        try {
            int puerto = Integer.parseInt(args[1]);
            Cliente cli = new Cliente(ipServidor, puerto);

            System.out.println("Conectando a " + ipServidor + ":" + puerto + "\n");
            cli.startClient();
        } catch (NumberFormatException e) {
            System.out.println("Error: El puerto debe ser un número válido");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            System.exit(1);
        }
    }
}