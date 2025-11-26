package auxiliar;

/**
 * Clase para implementar la pila del juego.
 *
 * @author Antonio
 * @version 1.0
 */
public class Pila {
    private int[] elementos; // Array que contiene los elementos de la pila

    /**
     * Constructor de la clase Pila.
     *
     * @param tamano
     */
    public Pila(int tamano) {
        elementos = new int[tamano];
    }

    /**
     * Devuelve el elemento en la parte superior de la pila.
     *
     * @return
     */
    public int getTop() {
        return elementos[elementos.length - 1];
    }

    /**
     * Metodo para insertar un elemento en la parte superior de la pila.
     *
     * @param elemento
     */
    public void push(int elemento) {
        elementos[elementos.length - 1] = elemento;
    }

    /**
     * Metodo para eliminar el elemento en la parte superior de la pila.
     *
     * @return
     */
    public int pop() {
        return elementos[elementos.length - 1]--;
    }

    /**
     * Metodo que devuelve el tamaño de la pila.
     *
     * @return
     */
    public int getSize() {
        return elementos.length;
    }

    /**
     * Metodo que devuelve los elementos de la pila.
     *
     * @return
     */
    public int[] getElements() {
        return elementos;
    }
}
