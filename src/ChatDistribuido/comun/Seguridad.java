package ChatDistribuido.comun;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * Clase de utilidad para manejar el cifrado RSA y conversiones.
 * Utiliza Base64 para manejar los bytes cifrados como Strings.
 */
public class Seguridad {

    /**
     * Genera un par de claves RSA (Pública y Privada).
     * Pre: Ninguna.
     * Post: Devuelve un objeto KeyPair con las claves generadas.
     */
    public static KeyPair generarClaves() throws Exception {
        KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
        generador.initialize(2048);
        return generador.generateKeyPair();
    }

    /**
     * Cifra un texto utilizando una clave pública.
     * Pre: El texto y la clave no deben ser nulos.
     * Post: Devuelve el String cifrado en formato Base64.
     */
    public static String cifrar(String texto, PublicKey clave) throws Exception {
        Cipher cifrador = Cipher.getInstance("RSA");
        cifrador.init(Cipher.ENCRYPT_MODE, clave);
        byte[] bytesCifrados = cifrador.doFinal(texto.getBytes());
        return Base64.getEncoder().encodeToString(bytesCifrados);
    }

    /**
     * Descifra un texto en Base64 utilizando una clave privada.
     * Pre: El texto cifrado (Base64) y la clave no deben ser nulos.
     * Post: Devuelve el String descifrado original.
     */
    public static String descifrar(String textoBase64, PrivateKey clave) throws Exception {
        Cipher cifrador = Cipher.getInstance("RSA");
        cifrador.init(Cipher.DECRYPT_MODE, clave);
        byte[] bytesCifrados = Base64.getDecoder().decode(textoBase64);
        byte[] bytesDescifrados = cifrador.doFinal(bytesCifrados);
        return new String(bytesDescifrados);
    }

    /**
     * Convierte los bytes de una Public Key a String Base64 para enviarla.
     * Pre: La clave no debe ser nula.
     * Post: Devuelve la representación en String de la clave.
     */
    public static String clavePublicaAString(PublicKey pub) {
        return Base64.getEncoder().encodeToString(pub.getEncoded());
    }

    /**
     * Reconstruye una Public Key desde su String Base64.
     * Pre: El string de la clave no debe ser nulo.
     * Post: Devuelve el objeto PublicKey.
     */
    public static PublicKey stringAClavePublica(String pubStr) throws Exception {
        byte[] byteKey = Base64.getDecoder().decode(pubStr);
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(X509publicKey);
    }
}