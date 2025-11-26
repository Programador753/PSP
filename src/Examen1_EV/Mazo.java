package Examen1_EV;

/**
 * Clase para implementar el mazo del juego (pila de cartas que tienes guardadas y que aún no puedes usar.).
 *
 * @author Antonio
 * @version 1.0
 */
public class Mazo {
    private Carta[] elementos; // Array que contiene las cartas del mazo.
    private int tope; // Maximo índice del array elementos.

    /**
     * Constructor de la clase Mazo.
     *
     * @param cantidad Cantidad de cartas que tiene el mazo.
     */
    public Mazo(int cantidad) {
        elementos = new Carta[cantidad];
        tope = -1;
    }

    /**
     * Añade una carta al mazo.
     *
     * @param carta Carta a añadir.
     */
    public void apilar(Carta carta) {
        if (tope < elementos.length - 1) {
            elementos[++tope] = carta;
        }
    }

    /**
     * Extrae y devuelve la carta superior del mazo.
     *
     * @return Carta superior o null si está vacío.
     */
    public Carta desapilar() {
        if (!estaVacio()) {
            return elementos[tope--];
        }
        return null;
    }

    /**
     * Verifica si el mazo está vacío.
     *
     * @return true si está vacío, false en caso contrario.
     */
    public boolean estaVacio() {
        return tope == -1;
    }

    /**
     * Metodo que devuelve la carta en la parte superior del mazo.
     */
    public Carta getTop() {
        if (!estaVacio()) {
            return elementos[tope];
        }
        return null;
    }

    /**
     * Metodo que devuelve la cantidad de cartas del mazo.
     *
     * @return cantidad de cartas restantes.
     */
    public int getCantidad() {
        return tope + 1;
    }
}
