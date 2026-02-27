package Blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Semaphore;

/**
 * La cadena de bloques. Protegida con semáforos para acceso concurrente seguro.
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

    // La cadena en sí: una lista de bloques enlazados
    private ArrayList<Block> cadena;

    // Semáforo binario (mutex) para proteger el acceso concurrente a la cadena
    // transient porque los semáforos no son serializables
    private transient Semaphore semaforo;

    public Blockchain() {
        this.cadena = new ArrayList<>();
        this.semaforo = new Semaphore(1);
    }

    /**
     * Asegura que el semáforo exista (necesario tras deserialización).
     */
    private Semaphore getSemaforo() {
        if (semaforo == null) {
            semaforo = new Semaphore(1);
        }
        return semaforo;
    }

    public void inicializar() {
        // Crea el bloque génesis y lo añade como primer eslabón
        GenesisBlock genesis = new GenesisBlock();
        // Calcula el hash del génesis para consistencia
        String hashGenesis = HashUtil.calcularHash(genesis.toString());
        genesis.setHash(hashGenesis);
        cadena.add(genesis);
    }

    public void agregarBloque(Block bloque) {
        try {
            getSemaforo().acquire();
            try {
                cadena.add(bloque);
            } finally {
                getSemaforo().release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

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
     * Devuelve el tamaño de la cadena de forma thread-safe.
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
     * Devuelve una copia de la cadena para sincronización con otros nodos.
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
     * Reemplaza la cadena local por una cadena más larga recibida de otro nodo.
     * Solo se reemplaza si la cadena nueva es válida y más larga.
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
     * Devuelve TODAS las transacciones confirmadas en la cadena (de todos los bloques).
     * Útil para limpiar el mempool tras sincronizar con una cadena más larga.
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
     * Valida una cadena de bloques cualquiera.
     */
    private boolean validarCadena(ArrayList<Block> cadenaAValidar) {
        if (cadenaAValidar.isEmpty()) return false;
        String hashPrevio = cadenaAValidar.get(0).getHash();
        for (int i = 1; i < cadenaAValidar.size(); i++) {
            Block bloque = cadenaAValidar.get(i);
            if (!Objects.equals(hashPrevio, bloque.getHashPrevio())
                    || !bloque.getHash().startsWith(PREFIJO_DIFICULTAD)) {
                return false;
            }
            // Verificar que el hash recalculado coincide
            String hashRecalculado = HashUtil.calcularHash(bloque.toString());
            if (!hashRecalculado.equals(bloque.getHash())) {
                return false;
            }
            hashPrevio = bloque.getHash();
        }
        return true;
    }

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