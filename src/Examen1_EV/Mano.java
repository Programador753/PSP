package Examen1_EV;

/**
 * Clase Mano que representa una mano del jugador.
 *
 * @author Antonio
 * @version 1.0
 */
public class Mano {
    private Carta[] cartas; // Array que contiene las cartas de la mano

    /**
     * Constructor de la clase Mano.
     *
     * @param cantidadCartas Cantidad de cartas que tiene la mano.
     */
    public Mano(int cantidadCartas) {
        cartas = new Carta[cantidadCartas];
    }

    /**
     * Devuelve el array de cartas de la mano.
     *
     * @return array de cartas.
     */
    public Carta[] getCartas() {
        return cartas;
    }

    /**
     * Asigna un array de cartas a la mano.
     *
     * @param cartas array de cartas a asignar.
     */
    public void setCartas(Carta[] cartas) {
        this.cartas = cartas;
    }

    /**
     * Obtiene una carta específica de la mano.
     *
     * @param indice posición de la carta.
     * @return carta en esa posición.
     */
    public Carta getCarta(int indice) {
        if (indice >= 0 && indice < cartas.length) {
            return cartas[indice];
        }
        return null;
    }

    /**
     * Reemplaza una carta en la mano.
     *
     * @param indice posición.
     * @param carta  nueva carta.
     */
    public void setCarta(int indice, Carta carta) {
        if (indice >= 0 && indice < cartas.length) {
            cartas[indice] = carta;
        }
    }

    /**
     * Muestra todas las cartas de la mano.
     */
    public void mostrar() {
        for (int i = 0; i < cartas.length; i++) {
            if (cartas[i] != null) {
                System.out.println("[" + i + "] " + cartas[i].getNombre() + " (Coste: " + cartas[i].getCoste() + ")");
            }
        }
    }

    /**
     * Verifica si la mano está vacía.
     *
     * @return true si todas las cartas son null.
     */
    public boolean estaVacia() {
        for (Carta carta : cartas) {
            if (carta != null) {
                return false;
            }
        }
        return true;
    }
}
