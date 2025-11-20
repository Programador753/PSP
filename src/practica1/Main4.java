package practica1;

import java.util.Random;

public class Main4 {

    /**
     * Pre: ---
     * Post: Crea un vector de 100 números aleatorios y lanza tres hilos
     * para calcular concurrentemente la media, máximo/mínimo y
     * desviación típica. Muestra los resultados por pantalla.
     */
    public static void main(String[] args) {
        // Crear y rellenar el vector con números aleatorios
        int[] vector = new int[100];
        Random random = new Random();

        System.out.println("Generando vector de 100 números aleatorios...");
        for (int i = 0; i < vector.length; i++) {
            vector[i] = random.nextInt(1000); // Números entre 0 y 999
        }
        System.out.println("Vector generado correctamente.\n");

        // Crear los hilos para los cálculos
        ThreadMedia threadMedia = new ThreadMedia(vector);
        ThreadMaxMin threadMaxMin = new ThreadMaxMin(vector);

        // Iniciar los primeros dos hilos (media y max/min pueden ejecutarse en paralelo)
        System.out.println("Iniciando cálculos concurrentes...");
        threadMedia.start();
        threadMaxMin.start();

        try {
            // Esperar a que termine el cálculo de la media
            threadMedia.join();

            // Una vez que tenemos la media, podemos calcular la desviación típica
            ThreadDesviacion threadDesviacion = new ThreadDesviacion(vector, threadMedia.getMedia());
            threadDesviacion.start();

            // Esperar a que terminen todos los cálculos
            threadMaxMin.join();
            threadDesviacion.join();

            // Mostrar los resultados
            System.out.println("\n=== RESULTADOS ESTADÍSTICOS ===");
            System.out.printf("Media aritmética: %.2f%n", threadMedia.getMedia());
            System.out.printf("Valor máximo: %d%n", threadMaxMin.getMaximo());
            System.out.printf("Valor mínimo: %d%n", threadMaxMin.getMinimo());
            System.out.printf("Desviación típica: %.2f%n", threadDesviacion.getDesviacion());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nTodos los cálculos completados. Fin del programa.");
    }
}