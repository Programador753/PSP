package Blockchain;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase representa el mempool, un espacio temporal donde se almacenan las transacciones que aún no han sido
 * incluidas en un bloque. El mempool actúa como una especie de "sala de espera" para las transacciones,
 * permitiendo que los mineros seleccionen cuáles incluir en el próximo bloque a minar. Las transacciones en el mempool
 * pueden ser agregadas por los usuarios y permanecen allí hasta que son confirmadas en la cadena de bloques,
 * momento en el cual se eliminan del mempool.
 */
public class Mempool {

    //Lista que almacena las transacciones que aun no han sido minadas
    public List<Transaction> transacciones;

    /**
     *  Constructor de la clase Mempool que inicializa la lista de transacciones vacía.
     *  Pre: No se requiere ningún parámetro para la creación del mempool.
     *  Post: Se crea un objeto Mempool con una lista de transacciones vacía, listo para almacenar nuevas transacciones.
     *  El mempool actúa como un espacio temporal donde las transacciones se mantienen hasta que son incluidas en un
     *  bloque por los mineros.
     */
    public Mempool(){
        transacciones = new ArrayList<>();
    }

    /**
     * Método que agrega una transacción al mempool.
     * Pre: El objeto Transaction debe ser válido y no nulo.
     * Post: La transacción se agrega al mempool, quedando disponible para ser incluida en futuros bloques.
     * @param transaccion Objeto Transaction que representa la transacción a agregar al mempool. No debe ser nulo.
     */
    public void agregarTransaccion(Transaction transaccion){

        if(transaccion != null){
            transacciones.add(transaccion);
        }
        else{
            System.out.println("Transaccion no encontrada o esta vacia");
        }
    }

    /**
     * Método que devuelve una lista con las primeras N transacciones del mempool.
     * Pre: El número de transacciones a obtener debe ser un valor positivo y menor o igual al tamaño del mempool.
     * Post: Retorna una lista de objetos Transaction que contiene
     * las primeras transacciones del mempool, hasta el número especificado o el total disponible si es menor.
     * @param numeroTransacciones Número de transacciones a obtener del mempool. Debe ser un valor positivo y no mayor
     *                            al tamaño actual del mempool.
     * @return Una lista de objetos Transaction que contiene las primeras transacciones del mempool,
     * limitada por el número especificado.
     */
    public List<Transaction> obtenerPrimeras(int numeroTransacciones){

        // Lista de transacciones que se devolverá al final del método, inicialmente vacía
        List<Transaction> listaTransacciones = new ArrayList<>();

        //Calcula la cantidad de transacciones que va a sacar del mempool, que será el minimo entre el numero solicitado
        //y el tamaño actual del mempool, para evitar errores de indice
        int cantidadASacar = Math.min(numeroTransacciones, transacciones.size());

        //Agrega a la lista de transacciones a devolver las primeras transacciones
        for(int i = 0; i < cantidadASacar; i++){
            listaTransacciones.add(transacciones.get(i));
        }

        return listaTransacciones;
    }

    /**
     * Método que elimina las transacciones que han sido minadas del mempool.
     * Pre: La lista de transacciones minadas debe contener objetos Transaction válidos que correspond
     * a transacciones que están actualmente en el mempool.
     * Post: Las transacciones que han sido minadas se eliminan del mempool,
     * dejando solo las transacciones que aún no han sido confirmadas en la cadena de bloques. Esto asegura
     * que el mempool se mantenga actualizado y solo contenga transacciones pendientes de ser minadas.
     * @param transaccionesMinadas Lista de objetos Transaction que representa las transacciones que han sido minadas
     *                             y deben ser eliminadas del mempool.
     */
    public void eliminarTransacciones(List<Transaction> transaccionesMinadas){
        // Esto busca y borra de golpe y de forma segura todas las coincidencias
        transacciones.removeAll(transaccionesMinadas);
    }

    /**
     * Método que verifica si el mempool está vacío.
     * Pre: No se requiere ningún parámetro para esta verificación.
     * Post: Retorna un valor booleano que indica si el mempool no contiene transacciones. Si el mempool está vacío,
     * devuelve true; de lo contrario, devuelve false.
     * @return
     */
    public boolean estaVacio(){
        return transacciones.isEmpty();
    }
}
