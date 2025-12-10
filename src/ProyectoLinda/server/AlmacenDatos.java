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
    /**
     * Pre: Ninguna.
     * Post: Inicializa lista y mutex.
     */
    public AlmacenDatos() {
        this.inventarioTuplas = new ArrayList<>();
        this.semaforoAcceso = new Semaphore(1);
    }
    /**
     * Pre: Tupla valida.
     * Post: Añade tupla.
     */
    public void postNote(TuplaLinda tupla) {
        try {
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
}