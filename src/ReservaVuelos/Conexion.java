package ReservaVuelos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase que gestiona la conexión cliente-servidor para el sistema de reserva de vuelos.
 * Permite crear tanto sockets de servidor como de cliente según el tipo especificado.
 */
public class Conexion {
    private final int PUERTO = 1234; // Puerto para la conexión
    private final String HOST = "localhost"; // Host para la conexión
    protected ServerSocket ss; // Socket del servidor
    protected Socket cs; // Socket del cliente

    /**
     * Pre: tipo debe ser "servidor" o cualquier otro valor para cliente
     * Post: crea un ServerSocket en el puerto 1234 si tipo es "servidor",
     *       o crea un Socket cliente conectado a localhost:1234 en caso contrario
     * @param tipo String que indica si se crea un socket de "servidor" o cliente
     *
     */
    public Conexion(String tipo) throws IOException {
        /*
         * Comprobación del tipo de conexión
         */
        if(tipo.equalsIgnoreCase("servidor")) {
            // Se crea el socket para el servidor en puerto 1234
            ss = new ServerSocket(PUERTO);
        } else {
            // Socket para el cliente conectado a localhost en puerto 1234
            cs = new Socket(HOST, PUERTO);
        }
    }
}
