package ServidordeCorreos;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Clase que gestiona la cola de mensajes salientes.
 * Implementa un buffer limitado con semáforos.
 */
public class BandejaSalida {

    private Queue<String> cola;
    private Semaphore mutex;
    private Semaphore huecos;
    private Semaphore items;

    /**
     * Constructor de la Bandeja de Salida.
     * @param capacidad Máximo número de emails que caben en la cola.
     */
    public BandejaSalida(int capacidad) {
        this.cola = new LinkedList<>();
        this.mutex = new Semaphore(1);
        this.huecos = new Semaphore(capacidad);
        this.items = new Semaphore(0);
    }

    /**
     * Pre: El mensaje no debe ser nulo.
     * Post: Añade el mensaje a la cola si hay hueco.
     */
    public void depositarMensaje(String mensaje) {
        try {
            huecos.acquire();
            mutex.acquire();

            // Sección Crítica
            cola.offer(mensaje);
            System.out.println("--> ENTRADA: " + mensaje + ". (Pendientes: " + cola.size() + ")");

            mutex.release();
            items.release();
        } catch (InterruptedException e) {
            System.err.println("Error al depositar mensaje.");
            e.printStackTrace();
        }
    }

    /**
     * Pre: ---
     * Post: Extrae el primer mensaje de la cola para enviarlo.
     */
    public String extraerMensaje() {
        String mensaje = null;
        try {
            items.acquire();
            mutex.acquire();

            // Sección Crítica
            mensaje = cola.poll();
            System.out.println("<-- SALIDA: Enviando " + mensaje);

            mutex.release();
            huecos.release();
        } catch (InterruptedException e) {
            System.err.println("Error al extraer mensaje.");
            e.printStackTrace();
        }
        return mensaje;
    }
}