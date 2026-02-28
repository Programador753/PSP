package Blockchain;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Clase que agrupa un conjunto de transacciones validadas para formar un bloque en la cadena.
 */
public class Block implements Serializable {
    private String hash;
    private String hashPrevio;
    private long timestamp;
    private int nonce;
    private ArrayList<Transaction> transacciones;
    /**
     * Constructor de la clase Block que inicializa el bloque con un hash previo y una lista de transacciones.
     * Pre: El parámetro transacciones debe ser una lista inicializada y válida.
     * Post: Se crea un objeto Block con el tiempo actual, un nonce inicializado a cero y los datos proporcionados.
     */
    public Block(String hashPrevio, ArrayList<Transaction> transacciones) {
        this.hashPrevio = hashPrevio;
        this.transacciones = transacciones;
        this.timestamp = System.currentTimeMillis();
        this.nonce = 0;
    }
    /**
     * Método que devuelve el hash actual del bloque.
     * Pre: El objeto Block debe estar instanciado y el hash calculado previamente.
     * Post: Retorna una cadena de texto con el identificador único criptográfico del bloque.
     */
    public String getHash() {
        return hash;
    }
    /**
     * Método que devuelve el hash del bloque anterior en la cadena.
     * Pre: El objeto Block debe estar instanciado.
     * Post: Retorna una cadena de texto con el enlace criptográfico al bloque previo.
     */
    public String getHashPrevio() {
        return hashPrevio;
    }
    /**
     * Método que devuelve la marca de tiempo en la que se creó el bloque.
     * Pre: El objeto Block debe estar instanciado.
     * Post: Retorna un valor numérico largo que representa el momento exacto de creación.
     */
    public long getTimestamp() {
        return timestamp;
    }
    /**
     * Método que devuelve el número arbitrario utilizado para el proceso de minado.
     * Pre: El objeto Block debe estar instanciado.
     * Post: Retorna un número entero que representa el nonce actual del bloque.
     */
    public int getNonce() {
        return nonce;
    }
    /**
     * Método que devuelve la lista de transacciones contenidas en el bloque.
     * Pre: El objeto Block debe estar instanciado.
     * Post: Retorna una estructura de datos de tipo ArrayList con las transacciones del bloque.
     */
    public ArrayList<Transaction> getTransacciones() {
        return transacciones;
    }
    /**
     * Método que establece un nuevo hash para el bloque tras ser minado.
     * Pre: El parámetro hash no debe ser nulo y debe cumplir la dificultad requerida por la red.
     * Post: El atributo hash del bloque queda actualizado con el nuevo valor proporcionado por el minero.
     */
    public void setHash(String hash) {
        this.hash = hash;
    }
    /**
     * Método que establece un nuevo valor para el nonce durante el intento de minado.
     * Pre: El parámetro nonce debe ser un número entero válido.
     * Post: El atributo nonce del bloque se actualiza con el número proporcionado.
     */
    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    /**
     * Método que establece el timestamp del bloque.
     * Usado internamente para crear un bloque génesis determinista.
     * Pre: El parámetro timestamp debe ser un valor válido en milisegundos.
     * Post: El atributo timestamp del bloque se actualiza con el valor proporcionado.
     */
    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Método que concatena los campos del bloque en un orden específico para calcular su hash.
     * Pre: Los atributos del bloque deben estar debidamente inicializados.
     * Post: Retorna una cadena de texto exacta con el hash previo, timestamp, nonce y datos de transacciones
     * concatenados.
     */
    @Override
    public String toString() {
        StringBuilder txString = new StringBuilder();
        if (transacciones != null) {
            for (Transaction tx : transacciones) {
                txString.append(tx.toString());
            }
        }
        return hashPrevio + timestamp + nonce + txString.toString();
    }
}