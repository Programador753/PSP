package ServidordeCorreos;

/**
 * Clase principal del sistema de correos.
 */
public class MainCorreo {

    /**
     * Pre: ---
     * Post: Inicia la bandeja, usuarios y despachador.
     */
    public static void main(String[] args) {
        // 1. Recurso compartido (Capacidad 10)
        BandejaSalida bandeja = new BandejaSalida(10);

        // 2. Hilo Consumidor
        Despachador server = new Despachador(bandeja);
        server.start();

        // 3. Hilos Productores (3 Usuarios)
        Usuario[] usuarios = new Usuario[3];
        for (int i = 0; i < 3; i++) {
            usuarios[i] = new Usuario("User" + (i + 1), bandeja);
            usuarios[i].start(); // Inicia el hilo de usuario
        }

        // 4. Espera a los productores
        try {
            for (int i = 0; i < 3; i++) {
                usuarios[i].join();
            }
            System.out.println("--- Todos los usuarios han enviado sus emails ---");
            // Nota: El despachador seguiría corriendo en segundo plano
            // porque tiene un while(true).
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}