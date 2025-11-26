package ejercicioBaseDatos;

import java.util.concurrent.Semaphore;

/**
 * Clase que gestiona el acceso concurrente a la Base de Datos.
 * Implementa el patrón Lectores-Escritores con prioridad a lectores.
 */
public class BaseDatos {

    private int lectoresActivos;
    private Semaphore mutex; // Protege la variable lectoresActivos
    private Semaphore dbLock; // Controla el acceso exclusivo a la BD

    /**
     * Constructor de la Base de Datos.
     * Inicializa el contador de lectores y los semáforos.
     */
    public BaseDatos() {
        this.lectoresActivos = 0;
        this.mutex = new Semaphore(1);
        this.dbLock = new Semaphore(1);
    }

    /**
     * Pre: El identificador del lector no debe ser nulo.
     * Post: El lector registra su entrada. Si es el primero, bloquea
     * el acceso a los escritores.
     */
    public void entrarLector(String id) {
        try {
            // 1. Acceso exclusivo al contador
            mutex.acquire();

            lectoresActivos++;
            if (lectoresActivos == 1) {
                // El primer lector cierra la puerta a los escritores
                dbLock.acquire();
            }

            mutex.release();

            System.out.println("LECTOR (" + id + ") entra. Leyendo... (Total dentro: " + lectoresActivos + ")");

        } catch (InterruptedException e) {
            System.err.println("Error: Lector interrumpido al entrar.");
            e.printStackTrace();
        }
    }

    /**
     * Pre: El identificador del lector no debe ser nulo.
     * Post: El lector registra su salida. Si es el último, desbloquea
     * el acceso para los escritores.
     */
    public void salirLector(String id) {
        try {
            mutex.acquire();

            lectoresActivos--;
            System.out.println("LECTOR (" + id + ") sale. (Quedan: " + lectoresActivos + ")");

            if (lectoresActivos == 0) {
                // El último lector abre la puerta a los escritores
                dbLock.release();
            }

            mutex.release();

        } catch (InterruptedException e) {
            System.err.println("Error: Lector interrumpido al salir.");
            e.printStackTrace();
        }
    }

    /**
     * Pre: El identificador del escritor no debe ser nulo.
     * Post: Solicita acceso exclusivo a la base de datos.
     * Espera si hay lectores u otro escritor dentro.
     */
    public void entrarEscritor(String id) {
        try {
            // El escritor necesita el permiso global
            dbLock.acquire();
            System.out.println(">>> ESCRITOR (" + id + ") entra. ESCRIBIENDO DATOS...");
        } catch (InterruptedException e) {
            System.err.println("Error: Escritor interrumpido al entrar.");
            e.printStackTrace();
        }
    }

    /**
     * Pre: El identificador del escritor no debe ser nulo.
     * Post: Libera el acceso exclusivo a la base de datos.
     */
    public void salirEscritor(String id) {
        System.out.println("<<< ESCRITOR (" + id + ") sale. Fin escritura.");
        dbLock.release();
    }
}