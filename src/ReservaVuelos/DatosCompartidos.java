package ReservaVuelos;

import java.util.concurrent.Semaphore;

/**
 * Clase que encapsula los datos compartidos entre el servidor y los threads de clientes.
 * Utiliza semáforos para garantizar acceso exclusivo a cada plaza del avión.
 */
public class DatosCompartidos {
    private final Avion avion;
    private final Semaphore[][] semaforosPlazas;
    private static final int FILAS = 4;
    private static final int ASIENTOS = 4;

    /**
     * Pre: ninguna
     * Post: Se crean los datos compartidos con un avión y un semáforo para cada plaza
     */
    public DatosCompartidos() {
        avion = new Avion();
        semaforosPlazas = new Semaphore[FILAS][ASIENTOS];
        /*
         * Inicializar un semáforo por cada plaza del avión
         */
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < ASIENTOS; j++) {
                semaforosPlazas[i][j] = new Semaphore(1, true);
            }
        }
    }

    /**
     * Pre: ninguna
     * Post: Retorna true si hay al menos una plaza libre, false si está completo
     */
    public boolean hayPlazasLibres() {
        return avion.hayPlazasLibres();
    }

    /**
     * Pre: ninguna
     * Post: Retorna el número actual de plazas ocupadas
     */
    public int getPlazasOcupadas() {
        return avion.getPlazasOcupadas();
    }

    /**
     * Pre: plaza debe tener formato válido (ej: "2C")
     * Post: Intenta reservar la plaza usando el semáforo correspondiente para
     *       garantizar acceso exclusivo. Retorna el resultado de la operación.
     *
     * @param plaza Identificador de la plaza (ej: "2C")
     */
    public String reservarPlaza(String plaza) {
        /*
         * Validar formato básico
         */
        if (plaza == null || plaza.length() != 2) {
            return "ERROR:Formato inválido";
        }

        char charFila = plaza.charAt(0);
        char charAsiento = plaza.charAt(1);

        if (!Character.isDigit(charFila) || !Character.isLetter(charAsiento)) {
            return "ERROR:Formato inválido";
        }

        /*
         * Convertir a índices de array
         */
        int fila = Character.getNumericValue(charFila) - 1;
        int asiento = Character.toUpperCase(charAsiento) - 'A';

        /*
         * Validar rangos válidos
         */
        if (fila < 0 || fila >= FILAS || asiento < 0 || asiento >= ASIENTOS) {
            return "ERROR:Plaza inválida (debe ser 1-4 y A-D)";
        }

        /*
         * Obtener el semáforo específico de la plaza
         */
        Semaphore semaforoPlaza = semaforosPlazas[fila][asiento];

        try {
            /*
             * Adquirir el semáforo para acceso exclusivo a la plaza
             */
            semaforoPlaza.acquire();
            /*
             * Realizar la reserva de forma segura
             */
            return avion.reservarPlaza(plaza);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "ERROR:Operación interrumpida";
        } finally {
            /*
             * Liberar el semáforo
             */
            semaforoPlaza.release();
        }
    }
}

