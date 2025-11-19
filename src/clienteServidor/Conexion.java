package clienteServidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase base que gestiona las conexiones de red para el sistema cliente-servidor.
 * Proporciona la infraestructura de sockets necesaria tanto para clientes como para servidores.
 *
 * Esta clase encapsula la lógica de creación de sockets según el tipo de entidad
 * (cliente o servidor) que se desee implementar.
 *
 * @author Sistema PSP
 * @version 1.0
 */
public class Conexion {
    /** Socket del servidor para aceptar conexiones entrantes */
    protected ServerSocket ss;

    /** Socket del cliente para la comunicación con el servidor */
    protected Socket cs;

    /**
     * Constructor que crea una conexión según el tipo especificado.
     *
     * Si el tipo es "servidor", crea un ServerSocket en el puerto especificado
     * para aceptar conexiones entrantes. Para cualquier otro tipo, crea un Socket
     * cliente que se conecta al host y puerto especificados.
     *
     * @param tipo Tipo de conexión: "servidor" para crear un ServerSocket,
     *             cualquier otro valor para crear un Socket cliente
     * @param host Dirección del host (solo usado para clientes, puede ser null para servidor)
     * @param puerto Número de puerto para la conexión
     * @throws IOException Si ocurre un error al crear el socket
     */
    public Conexion(String tipo, String host, int puerto) throws IOException {
        if(tipo.equalsIgnoreCase("servidor")) {
            ss = new ServerSocket(puerto);
        } else {
            cs = new Socket(host, puerto);
        }
    }
}