package Blockchain;

import java.io.Serializable;
/**
 * Clase que representa una transacción en la red blockchain transportando valor entre usuarios.
 */
public class Transaction implements Serializable {
    private String id;
    private String clavePublicaEmisor;
    private String clavePublicaReceptor;
    private double monto;
    /**
     * Constructor de la clase Transaction que inicializa todos los atributos.
     * Pre: Los parámetros id, clavePublicaEmisor y clavePublicaReceptor no deben ser nulos.
     * El monto debe ser un número válido.
     * Post: Se crea un objeto Transaction con los valores proporcionados asignados a sus respectivos atributos.
     */
    public Transaction(String id, String clavePublicaEmisor, String clavePublicaReceptor, double monto) {
        this.id = id;
        this.clavePublicaEmisor = clavePublicaEmisor;
        this.clavePublicaReceptor = clavePublicaReceptor;
        this.monto = monto;
    }
    /**
     * Método que devuelve el identificador único de la transacción.
     * Pre: El objeto Transaction debe estar correctamente instanciado.
     * Post: Retorna una cadena de texto con el hash o identificador de la transacción.
     */
    public String getId() {
        return id;
    }
    /**
     * Método que devuelve la clave pública del emisor de la transacción.
     * Pre: El objeto Transaction debe estar correctamente instanciado.
     * Post: Retorna una cadena de texto con la clave pública del usuario que envía los fondos.
     */
    public String getClavePublicaEmisor() {
        return clavePublicaEmisor;
    }
    /**
     * Método que devuelve la clave pública del receptor de la transacción.
     * Pre: El objeto Transaction debe estar correctamente instanciado.
     * Post: Retorna una cadena de texto con la clave pública del usuario que recibe los fondos.
     */
    public String getClavePublicaReceptor() {
        return clavePublicaReceptor;
    }
    /**
     * Método que devuelve la cantidad de valor transferido en la transacción.
     * Pre: El objeto Transaction debe estar correctamente instanciado.
     * Post: Retorna un valor numérico decimal que representa el monto de la transacción.
     */
    public double getMonto() {
        return monto;
    }
    /**
     * Método que serializa la transacción concatenando sus atributos en texto continuo.
     * Pre: El objeto Transaction debe tener sus atributos inicializados.
     * Post: Retorna una cadena de texto que une el id, emisor, receptor y monto, útil para envíos por red.
     */
    @Override
    public String toString() {
        return id + clavePublicaEmisor + clavePublicaReceptor + monto;
    }
}