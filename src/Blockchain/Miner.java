package Blockchain;

import java.util.ArrayList;
import java.util.Random;

/**
 * Clase encargada del proceso de minado de bloques en la blockchain.
 * Busca un nonce que produzca un hash válido que cumpla con la dificultad
 * definida en Blockchain.DIFICULTAD. Puede ser detenido externamente cuando
 * otro nodo de la red mina un bloque válido primero, gracias al flag volátil.
 */
public class Miner {

    // Generador de números aleatorios para los nonces
    private Random random = new Random();

    // Flag volátil que permite detener el minado desde otro hilo de forma segura
    private volatile boolean minando = true;

    /**
     * Método que detiene el proceso de minado de forma segura desde otro hilo.
     * Pre: El objeto Miner debe estar instanciado y el proceso de minado debe estar en curso.
     * Post: El flag minando se establece en false, provocando que el bucle de minado
     *       termine en la siguiente iteración.
     */
    public void detener() {
        minando = false;
    }

    /**
     * Método que indica si el minero está actualmente en proceso de minado.
     * Pre: El objeto Miner debe estar instanciado.
     * Post: Retorna true si el minero sigue minando, false si fue detenido o finalizó.
     */
    public boolean isMinando() {
        return minando;
    }

    /**
     * Método que mina un nuevo bloque extrayendo transacciones del mempool.
     * Itera con nonces aleatorios hasta encontrar un hash que cumpla la dificultad
     * o hasta que el flag minando sea puesto a false por otro hilo.
     * Pre: El mempool debe estar inicializado y contener al menos una transacción.
     *      El hashPrevio debe ser el hash del último bloque válido de la cadena.
     * Post: Retorna un bloque con hash válido si se encuentra un nonce adecuado,
     *       o null si el mempool está vacío o el minado fue interrumpido externamente.
     * @param mempool El mempool del nodo, protegido por semáforo internamente.
     * @param hashPrevio El hash del último bloque de la cadena.
     * @return El bloque minado con hash válido, o null si no se pudo minar.
     */
    public Block minar(Mempool mempool, String hashPrevio) {

        // 1. Verifica que hay transacciones pendientes
        if (mempool.estaVacio()) {
            System.out.println("No hay transacciones en el mempool para minar.");
            return null;
        }

        // 2. Coge las 3 primeras transacciones pendientes del mempool (thread-safe)
        ArrayList<Transaction> txs = new ArrayList<>(mempool.obtenerPrimeras(3));

        if (txs.isEmpty()) {
            System.out.println("No se pudieron obtener transacciones del mempool.");
            return null;
        }

        // 3. Crea el bloque candidato con esas transacciones y el hash del bloque previo
        Block bloque = new Block(hashPrevio, txs);

        System.out.println("Minando bloque con " + txs.size() + " transacciones...");

        int intentos = 0;

        // 4. Bucle de prueba y error: se detiene cuando se encuentra un hash válido
        //    o cuando otro nodo mina el bloque primero (minando = false)
        while (minando) {
            // Genera un nonce aleatorio y se lo asigna al bloque
            int nonce = random.nextInt(Integer.MAX_VALUE);
            bloque.setNonce(nonce);

            // Calcula el hash del bloque con ese nonce
            String hash = HashUtil.calcularHash(bloque.toString());
            intentos++;

            // Si cumple la dificultad, el bloque es válido
            if (hash.startsWith(Blockchain.PREFIJO_DIFICULTAD)) {
                bloque.setHash(hash);
                System.out.println("¡Bloque minado en " + intentos + " intentos!");
                System.out.println("Hash: " + hash);
                System.out.println("Nonce: " + nonce);
                return bloque;
            }

            // Feedback cada 1 millón de intentos para informar del progreso
            if (intentos % 1000000 == 0) {
                System.out.println("  ... " + intentos + " intentos realizados");
            }
        }

        // Si se sale del bucle sin encontrar hash, es porque otro nodo minó primero
        System.out.println("Minado detenido: otro nodo minó el bloque primero.");
        return null;
    }
}
