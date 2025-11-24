package LINDA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Clase que representa una tupla de datos.
 * Los datos de la tupla pueden ser de cualquier tipo.
 * Para poder almacenar una tupla uso ArrayList.
 * Implementa Serializable para poder ser enviada a través de Sockets.
 *
 * @version 1.0
 * @autor Antonio y Victor
 */
public class Tupla implements Serializable {

    //Atributo de la clase Tupla que almacena los datos de la tupla.
    private ArrayList<String> campos;

    /**
     * Metodo constructor de la clase Tupla que recibe como parametro un ArrayList de Strings.
     * Los tres puntos son para poder usar el metodo varargs que permite pasar un número variable de argumentos.
     *
     * @param datos ArrayList de Strings con los datos de la tupla.
     */
    public Tupla(String... datos) {

        // Convertimos el array de Strings a un ArrayList de Strings para poder almacenarlo en campos.
        this.campos = new ArrayList<>(Arrays.asList(datos));
    }

    /**
     * Metodo que devuelve un ArrayList de Strings con los datos de la tupla.
     *
     * @return
     */
    public ArrayList<String> getCampos() {
        return campos;
    }

    /**
     * Metodo que asigna un nuevo ArrayList de Strings a los campos de la tupla.
     *
     * @param campos
     */
    public void setCampos(ArrayList<String> campos) {
        this.campos = campos;
    }

    /**
     * Metodo que devuelve una representacion en String de la tupla.
     *
     * @return
     */
    @Override
    public String toString() {
        return "Tupla{" + "campos=" + campos + '}';
    }

    /**
     * Este metodo busca matches entre tuplas según lo que tenga guardado sus tuplas
     * @param dato
     * @return
     */
    public boolean coincidente(Tupla dato){
        if(campos.size() != dato.getCampos().size()) {
            return false;
        }

        return true;
    }
}