package practica2;

public class Main2 {
    /**
     * Pre: ---
     * Post: Ejecuta el cálculo del producto matriz-vector usando 16 threads
     *       e identifica el thread más lento usando exclusión mutua con semáforo
     */
    public static void main(String[] args) {
        System.out.println("Iniciando cálculo del producto matriz-vector con " + ShareData.getNumThreads() + " threads");
        System.out.println("Identificando el thread más lento...");

        // Inicializar datos
        ShareData.inicializarDatos();

        // Crear e iniciar los threads calculadores
        ThreadCalculador2[] threads = new ThreadCalculador2[ShareData.getNumThreads()];
        for (int i = 0; i < ShareData.getNumThreads(); i++) {
            threads[i] = new ThreadCalculador2(i);
            threads[i].start();
        }

        // Proceso informador: espera activa hasta que todos terminen
        System.out.println("Proceso informador esperando finalización de threads...");
        while (ShareData.getThreadsFinalizados() < ShareData.getNumThreads()) {
            // Espera activa - comprueba continuamente
            Thread.yield(); // Cede el procesador para eficiencia
        }

        // Calcular módulo del vector resultado
        double modulo = ShareData.calcularModulo();

        // Mostrar resultado
        System.out.println("Todos los threads han finalizado.");
        System.out.println("Módulo del vector resultado: " + modulo);
        System.out.println("Thread más lento: Thread " + ShareData.getIdMasLento() +
                " con tiempo: " + (ShareData.getTMax() / 1_000_000.0) + " ms");
    }

    /**
     * Pre: ---
     * Post: Incrementa de forma sincronizada el contador de threads finalizados
     */
    public static synchronized void incrementarThreadsFinalizados() {
        ShareData.incrementarThreadsFinalizados();
    }

    /**
     * Pre: tiempoEjecucion >= 0, idThread >= 0
     * Post: Actualiza tMax e idMasLento si este thread ha sido más lento
     *       usando exclusión mutua con semáforo
     */
    public static void actualizarThreadMasLento(long tiempoEjecucion, int idThread) {
        ShareData.actualizarThreadMasLento(tiempoEjecucion, idThread);
    }

    // Getters para constantes (delegando a ShareData)
    public static int getN() {
        return ShareData.getN();
    }

    public static int getFilasPorThread() {
        return ShareData.getFilasPorThread();
    }
}