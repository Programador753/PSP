package practica3;

/**
 * Esta clase encapsula los datos compartidos entre los hilos del sistema concurrente.
 * Contiene la cola concurrente y proporciona acceso sincronizado a ella.
 */
public class ShareData {
    private Cola cola;

    /**
     * Pre: capacidad > 0
     * Post: Constructor que inicializa la cola concurrente compartida
     *       con la capacidad especificada.
     */
    public ShareData(int capacidad) {
        this.cola = new Cola(capacidad);
    }

    /**
     * Pre: ---
     * Post: Devuelve la referencia a la cola concurrente compartida.
     */
    public Cola getCola() {
        return cola;
    }

    /**
     * Pre: elemento != null
     * Post: Inserta un elemento en la cola compartida de forma thread-safe.
     */
    public void insertar(String elemento) throws InterruptedException {
        cola.insertarConcurrente(elemento);
    }

    /**
     * Pre: ---
     * Post: Extrae un elemento de la cola compartida de forma thread-safe.
     */
    public String extraer() throws InterruptedException {
        return cola.extraerConcurrente();
    }

    /**
     * Pre: ---
     * Post: Devuelve el tamaño actual de la cola compartida.
     */
    public int getTamaño() throws InterruptedException {
        return cola.getTamañoConcurrente();
    }

    /**
     * Pre: ---
     * Post: Devuelve la capacidad de la cola compartida.
     */
    public int getCapacidad() {
        return cola.getCapacidad();
    }

    /**
     * Pre: ---
     * Post: Verifica si la cola compartida está vacía.
     */
    public boolean estaVacia() throws InterruptedException {
        return cola.estaVaciaConcurrente();
    }

    /**
     * Pre: ---
     * Post: Devuelve una representación en cadena de la cola compartida.
     */
    public String mostrarCola() throws InterruptedException {
        return cola.toStringConcurrente();
    }
}