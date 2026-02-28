package Blockchain;

import java.io.Serializable;

/**
 * Clase que encapsula los mensajes enviados entre nodos por la red P2P.
 * Cada mensaje tiene un tipo que indica cómo debe ser procesado y un payload con los datos.
 */
public class Mensaje implements Serializable {

    // Tipos de mensaje posibles
    public static final String TRANSACTION = "TRANSACTION";
    public static final String BLOCK = "BLOCK";
    public static final String PEER_LIST = "PEER_LIST";
    public static final String SYNC_REQUEST = "SYNC_REQUEST";
    public static final String SYNC_RESPONSE = "SYNC_RESPONSE";
    public static final String CONTACT_INFO = "CONTACT_INFO";
    public static final String NEW_NODE = "NEW_NODE";
    public static final String SYNC_MEMPOOL_REQUEST = "SYNC_MEMPOOL_REQUEST";
    public static final String SYNC_MEMPOOL_RESPONSE = "SYNC_MEMPOOL_RESPONSE";

    private String tipo;
    private Object payload;

    /**
     * Constructor del mensaje.
     * @param tipo Tipo de mensaje (TRANSACTION, BLOCK, PEER_LIST, SYNC_REQUEST, SYNC_RESPONSE)
     * @param payload Datos del mensaje (Transaction, Block, lista de puertos, Blockchain, etc.)
     */
    public Mensaje(String tipo, Object payload) {
        this.tipo = tipo;
        this.payload = payload;
    }

    public String getTipo() {
        return tipo;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Mensaje{tipo='" + tipo + "', payload=" + payload + "}";
    }
}

