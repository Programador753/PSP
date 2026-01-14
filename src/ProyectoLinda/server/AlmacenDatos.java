package ProyectoLinda.server;
import ProyectoLinda.comun.TuplaLinda;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
/**
 * Gestion de memoria compartida con semaforos.
 */
public class AlmacenDatos {
    private List<TuplaLinda> inventarioTuplas;
    private Semaphore semaforoAcceso;
    private Semaphore semaforoSincronizacion;
    private boolean enSincronizacion;
    /**
     * Pre: Ninguna.
     * Post: Inicializa lista y mutex.
     */
    public AlmacenDatos() {
        this.inventarioTuplas = new ArrayList<>();
        this.semaforoAcceso = new Semaphore(1);
        this.semaforoSincronizacion = new Semaphore(1);
        this.enSincronizacion = false;
    }
    /**
     * Pre: Tupla valida.
     * Post: Añade tupla.
     */
    public void postNote(TuplaLinda tupla) {
        try {
            semaforoSincronizacion.acquire();
            if (enSincronizacion) {
                System.out.println("BLOQUEADO: Servidor en proceso de sincronización. Operación rechazada.");
                semaforoSincronizacion.release();
                return;
            }
            semaforoSincronizacion.release();

            semaforoAcceso.acquire();
            inventarioTuplas.add(tupla);
            System.out.println("Almacenado: " + tupla);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforoAcceso.release();
        }
    }
    /**
     * Pre: Patron valido.
     * Post: Busca y borra. Devuelve null si no existe (no bloquea).
     */
    public TuplaLinda removeNote(TuplaLinda patron) {
        TuplaLinda encontrada = null;
        try {
            semaforoSincronizacion.acquire();
            if (enSincronizacion) {
                System.out.println("BLOQUEADO: Servidor en proceso de sincronización. Operación rechazada.");
                semaforoSincronizacion.release();
                return null;
            }
            semaforoSincronizacion.release();

            semaforoAcceso.acquire();
            Iterator<TuplaLinda> it = inventarioTuplas.iterator();
            while (it.hasNext()) {
                TuplaLinda t = it.next();
                if (t.coincideConPatron(patron)) {
                    it.remove();
                    encontrada = t;
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforoAcceso.release();
        }
        return encontrada;
    }
    /**
     * Pre: Patron valido.
     * Post: Busca. Devuelve null si no existe (no bloquea).
     */
    public TuplaLinda readNote(TuplaLinda patron) {
        TuplaLinda encontrada = null;
        try {
            semaforoAcceso.acquire();
            for (TuplaLinda t : inventarioTuplas) {
                if (t.coincideConPatron(patron)) {
                    encontrada = t;
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforoAcceso.release();
        }
        return encontrada;
    }
    /**
     * Pre: Ninguna.
     * Post: Copia de seguridad.
     */
    public List<TuplaLinda> obtenerCopiaSeguridad() {
        List<TuplaLinda> copia = new ArrayList<>();
        try {
            semaforoAcceso.acquire();
            copia.addAll(inventarioTuplas);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforoAcceso.release();
        }
        return copia;
    }
    /**
     * Pre: Lista valida.
     * Post: Carga datos.
     */
    public void cargarDatosMasivos(List<TuplaLinda> datos) {
        try {
            semaforoAcceso.acquire();
            inventarioTuplas.addAll(datos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforoAcceso.release();
        }
    }

    /**
     * Pre: Ninguna.
     * Post: Inicia modo sincronización bloqueando operaciones de escritura.
     */
    public void iniciarSincronizacion() {
        try {
            semaforoSincronizacion.acquire();
            enSincronizacion = true;
            System.out.println(">>> SINCRONIZACIÓN INICIADA - Operaciones bloqueadas <<<");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforoSincronizacion.release();
        }
    }

    /**
     * Pre: Lista valida de la replica.
     * Post: Limpia datos actuales y carga datos de la replica de forma segura.
     */
    public void sincronizarDesdeReplica(List<TuplaLinda> datosReplica) {
        try {
            semaforoAcceso.acquire();
            inventarioTuplas.clear();
            inventarioTuplas.addAll(datosReplica);
            System.out.println(">>> Datos sincronizados: " + datosReplica.size() + " tuplas recuperadas <<<");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforoAcceso.release();
        }
    }

    /**
     * Pre: Ninguna.
     * Post: Finaliza modo sincronización permitiendo operaciones normales.
     */
    public void finalizarSincronizacion() {
        try {
            semaforoSincronizacion.acquire();
            enSincronizacion = false;
            System.out.println(">>> SINCRONIZACIÓN COMPLETADA - Servidor operativo <<<");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforoSincronizacion.release();
        }
    }
}