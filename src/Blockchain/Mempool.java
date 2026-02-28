package Blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Esta clase representa el mempool, un espacio temporal donde se almacenan las transacciones que aún no han sido
 * incluidas en un bloque. Protegido con semáforos para acceso concurrente seguro desde múltiples hilos.
 */
public class Mempool {

    // Lista que almacena las transacciones que aún no han sido minadas
    private List<Transaction> transacciones;

    // Semáforo binario (mutex) para proteger el acceso concurrente al mempool
    private final Semaphore semaforo;

    /**
     * Constructor de la clase Mempool que inicializa la lista de transacciones vacía y el semáforo.
     * Post: Se crea un objeto Mempool con una lista vacía y un semáforo binario (1 permiso).
     */
    public Mempool() {
        transacciones = new ArrayList<>();
        semaforo = new Semaphore(1);
    }

    /**
     * Método que agrega una transacción al mempool de forma thread-safe.
     * Usa semáforo para garantizar exclusión mutua.
     * @return true si la transacción fue añadida (era nueva), false si ya existía o era nula.
     */
    public boolean agregarTransaccion(Transaction transaccion) {
        try {
            semaforo.acquire();
            try {
                if (transaccion != null) {
                    // Evitar duplicados por ID
                    for (Transaction tx : transacciones) {
                        if (tx.getId().equals(transaccion.getId())) {
                            return false; // Ya existe, no la añade
                        }
                    }
                    transacciones.add(transaccion);
                    return true;
                } else {
                    System.out.println("Transacción no encontrada o está vacía");
                    return false;
                }
            } finally {
                semaforo.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Hilo interrumpido al agregar transacción");
            return false;
        }
    }

    /**
     * Método que devuelve una lista con las primeras N transacciones del mempool de forma thread-safe.
     */
    public List<Transaction> obtenerPrimeras(int numeroTransacciones) {
        List<Transaction> listaTransacciones = new ArrayList<>();
        try {
            semaforo.acquire();
            try {
                int cantidadASacar = Math.min(numeroTransacciones, transacciones.size());
                for (int i = 0; i < cantidadASacar; i++) {
                    listaTransacciones.add(transacciones.get(i));
                }
            } finally {
                semaforo.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Hilo interrumpido al obtener transacciones");
        }
        return listaTransacciones;
    }

    /**
     * Método que elimina las transacciones que han sido minadas del mempool de forma thread-safe.
     */
    public void eliminarTransacciones(List<Transaction> transaccionesMinadas) {
        try {
            semaforo.acquire();
            try {
                // Elimina por ID para evitar problemas de referencia entre procesos
                List<String> idsMinados = new ArrayList<>();
                for (Transaction tx : transaccionesMinadas) {
                    idsMinados.add(tx.getId());
                }
                transacciones.removeIf(tx -> idsMinados.contains(tx.getId()));
            } finally {
                semaforo.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Hilo interrumpido al eliminar transacciones");
        }
    }

    /**
     * Método que verifica si el mempool está vacío de forma thread-safe.
     */
    public boolean estaVacio() {
        try {
            semaforo.acquire();
            try {
                return transacciones.isEmpty();
            } finally {
                semaforo.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }
    }

    /**
     * Devuelve el número de transacciones pendientes de forma thread-safe.
     */
    public int size() {
        try {
            semaforo.acquire();
            try {
                return transacciones.size();
            } finally {
                semaforo.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }
}
