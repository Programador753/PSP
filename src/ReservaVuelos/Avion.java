package ReservaVuelos;

/**
 * Clase que representa el avión con 4 filas y 4 asientos (A, B, C, D).
 * Gestiona las reservas de forma thread-safe mediante sincronización.
 *
 */
public class Avion {
    private final boolean[][] plazas; // true = ocupada, false = libre
    private static final int FILAS = 4;
    private static final int ASIENTOS = 4;
    private int plazasOcupadas;

    /**
     * Pre: ninguna
     * Post: Se crea un avión con 16 plazas libres (4x4)
     */
    public Avion() {
        plazas = new boolean[FILAS][ASIENTOS];
        plazasOcupadas = 0;
    }

    /**
     * Pre: plaza debe tener formato válido (ej: "2C")
     * Post: Si la plaza está libre, se reserva y retorna "RESERVADA:plaza".
     *       Si está ocupada, retorna "PLAZA OCUPADA:estado".
     *       Si el formato es inválido, retorna "ERROR:..."
     *
     * @param plaza Identificador de la plaza (ej: "2C")
     *
     */
    public synchronized String reservarPlaza(String plaza) {
        /*
         * Validar formato de la plaza
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
        int asiento = charAsiento - 'A';

        /*
         * Validar rangos válidos
         */
        if (fila < 0 || fila >= FILAS || asiento < 0 || asiento >= ASIENTOS) {
            return "ERROR:Plaza inválida (debe ser 1-4 y A-D)";
        }

        /*
         * Intentar reservar la plaza
         */
        if (plazas[fila][asiento]) {
            return "PLAZA OCUPADA:" + getEstadoAvion();
        } else {
            plazas[fila][asiento] = true;
            plazasOcupadas++;
            return "RESERVADA:" + plaza;
        }
    }

    /**
     * Pre: ninguna
     * Post: Retorna true si hay al menos una plaza libre, false si está completo
     *
     *
     */
    public synchronized boolean hayPlazasLibres() {

        return plazasOcupadas < (FILAS * ASIENTOS);
    }

    /**
     * Pre: ninguna
     * Post: Retorna una cadena con el estado de todas las plazas en formato
     *       "XXXX-LXXL-XXXX-LLLL" donde X=ocupada, L=libre, separadas por filas
     *
     *
     */
    private String getEstadoAvion() {
        StringBuilder estado = new StringBuilder();
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < ASIENTOS; j++) {
                estado.append(plazas[i][j] ? 'X' : 'L');
            }
            if (i < FILAS - 1) {
                estado.append('-');
            }
        }
        return estado.toString();
    }

    /**
     * Pre: ninguna
     * Post: Retorna el número actual de plazas ocupadas
     *
     *
     */
    public synchronized int getPlazasOcupadas() {
        return plazasOcupadas;
    }
}

