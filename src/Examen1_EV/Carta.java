package Examen1_EV;

/**
 * Clase que representa una carta.
 * Controla toda la logica relacionada con las cartas del juego.
 *
 * @author Antonio
 * @version 1.0
 */
public class Carta {
    private int coste; // Coste de la carta(Elixir)
    private String nombre; // Nombre de la carta(Tropa)

    /**
     * Constructor de la clase Carta.
     *
     * @param coste  Coste de la carta(Elixir).
     * @param nombre Nombre de la carta(Tropa).
     */
    public Carta(int coste, String nombre) {
        this.coste = coste;
        this.nombre = nombre;
    }

    /**
     * Devuelve el coste de la carta.
     *
     * @return
     */
    public int getCoste() {
        return coste;
    }

    /**
     * Devuelve el nombre de la carta.
     *
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve una cadena con el nombre y el coste de la carta.
     *
     * @return
     */
    @Override
    public String toString() {
        return nombre + " (Coste: " + coste + ")";
    }
}
