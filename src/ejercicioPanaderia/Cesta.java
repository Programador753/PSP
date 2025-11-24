package ejercicioPanaderia;

import java.util.Stack;
import java.util.concurrent.Semaphore;

/**
 * Clase que representa la cesta de pan compartida.
 * Funciona como una pila (LIFO) protegida por semáforos.
 */
public class Cesta {

    private Stack<String> pilaPan;
    private Semaphore mutex;
    private Semaphore huecos;
    private Semaphore barrasDisponibles;

    /**
     * Constructor de la Cesta.
     * @param capacidad Máximo de barras que caben en la cesta.
     */
    public Cesta(int capacidad) {
        this.pilaPan = new Stack<>();
        this.mutex = new Semaphore(1);
        this.huecos = new Semaphore(capacidad);
        this.barrasDisponibles = new Semaphore(0);
    }

    /**
     * Pre: El tipo de pan no debe ser nulo.
     * Post: Añade una barra a la cesta si hay hueco.
     * Si está llena, el hilo se bloquea.
     */
    public void ponerBarra(String barra) {
        try {
            // 1. Esperar a que haya espacio en la cesta
            huecos.acquire();

            // 2. Acceso exclusivo a la pila
            mutex.acquire();

            // --- Sección Crítica ---
            pilaPan.push(barra);
            System.out.println("PANADERO: Pone " + barra + ". (Cesta: " + pilaPan.size() + ")");
            // -----------------------

            mutex.release();

            // 3. Avisar que hay una barra disponible
            barrasDisponibles.release();

        } catch (InterruptedException e) {
            System.err.println("Error al poner barra.");
            e.printStackTrace();
        }
    }

    /**
     * Pre: ---
     * Post: Saca una barra de la cesta si hay disponibles.
     * Si está vacía, el hilo se bloquea.
     */
    public String cogerBarra() {
        String barra = null;
        try {
            // 1. Esperar a que haya pan
            barrasDisponibles.acquire();

            // 2. Acceso exclusivo
            mutex.acquire();

            // --- Sección Crítica ---
            barra = pilaPan.pop();
            System.out.println("CLIENTE: Compra " + barra);
            // -----------------------

            mutex.release();

            // 3. Liberar un hueco en la cesta
            huecos.release();

        } catch (InterruptedException e) {
            System.err.println("Error al coger barra.");
            e.printStackTrace();
        }
        return barra;
    }
}