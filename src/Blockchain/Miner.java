package Blockchain;

import java.util.ArrayList;
import java.util.Random;

/**
 * Clase encargada del proceso de minado: busca un nonce que produzca
 * un hash válido (que cumpla la dificultad definida en Blockchain.DIFICULTAD) para un bloque con transacciones del mempool.
 */
public class Miner {

    // La dificultad se define en Blockchain.PREFIJO_DIFICULTAD (centralizada)
    private Random random = new Random();

    /**
     * Mina un nuevo bloque extrayendo transacciones del mempool.
     * @param mempool El mempool del nodo (protegido por semáforo internamente)
     * @param hashPrevio El hash del último bloque de la cadena
     * @return El bloque minado con hash válido, o null si no hay transacciones
     */
    public Block minar(Mempool mempool, String hashPrevio) {

        // 1. Verifica que hay transacciones pendientes
        if (mempool.estaVacio()) {
            System.out.println("No hay transacciones en el mempool para minar.");
            return null;
        }

        // 2. Coge las 3 primeras transacciones pendientes del Mempool (thread-safe)
        ArrayList<Transaction> txs = new ArrayList<>(mempool.obtenerPrimeras(3));

        if (txs.isEmpty()) {
            System.out.println("No se pudieron obtener transacciones del mempool.");
            return null;
        }

        // 3. Crea el bloque con esas transacciones y el hashPrevio
        Block bloque = new Block(hashPrevio, txs);

        System.out.println("Minando bloque con " + txs.size() + " transacciones...");

        int intentos = 0;
        while (true) {
            // 4. Genera un nonce aleatorio y se lo asigna al bloque
            int nonce = random.nextInt(Integer.MAX_VALUE);
            bloque.setNonce(nonce);

            // 5. Calcula el hash del bloque con ese nonce
            String hash = HashUtil.calcularHash(bloque.toString());
            intentos++;

            // 6. Si cumple la dificultad, bloque válido
            if (hash.startsWith(Blockchain.PREFIJO_DIFICULTAD)) {
                bloque.setHash(hash);
                System.out.println("¡Bloque minado en " + intentos + " intentos!");
                System.out.println("Hash: " + hash);
                System.out.println("Nonce: " + nonce);
                return bloque;
            }

            // Feedback cada 1 millón de intentos
            if (intentos % 1000000 == 0) {
                System.out.println("  ... " + intentos + " intentos realizados");
            }
        }
    }
}