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

        // Crear instancia de datos compartidos
        ShareData shareData = new ShareData();

        // Inicializar datos
        shareData.inicializarDatos();

        // Crear e iniciar los threads calculadores
        ThreadCalculador2[] threads = new ThreadCalculador2[ShareData.getNumThreads()];
        for (int i = 0; i < ShareData.getNumThreads(); i++) {
            threads[i] = new ThreadCalculador2(i, shareData);
            threads[i].start();
        }

        // Proceso informador: espera activa hasta que todos terminen
        System.out.println("Proceso informador esperando finalización de threads...");
        while (shareData.getThreadsFinalizados() < ShareData.getNumThreads()) {
            // Espera activa - comprueba continuamente
            Thread.yield(); // Cede el procesador para eficiencia
        }

        // Calcular módulo del vector resultado
        double modulo = shareData.calcularModulo();

        // Mostrar resultado
        System.out.println("Todos los threads han finalizado.");
        System.out.println("Módulo del vector resultado: " + modulo);
        System.out.println("Thread más lento: Thread " + shareData.getIdMasLento() +
                " con tiempo: " + (shareData.getTMax() / 1_000_000.0) + " ms");
    }
}