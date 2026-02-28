package Blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Semaphore;

/**
 * Clase que representa la cadena de bloques del nodo.
 * Protegida con semáforos para acceso concurrente seguro desde múltiples hilos.
 * Implementa Serializable para poder ser enviada por la red entre nodos.
 */
public class Blockchain implements Serializable {

    /**
     * Dificultad de minado: número de ceros iniciales requeridos en el hash.
     * ╔═══════════════════════════════════════════════════════════════╗
     * ║  CAMBIAR ESTE VALOR PARA AJUSTAR LA DIFICULTAD DE MINADO    ║
     * ║  Ejemplos:                                                   ║
     * ║    3 = muy fácil  (milisegundos)                             ║
     * ║    5 = moderado   (segundos)                                 ║
     * ║    6 = difícil    (decenas de segundos)                      ║
     * ║    7 = muy difícil (minutos)                                 ║
     * ╚═══════════════════════════════════════════════════════════════╝
     */
    public static final int DIFICULTAD = 5;

    // Prefijo de ceros que debe tener el hash para ser válido (generado desde DIFICULTAD)
    public static final String PREFIJO_DIFICULTAD = "0".repeat(DIFICULTAD);

    /**
     * Hash del bloque génesis de la red.
     * Se calcula automáticamente de forma determinista para que todos los nodos
     * obtengan el mismo valor sin necesidad de hardcodeo manual.
     */
    public static final String GENESIS_HASH = GenesisBlock.obtenerHashGenesis();

    // La cadena en sí: una lista de bloques enlazados por sus hashes
    private ArrayList<Block> cadena;

    // Semáforo binario (mutex) para proteger el acceso concurrente a la cadena
    // Es transient porque los semáforos no son serializables
    private transient Semaphore semaforo;

    /**
     * Constructor de la clase Blockchain que inicializa la cadena vacía y el semáforo.
     * Pre: Ninguna.
     * Post: Se crea un objeto Blockchain con una lista vacía y un semáforo binario (1 permiso).
     */
    public Blockchain() {
        this.cadena = new ArrayList<>();
        this.semaforo = new Semaphore(1);
    }

    /**
     * Método que asegura que el semáforo exista, necesario tras deserialización
     * ya que el semáforo es transient y no se serializa.
     * Pre: Ninguna.
     * Post: Retorna el semáforo existente, o crea uno nuevo si era null tras deserialización.
     */
    private Semaphore getSemaforo() {
        if (semaforo == null) {
            semaforo = new Semaphore(1);
        }
        return semaforo;
    }

    /**
     * Método que inicializa la cadena creando y añadiendo el bloque génesis.
     * Pre: La cadena debe estar vacía (recién construida).
     * Post: La cadena contiene exactamente un bloque: el génesis, con su hash calculado y hashPrevio null.
     */
    public void inicializar() {
        GenesisBlock genesis = new GenesisBlock();
        genesis.setHash(GENESIS_HASH);
        cadena.add(genesis);

        System.out.println(" Bloque génesis inicializado: " + GENESIS_HASH.substring(0, 20) + "...");
    }

    /**
     * Método que añade un bloque a la cadena de forma atómica: verifica que el hashPrevio
     * del bloque coincida con el hash del último bloque DENTRO del semáforo, y solo entonces
     * lo añade. Esto previene condiciones de carrera cuando dos mineros encuentran un bloque
     * válido al mismo tiempo (solo el primero en adquirir el semáforo gana).
     * Pre: El bloque debe tener hash, hashPrevio, transacciones y nonce correctamente establecidos.
     * Post: Si el hashPrevio del bloque coincide con el hash del último bloque de la cadena,
     *       se añade y retorna true. Si no coincide (otro bloque fue aceptado antes), retorna false.
     */
    public boolean agregarBloque(Block bloque) {
        try {
            getSemaforo().acquire();
            try {
                // Verificación atómica: el hashPrevio debe coincidir con el último bloque actual
                Block ultimo = cadena.get(cadena.size() - 1);
                if (!ultimo.getHash().equals(bloque.getHashPrevio())) {
                    return false;
                }
                cadena.add(bloque);
                return true;
            } finally {
                getSemaforo().release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Método que devuelve el último bloque de la cadena de forma thread-safe.
     * Pre: La cadena debe contener al menos el bloque génesis.
     * Post: Retorna el último bloque de la cadena, o null si el hilo fue interrumpido.
     */
    public Block getUltimoBloque() {
        try {
            getSemaforo().acquire();
            try {
                return cadena.get(cadena.size() - 1);
            } finally {
                getSemaforo().release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Método que devuelve el número de bloques en la cadena de forma thread-safe.
     * Pre: La cadena debe estar inicializada.
     * Post: Retorna un entero con la cantidad total de bloques (incluyendo el génesis).
     */
    public int getSize() {
        try {
            getSemaforo().acquire();
            try {
                return cadena.size();
            } finally {
                getSemaforo().release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    /**
     * Método que devuelve una copia de la cadena para sincronización con otros nodos.
     * Pre: La cadena debe estar inicializada.
     * Post: Retorna un nuevo ArrayList con todos los bloques de la cadena (copia superficial).
     */
    public ArrayList<Block> getCadena() {
        try {
            getSemaforo().acquire();
            try {
                return new ArrayList<>(cadena);
            } finally {
                getSemaforo().release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    /**
     * Método que reemplaza la cadena local por una cadena más larga recibida de otro nodo.
     * Solo se reemplaza si la cadena nueva es estrictamente más larga y válida (regla de la cadena más larga).
     * Pre: La nueva cadena debe ser una lista de bloques no vacía.
     * Post: Si la nueva cadena es más larga y válida, reemplaza la local y retorna true.
     *       Si no, la cadena local permanece sin cambios y retorna false.
     */
    public boolean reemplazarCadena(ArrayList<Block> nuevaCadena) {
        try {
            getSemaforo().acquire();
            try {
                if (nuevaCadena.size() > cadena.size() && validarCadena(nuevaCadena)) {
                    cadena = nuevaCadena;
                    System.out.println("[Blockchain] Cadena reemplazada con una más larga (" + cadena.size() + " bloques)");
                    return true;
                }
                return false;
            } finally {
                getSemaforo().release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Método que devuelve todas las transacciones confirmadas en la cadena (de todos los bloques).
     * Útil para limpiar el mempool tras sincronizar con una cadena más larga.
     * Pre: La cadena debe estar inicializada.
     * Post: Retorna un ArrayList con todas las transacciones de todos los bloques de la cadena.
     */
    public ArrayList<Transaction> getTransaccionesConfirmadas() {
        ArrayList<Transaction> confirmadas = new ArrayList<>();
        try {
            getSemaforo().acquire();
            try {
                for (Block bloque : cadena) {
                    if (bloque.getTransacciones() != null) {
                        confirmadas.addAll(bloque.getTransacciones());
                    }
                }
            } finally {
                getSemaforo().release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return confirmadas;
    }

    /**
     * Método privado que valida la integridad de una cadena de bloques cualquiera.
     * Comprueba que cada bloque enlaza correctamente con el anterior mediante hashPrevio,
     * que cumple la dificultad y que su hash recalculado coincide con el almacenado.
     * Pre: La cadena a validar debe ser una lista no vacía de bloques.
     * Post: Retorna true si toda la cadena es íntegra y válida, false si hay alguna inconsistencia.
     */
    private boolean validarCadena(ArrayList<Block> cadenaAValidar) {
        if (cadenaAValidar.isEmpty()) return false;
        String hashPrevio = cadenaAValidar.get(0).getHash();
        for (int i = 1; i < cadenaAValidar.size(); i++) {
            Block bloque = cadenaAValidar.get(i);
            // Comprobar que el hashPrevio enlaza con el bloque anterior
            if (!Objects.equals(hashPrevio, bloque.getHashPrevio())
                    || !bloque.getHash().startsWith(PREFIJO_DIFICULTAD)) {
                return false;
            }
            // Verificar que el hash recalculado coincide con el almacenado
            String hashRecalculado = HashUtil.calcularHash(bloque.toString());
            if (!hashRecalculado.equals(bloque.getHash())) {
                return false;
            }
            hashPrevio = bloque.getHash();
        }
        return true;
    }

    /**
     * Método que comprueba si la cadena local es válida.
     * Pre: La cadena debe estar inicializada con al menos el bloque génesis.
     * Post: Retorna true si la cadena es íntegra y todos los bloques enlazan correctamente, false en caso contrario.
     */
    public boolean esValida() {
        try {
            getSemaforo().acquire();
            try {
                return validarCadena(cadena);
            } finally {
                getSemaforo().release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Método que imprime por consola todos los bloques de la cadena con su información detallada.
     * Pre: La cadena debe estar inicializada.
     * Post: Se imprime por consola cada bloque con su hash, hashPrevio, nonce y transacciones.
     */
    public void imprimirCadena() {
        try {
            getSemaforo().acquire();
            try {
                for (int i = 0; i < cadena.size(); i++) {
                    Block bloque = cadena.get(i);
                    System.out.println("--- Bloque " + i + " ---");
                    System.out.println("Hash: " + bloque.getHash());
                    System.out.println("Hash Previo: " + bloque.getHashPrevio());
                    System.out.println("Nonce: " + bloque.getNonce());
                    System.out.println("Transacciones: " + bloque.getTransacciones().size());
                    for (Transaction tx : bloque.getTransacciones()) {
                        System.out.println("  -> TX " + tx.getId() + " | Monto: " + tx.getMonto());
                    }
                    System.out.println("----------");
                }
            } finally {
                getSemaforo().release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
