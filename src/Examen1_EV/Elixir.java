package Examen1_EV;

import java.util.concurrent.Semaphore;

/**
 * Clase que representa hilo de Elixir en el juego, esta irá incrementando la cantidad cada 2 segundos,
 * hasta un maximo de 10 unidades de Elixir.
 *
 * @author Antonio
 * @version 1.0
 */
public class Elixir extends Thread {
    private int cantidad; // Cantidad de Elixir que tiene el hilo
    private static final int MAX_ELIXIR = 10; // Maximo de Elixir que puede tener el hilo
    private Semaphore semaforo; // Semáforo para controlar la sección crítica
    private boolean juegoActivo;

    /**
     * Constructor de la clase Elixir.
     *
     */
    public Elixir() {
        cantidad = 0;
        semaforo = new Semaphore(1);
        juegoActivo = true;
    }

    /**
     * Getter de la cantidad de Elixir que tiene el hilo.
     *
     * @return cantidad actual de elixir.
     */
    public int getCantidad() {
        try {
            semaforo.acquire();
            int valor = cantidad;
            semaforo.release();
            return valor;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Verifica si hay suficiente elixir y lo decrementa en una operación atómica.
     *
     * @param coste Coste de la carta.
     * @return true si había suficiente elixir y se decrementó, false en caso contrario.
     */
    public boolean consumir(int coste) {
        try {
            semaforo.acquire();
            if (cantidad >= coste) {
                cantidad -= coste;
                semaforo.release();
                return true;
            }
            semaforo.release();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Detiene el hilo de generación de elixir.
     */
    public void detener() {
        juegoActivo = false;
        this.interrupt();
    }

    /**
     * Metodo que incrementa la cantidad de Elixir cada 2 segundos hasta un maximo de 10 unidades.
     */
    @Override
    public void run() {
        while (juegoActivo) {
            try {
                Thread.sleep(2000);

                semaforo.acquire();
                if (cantidad < MAX_ELIXIR) {
                    cantidad++;
                }
                semaforo.release();

            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
