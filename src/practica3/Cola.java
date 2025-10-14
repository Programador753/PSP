package practica3;

import java.util.concurrent.Semaphore;

public class Cola {
    private Node frente;
    private Node fin;
    private int tamaño;
    private int capacidad;

    // Semáforos para sincronización concurrente
    private Semaphore mutex;           // Semáforo para exclusión mutua
    private Semaphore elementosDisponibles;  // Contador de elementos en la cola
    private Semaphore espaciosDisponibles;   // Contador de espacios libres

    /**
     * Pre: capacidad > 0
     * Post: Constructor de la cola con capacidad limitada usando lista enlazada.
     *       Inicializa una cola vacía con la capacidad especificada.
     *       Usa semáforos para gestionar el acceso concurrente seguro.
     */
    public Cola(int capacidad) {
        this.capacidad = capacidad;
        this.frente = null;
        this.fin = null;
        this.tamaño = 0;

        /*
         * Inicialización de los semáforos para concurrencia
         */
        this.mutex = new Semaphore(1);  // Exclusión mutua (binario)
        this.elementosDisponibles = new Semaphore(0);  // Inicialmente no hay elementos
        this.espaciosDisponibles = new Semaphore(capacidad);  // Inicialmente hay capacidad espacios
    }

    /**
     * Pre: elemento != null
     * Post: Inserta un elemento al final de la cola si hay espacio disponible.
     *       Devuelve true si se insertó correctamente, false si la cola está llena.
     */
    private boolean insertar(String elemento) {
        /*
         * Verificar si la cola está llena
         */
        if (estaLlena()) {
            return false;
        }

        /*
         * Crear un nuevo nodo con el elemento
         */
        Node nuevoNodo = new Node(elemento);

        /*
         * Si la cola está vacía, el nuevo nodo es el frente y el fin
         */
        if (estaVacia()) {
            frente = nuevoNodo;
            fin = nuevoNodo;
        } else {
            /*
             * Enlazar el nuevo nodo al final de la cola
             */
            fin.setSiguiente(nuevoNodo);
            fin = nuevoNodo;
        }

        tamaño++;
        return true;
    }

    /**
     * Pre: ---
     * Post: Extrae un elemento del frente de la cola siguiendo política FIFO.
     *       Devuelve el elemento extraído o null si la cola está vacía.
     */
    private String extraer() {
        /*
         * Verificar si la cola está vacía
         */
        if (estaVacia()) {
            return null;
        }

        /*
         * Obtener el contenido del nodo del frente
         */
        String elemento = frente.getContenido();

        /*
         * Avanzar el frente al siguiente nodo
         */
        frente = frente.getSiguiente();

        /*
         * Si la cola queda vacía, actualizar también el fin
         */
        if (frente == null) {
            fin = null;
        }

        tamaño--;
        return elemento;
    }

    /**
     * Pre: elemento != null
     * Post: Inserta un elemento de forma thread-safe en la cola usando semáforos.
     *       Se bloquea si la cola está llena hasta que haya espacio disponible.
     */
    public void insertarConcurrente(String elemento) throws InterruptedException {
        /*
         * Esperar hasta que haya espacio disponible en la cola
         */
        espaciosDisponibles.acquire();

        /*
         * Adquirir el mutex para acceder a la cola (exclusión mutua)
         */
        mutex.acquire();
        try {
            /*
             * Insertar el elemento en la cola
             */
            insertar(elemento);
        } finally {
            /*
             * Liberar el mutex
             */
            mutex.release();
        }

        /*
         * Señalizar que hay un elemento disponible
         */
        elementosDisponibles.release();
    }

    /**
     * Pre: ---
     * Post: Extrae un elemento de forma thread-safe de la cola usando semáforos.
     *       Se bloquea si la cola está vacía hasta que haya elementos disponibles.
     */
    public String extraerConcurrente() throws InterruptedException {
        /*
         * Esperar hasta que haya elementos disponibles en la cola
         */
        elementosDisponibles.acquire();

        /*
         * Adquirir el mutex para acceder a la cola (exclusión mutua)
         */
        mutex.acquire();
        String elemento = null;
        try {
            /*
             * Extraer el elemento de la cola
             */
            elemento = extraer();
        } finally {
            /*
             * Liberar el mutex
             */
            mutex.release();
        }

        /*
         * Señalizar que hay un espacio disponible
         */
        espaciosDisponibles.release();

        return elemento;
    }

    /**
     * Pre: ---
     * Post: Consulta el elemento del frente sin extraerlo.
     *       Devuelve el elemento del frente o null si la cola está vacía.
     */
    private String frente() {
        if (estaVacia()) {
            return null;
        }
        return frente.getContenido();
    }

    /**
     * Pre: ---
     * Post: Consulta el frente de la cola de forma thread-safe sin extraerlo.
     */
    public String frenteConcurrente() throws InterruptedException {
        mutex.acquire();
        try {
            return frente();
        } finally {
            mutex.release();
        }
    }

    /**
     * Pre: ---
     * Post: Verifica si la cola está vacía.
     *       Devuelve true si no contiene elementos, false en caso contrario.
     */
    private boolean estaVacia() {
        return tamaño == 0;
    }

    /**
     * Pre: ---
     * Post: Verifica si la cola está vacía de forma thread-safe.
     */
    public boolean estaVaciaConcurrente() throws InterruptedException {
        mutex.acquire();
        try {
            return estaVacia();
        } finally {
            mutex.release();
        }
    }

    /**
     * Pre: ---
     * Post: Verifica si la cola está llena.
     *       Devuelve true si ha alcanzado su capacidad máxima, false en caso contrario.
     */
    private boolean estaLlena() {
        return tamaño == capacidad;
    }

    /**
     * Pre: ---
     * Post: Verifica si la cola está llena de forma thread-safe.
     */
    public boolean estaLlenaConcurrente() throws InterruptedException {
        mutex.acquire();
        try {
            return estaLlena();
        } finally {
            mutex.release();
        }
    }

    /**
     * Pre: ---
     * Post: Devuelve el número actual de elementos en la cola.
     */
    private int getTamaño() {
        return tamaño;
    }

    /**
     * Pre: ---
     * Post: Obtiene el tamaño actual de la cola de forma thread-safe.
     */
    public int getTamañoConcurrente() throws InterruptedException {
        mutex.acquire();
        try {
            return getTamaño();
        } finally {
            mutex.release();
        }
    }

    /**
     * Pre: ---
     * Post: Devuelve la capacidad máxima de la cola.
     */
    public int getCapacidad() {
        return capacidad;
    }

    /**
     * Pre: ---
     * Post: Devuelve una representación en cadena de la cola mostrando
     *       todos sus elementos desde el frente hasta el fin.
     */
    private String obtenerRepresentacion() {
        if (estaVacia()) {
            return "Cola vacía";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Cola [");

        Node actual = frente;
        while (actual != null) {
            sb.append(actual.getContenido());
            if (actual.hasSiguiente()) {
                sb.append(" -> ");
            }
            actual = actual.getSiguiente();
        }

        sb.append("] (").append(tamaño).append("/").append(capacidad).append(")");
        return sb.toString();
    }

    /**
     * Pre: ---
     * Post: Devuelve una representación en cadena de la cola de forma thread-safe.
     */
    public String toStringConcurrente() throws InterruptedException {
        mutex.acquire();
        try {
            return obtenerRepresentacion();
        } finally {
            mutex.release();
        }
    }

    /**
     * Pre: ---
     * Post: Devuelve una representación en cadena de la cola.
     */
    @Override
    public String toString() {
        return obtenerRepresentacion();
    }
}