package ejercicioPanaderia;

/**
 * Clase principal que gestiona la panadería.
 */
public class MainPanaderia {

    /**
     * Pre: ---
     * Post: Inicializa la cesta, los panaderos y los clientes,
     * gestionando su ejecución concurrente hasta finalizar el stock.
     */
    public static void main(String[] args) {
        // 1. Crear el Recurso Compartido (Cesta para 8 barras)
        Cesta cesta = new Cesta(8);

        // 2. Configuración de producción y consumo
        int numPanaderos = 2;
        int barrasPorPanadero = 10; // Total producción: 20

        int numClientes = 5;
        // Para que cuadre exacto: 20 barras / 5 clientes = 4 barras/cliente
        int barrasPorCliente = 4;

        // 3. Crear Arrays de Hilos
        Panadero[] plantilla = new Panadero[numPanaderos];
        Cliente[] colaClientes = new Cliente[numClientes];

        // 4. Inicializar y Arrancar Hilos

        // Arrancamos Panaderos
        for (int i = 0; i < numPanaderos; i++) {
            plantilla[i] = new Panadero("Panadero_" + (i + 1), cesta, barrasPorPanadero);
            plantilla[i].start();
        }

        // Arrancamos Clientes
        for (int i = 0; i < numClientes; i++) {
            colaClientes[i] = new Cliente("Cliente_" + (i + 1), cesta, barrasPorCliente);
            colaClientes[i].start();
        }

        // 5. Esperar a que terminen (Join)
        try {
            // Esperamos a los productores
            for (int i = 0; i < numPanaderos; i++) {
                plantilla[i].join();
            }

            // Esperamos a los consumidores
            for (int i = 0; i < numClientes; i++) {
                colaClientes[i].join();
            }

        } catch (InterruptedException e) {
            System.err.println("Error: Hilo principal interrumpido.");
            e.printStackTrace();
        }

        System.out.println("--- FIN DEL DÍA: Todo el pan vendido y comido ---");
    }
}