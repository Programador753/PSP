package LINDA;

import java.io.Serializable;

/**
 * Clase auxiliar que contiene la lógica para la comparación de patrones en el sistema Linda.
 * Implementa Serializable para poder ser enviada a través de sockets si fuera necesario.
 */
public class LindaDriver implements Serializable {
    /**
     * Comprueba si una tupla de datos coincide con un patrón dado, considerando variables.
     * Pre: Tanto la tupla como el patrón deben ser arrays de String inicializados.
     * Post: Devuelve true si coinciden en longitud y contenido (o variable), false en caso contrario.
     */
    public static boolean coincide(String[] tupla, String[] patron) {
        if (tupla.length != patron.length) {
            return false;
        }
        for (int i = 0; i < tupla.length; i++) {
            String datoTupla = tupla[i];
            String datoPatron = patron[i];
            if (datoPatron.startsWith("?")) {
                continue;
            }
            if (!datoTupla.equals(datoPatron)) {
                return false;
            }
        }
        return true;
    }
}