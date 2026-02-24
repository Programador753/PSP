package Blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * Esta clase proporciona métodos para firmar datos con una clave privada y verificar firmas con una clave pública
 * utilizando el algoritmo de firma digital ECDSA con SHA-256.
 * El método firmar() toma una clave privada y una cadena de datos, y devuelve la firma digital en forma de un arreglo
 * de bytes. El método verificar() toma una clave pública, la cadena de datos original y la firma obtenida, y devuelve
 * un valor booleano indicando si la firma es válida o no.
 */
public class SignatureUtil {

    /**
     * Método que firma los datos utilizando la clave privada proporcionada.
     * @param clavePrivada La clave privada que se utilizará para generar la firma digital.
     * @param datos La cadena de texto que se desea firmar. Esta información se convertirá a bytes para el proceso de firma.
     * @return Un arreglo de bytes que representa la firma digital generada a partir de los datos y la clave privada.
     * Si ocurre un error durante el proceso, se devuelve null.
     */
    public static byte[] firmar(PrivateKey clavePrivada, String datos) {
        try {
            // 1. Pide la herramienta Signature usando el algoritmo "SHA256withECDSA"
            Signature firma = Signature.getInstance("SHA256withECDSA");

            // 2. Inicializa la herramienta para firmar pasándole la clavePrivada
            firma.initSign(clavePrivada);

            // 3. Mete los datos en la herramienta (recuerda pasarlos a bytes con .getBytes("UTF-8"))
            firma.update(datos.getBytes("UTF-8"));

            // 4. Genera la firma y devuélvela con return
            return firma.sign();

        } catch (Exception e) {
            System.out.println("Error al firmar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Método que verifica si la firma obtenida es válida para los datos proporcionados utilizando la clave pública.
     * @param clavePublica La clave pública que se utilizará para verificar la firma digital. Esta clave debe
     *                     corresponder a la clave privada que se usó para generar la firma.
     * @param datos La cadena de texto original que se firmó. Esta información se convertirá a bytes para el
     *              proceso de verificación.
     * @param firmaObtenida El arreglo de bytes que representa la firma digital que se desea verificar.
     *                      Esta firma debe haber sido generada a partir de los mismos datos y la clave privada
     *                      correspondiente a la clave pública proporcionada.
     * @return Un valor booleano que indica si la firma es válida (true) o no (false).
     * Si ocurre un error durante el proceso, se devuelve false por seguridad, indicando que la firma no es válida.
     */
    public static boolean verificar(PublicKey clavePublica, String datos, byte[] firmaObtenida) {
        try {
            // 1. Pide la herramienta Signature usando el mismo algoritmo "SHA256withECDSA"
            Signature firma = Signature.getInstance("SHA256withECDSA");

            // 2. Inicializa la herramienta para verificar pasándole la clavePublica
            firma.initVerify(clavePublica);

            // 3. Mete los datos originales en la herramienta (igual, pasados a bytes con UTF-8)
            firma.update(datos.getBytes("UTF-8"));

            // 4. Llama al método que comprueba si la firmaObtenida coincide y devuelve el true o false directamente
            return firma.verify(firmaObtenida);

        } catch (Exception e) {
            System.out.println("Error al verificar: " + e.getMessage());
            return false; // Si algo falla, por seguridad decimos que la firma no es válida
        }
    }
}