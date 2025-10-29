package ReservaVuelos;

import java.io.IOException;

/**
 * Clase principal que inicia el servidor de reservas de vuelos.
 *
 */
public class MainServidor {
    /**
     * Pre: ninguna
     * Post: Se crea e inicia el servidor de reservas. El servidor queda
     *       en espera de clientes de forma indefinida.
     */
    public static void main(String[] args) {
        try {
            /*
             * Creación del servidor
             */
            Servidor serv = new Servidor();

            /*
             * Inicio del servidor
             */
            serv.startServer();

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            System.err.println("Verifique que el puerto no esté en uso");
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }
}