package clienteServidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Conexion {
    protected ServerSocket ss; //Socket del servidor
    protected Socket cs; //Socket del cliente
    
    public Conexion(String tipo, String host, int puerto) throws IOException {//Constructor
        if(tipo.equalsIgnoreCase("servidor")) {
            ss = new ServerSocket(puerto);//Se crea el socket para el servidor en el puerto especificado
        } else {
            cs = new Socket(host, puerto); //Socket para el cliente en el host y puerto especificados
        }
    }
}
