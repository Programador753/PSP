package ReservaVuelos;

import java.io.IOException;

/**
 * Clase principal que inicia el cliente de reservas.
 * Permite especificar el nombre del cliente por línea de comandos.
 *
 */
public class MainCliente {
    /**
     * Pre: ninguna
     * Post: Se crea e inicia un cliente con el nombre especificado.
     *       Si no se proporciona nombre, se usa "Cliente 1" por defecto.
     *
     * @param args args[0] = nombre del cliente (opcional)
     */
    public static void main(String[] args) {
        String nombreCliente;

        /*
         * Obtener nombre del cliente de los argumentos o usar valor por defecto
         */
        if (args.length > 0) {
            nombreCliente = args[0];
        } else {
            nombreCliente = "Cliente 1";
            System.out.println("Uso: java ReservaVuelos.MainCliente <nombreCliente>");
            System.out.println("Usando nombre por defecto: " + nombreCliente + "\n");
        }

        try {
            /*
             * Creación e inicio del cliente
             */
            Cliente cli = new Cliente(nombreCliente);
            cli.startClient();

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            System.err.println("Verifique que el servidor esté en ejecución");
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }
}

