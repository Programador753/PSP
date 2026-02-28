
package Blockchain;

import java.util.ArrayList;

/**
 * Clase que representa el primer bloque de la cadena de bloques sin referencias previas.
 * El bloque génesis es idéntico para todos los nodos de la red, garantizando consenso inicial.
 */
public class GenesisBlock extends Block {

    // Timestamp fijo del génesis (01/01/2026 00:00:00 UTC)
    private static final long GENESIS_TIMESTAMP = 1735689600000L;

    // Mensaje fundacional de la blockchain
    private static final String GENESIS_MESSAGE = "Blockchain P2P Network - Genesis Block - Antonio Hernandez Cavero - 2026";

    /**
     * Constructor de la clase GenesisBlock que crea el bloque inicial de la red.
     * Pre: La clase Block de la que hereda debe estar correctamente implementada.
     * Post: Se crea un objeto GenesisBlock determinista con hash previo nulo,
     *       timestamp fijo y lista de transacciones vacía.
     */
    public GenesisBlock() {
        super(null, new ArrayList<Transaction>());
        // Sobrescribir el timestamp para que sea fijo
        this.setTimestamp(GENESIS_TIMESTAMP);
        this.setNonce(0);
    }

    /**
     * Representación en cadena del bloque génesis para cálculo de hash.
     * Usa un formato determinista que siempre produce el mismo resultado.
     */
    @Override
    public String toString() {
        return GENESIS_MESSAGE + "|" + GENESIS_TIMESTAMP + "|0";
    }

    /**
     * Método estático que devuelve el hash del génesis calculado deterministicamente.
     * Este método se llama una sola vez por clase y el resultado se cachea.
     * @return El hash SHA-256 del bloque génesis.
     */
    public static String obtenerHashGenesis() {
        GenesisBlock temporal = new GenesisBlock();
        return HashUtil.calcularHash(temporal.toString());
    }
}