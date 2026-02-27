package Blockchain;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Clase que representa una transacción en la red blockchain transportando valor entre usuarios.
 * Cada transacción lleva una firma digital ECDSA que garantiza la autenticidad del emisor.
 */
public class Transaction implements Serializable {
    private String id;
    private String clavePublicaEmisor;   // Base64 de la clave pública del emisor
    private String clavePublicaReceptor;  // Base64 de la clave pública del receptor
    private double monto;
    private byte[] firma;                 // Firma digital ECDSA del emisor

    /**
     * Constructor de la clase Transaction.
     * Pre: Los parámetros id, clavePublicaEmisor y clavePublicaReceptor no deben ser nulos.
     * Post: Se crea un objeto Transaction sin firma (debe firmarse después con firmar()).
     */
    public Transaction(String id, String clavePublicaEmisor, String clavePublicaReceptor, double monto) {
        this.id = id;
        this.clavePublicaEmisor = clavePublicaEmisor;
        this.clavePublicaReceptor = clavePublicaReceptor;
        this.monto = monto;
        this.firma = null;
    }

    /**
     * Convierte una PublicKey a su representación Base64 para serialización.
     */
    public static String clavePublicaABase64(PublicKey clave) {
        return Base64.getEncoder().encodeToString(clave.getEncoded());
    }

    /**
     * Reconstruye una PublicKey desde su representación Base64.
     * Necesario para verificar firmas de transacciones recibidas por red.
     */
    public static PublicKey base64AClavePublica(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            System.out.println("Error al reconstruir clave pública: " + e.getMessage());
            return null;
        }
    }

    /**
     * Firma la transacción con la clave privada del emisor.
     * Pre: La clave privada debe corresponder a la clave pública del emisor.
     * Post: El campo firma queda establecido con la firma digital ECDSA.
     */
    public void firmar(PrivateKey clavePrivada) {
        String datos = getDatosParaFirmar();
        this.firma = SignatureUtil.firmar(clavePrivada, datos);
    }

    /**
     * Verifica que la firma de la transacción es válida usando la clave pública del emisor.
     * Reconstruye la PublicKey desde el Base64 almacenado en clavePublicaEmisor.
     * @return true si la firma es válida, false en caso contrario.
     */
    public boolean verificarFirma() {
        if (firma == null) {
            System.out.println("La transacción no está firmada.");
            return false;
        }
        PublicKey clave = base64AClavePublica(clavePublicaEmisor);
        if (clave == null) {
            return false;
        }
        String datos = getDatosParaFirmar();
        return SignatureUtil.verificar(clave, datos, firma);
    }

    /**
     * Devuelve la cadena de datos que se firma/verifica (sin incluir la propia firma).
     */
    private String getDatosParaFirmar() {
        return id + clavePublicaEmisor + clavePublicaReceptor + monto;
    }

    public String getId() {
        return id;
    }

    public String getClavePublicaEmisor() {
        return clavePublicaEmisor;
    }

    public String getClavePublicaReceptor() {
        return clavePublicaReceptor;
    }

    public double getMonto() {
        return monto;
    }

    public byte[] getFirma() {
        return firma;
    }

    /**
     * Serialización para el cálculo de hash del bloque.
     */
    @Override
    public String toString() {
        return id + clavePublicaEmisor + clavePublicaReceptor + monto;
    }
}