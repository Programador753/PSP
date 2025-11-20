
package practica1;

import java.util.Random;

public class Main3 {

    /**
     * Pre: ---
     * Post: crea 10 hilos distintos, cada uno con un tiempo de espera
     *       aleatorio entre 100 y 300 ms, y que ejecuten un número
     *       aleatorio de veces entre 5 y 15. Cada hilo muestra su
     *       identificador ("Soy 1", "Soy 2", etc.).
     */
    public static void main(String[] args) {
        Random random = new Random();
        /*
         * Creación del array de hilos
         */
        Thread3[] threads = new Thread3[10];
        /*
         * Creación de los 10 hilos con valores aleatorios
         */
        for (int i = 0; i < 10; i++) {
            String mensaje = "Soy " + (i + 1);
            int retardo = 100 + random.nextInt(201); // Entre 100 y 300 ms
            int veces = 5 + random.nextInt(11);      // Entre 5 y 15 veces
            threads[i] = new Thread3(mensaje, retardo, veces);
        }
        /*
         * Inicialización de todos los hilos
         */
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
        /*
         * Esperamos a que terminen todos los hilos
         */
        try {
            for (int i = 0; i < 10; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Fin");
    }
}
