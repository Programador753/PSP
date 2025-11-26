package ejercicioBaseDatos;

/**
 * Clase principal que lanza la simulación de la base de datos.
 */
public class MainBaseDatos {

    /**
     * Pre: ---
     * Post: Inicializa la BD, lanza lectores y escritores,
     * y espera a que terminen sus tareas.
     */
    public static void main(String[] args) {
        // 1. Crear Recurso Compartido
        BaseDatos baseDatos = new BaseDatos();

        // 2. Configuración de hilos
        int numLectores = 5;
        int numEscritores = 2;

        Lector[] lectores = new Lector[numLectores];
        Escritor[] escritores = new Escritor[numEscritores];

        // 3. Inicializar y Arrancar

        // Lanzamos Lectores
        for (int i = 0; i < numLectores; i++) {
            lectores[i] = new Lector("Lec_" + (i + 1), baseDatos);
            lectores[i].start();
        }

        // Lanzamos Escritores
        for (int i = 0; i < numEscritores; i++) {
            escritores[i] = new Escritor("Admin_" + (i + 1), baseDatos);
            escritores[i].start();
        }

        // 4. Esperar (Join)
        try {
            for (int i = 0; i < numLectores; i++) {
                lectores[i].join();
            }
            for (int i = 0; i < numEscritores; i++) {
                escritores[i].join();
            }
        } catch (InterruptedException e) {
            System.err.println("Error: Simulación interrumpida.");
            e.printStackTrace();
        }

        System.out.println("--- SISTEMA APAGADO: Todos los usuarios desconectados ---");
    }
}