package Blockchain;

import java.util.ArrayList;
/**
 * Clase que representa el primer bloque de la cadena de bloques sin referencias previas.
 */
public class GenesisBlock extends Block {
    /**
     * Constructor de la clase GenesisBlock que crea el bloque inicial de la red.
     * Pre: La clase Block de la que hereda debe estar correctamente implementada.
     * Post: Se crea un objeto GenesisBlock con hash previo nulo y una lista de transacciones completamente vacía.
     */
    public GenesisBlock() {
        super(null, new ArrayList<Transaction>());
    }
}