package ProyectoLinda.proxy;

import java.net.ServerSocket;

/**
 * Main del Proxy.
 */
public class ServidorCentralLinda {
    /**
     * Pre: Ninguna.
     * Post: Inicio servidor 8000.
     */
    public static void main(String[] args) {
        try (ServerSocket s = new ServerSocket(8000)) {
            System.out.println("LINDA Proxy (8000) esperando clientes...");
            while (true) {
                new HiloGestorCliente(s.accept()).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}