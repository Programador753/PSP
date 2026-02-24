package Blockchain;

import java.security.KeyPair;

/**
 *  Esta clase proporciona las calves publicas y privadas de los usuarios para realizar transacciones seguras en la red
 *  blockchain.
 */
public class KeyPairUtil {

    /**
     * Método estático que genera un par de claves (clave pública y clave privada) utilizando el algoritmo de Curva Elíptica (EC).
     * Pre: No se requieren parámetros de entrada.
     * Post: Retorna un objeto KeyPair que contiene la clave pública y la clave privada generadas.
     * @return
     */
    public static KeyPair generarParClaves(){

        try {
            //Pedimos el generador de claves con el algoritmo de Curva Elíptica ("EC")
            java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("EC");

            //Le damos un tamaño seguro (256 bits es el estándar)
            keyGen.initialize(256);

            //Generamos el par de claves (clave pública y clave privada) y lo devolvemos
            return keyGen.genKeyPair();

        }catch(Exception e){
            System.out.println("Error al generar el par de claves: " + e.getMessage());
            return null;
        }
    }
}
