package LINDA;

import java.io.*;
import java.net.*;

/**
 * Servidor central que actúa como intermediario y coordinador del sistema Linda.
 * Gestiona la distribución de tuplas y la tolerancia a fallos (réplica).
 */
public class ServidorLinda {
    /** Puerto del servidor principal para tuplas pequeñas. */
    static final int PUERTO_1_3_PRINCIPAL = 9001;
    /** Puerto del servidor réplica para tuplas pequeñas. */
    static final int PUERTO_1_3_REPLICA = 9002;
    /** Puerto del servidor para tuplas medianas. */
    static final int PUERTO_4_5 = 9003;
    /** Puerto del servidor para tuplas grandes. */
    static final int PUERTO_6 = 9004;

    /**
     * Método principal que inicia el servicio de coordinación.
     * Pre: Los servidores de almacenamiento deben estar activos preferiblemente.
     * Post: El servidor queda escuchando en el puerto 8080.
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("SERVIDOR LINDA escuchando en puerto 8080");
            while (true) {
                Socket cliente = serverSocket.accept();
                new Thread(() -> atenderCliente(cliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Procesa la petición del cliente y la enruta al servidor adecuado según el tamaño de la tupla.
     * Pre: El cliente debe enviar una operación válida y una tupla de Strings.
     * Post: Devuelve al cliente el resultado de la operación o un mensaje de error.
     */
    private static void atenderCliente(Socket cliente) {
        try (ObjectInputStream in = new ObjectInputStream(cliente.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(cliente.getOutputStream())) {
            String operacion = (String) in.readObject();
            String[] tupla = (String[]) in.readObject();
            int len = tupla.length;
            Object respuesta = null;
            if (len >= 4 && len <= 5) {
                respuesta = enviarANodo(PUERTO_4_5, operacion, tupla);
            } else if (len == 6) {
                respuesta = enviarANodo(PUERTO_6, operacion, tupla);
            } else if (len >= 1 && len <= 3) {
                respuesta = gestionarReplica(operacion, tupla);
            } else {
                respuesta = "ERROR: Longitud de tupla no válida";
            }
            out.writeObject(respuesta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestiona la lógica de replicación para tuplas pequeñas (1-3 elementos).
     * Pre: Recibe una operación y una tupla de tamaño 1 a 3.
     * Post: Garantiza la escritura en ambos nodos o la lectura con tolerancia a fallos.
     */
    private static Object gestionarReplica(String op, String[] tupla) {
        if (op.equals("PN")) {
            enviarANodo(PUERTO_1_3_PRINCIPAL, op, tupla);
            enviarANodo(PUERTO_1_3_REPLICA, op, tupla);
            return "OK (Guardado en sistema replicado)";
        } else {
            Object resp = enviarANodo(PUERTO_1_3_PRINCIPAL, op, tupla);
            if (resp instanceof String && ((String) resp).startsWith("ERROR_CONEXION")) {
                System.out.println("FALLO EN PRINCIPAL. USANDO REPLICA.");
                return enviarANodo(PUERTO_1_3_REPLICA, op, tupla);
            }
            return resp;
        }
    }

    /**
     * Envía una petición a un nodo de almacenamiento específico.
     * Pre: El puerto debe ser válido y corresponder a un ServidorAlmacen.
     * Post: Devuelve la respuesta del nodo o un mensaje de error si no conecta.
     */
    private static Object enviarANodo(int puerto, String op, String[] tupla) {
        try (Socket s = new Socket("localhost", puerto);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
            out.writeObject(op);
            out.writeObject(tupla);
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return "ERROR_CONEXION";
        }
    }
}