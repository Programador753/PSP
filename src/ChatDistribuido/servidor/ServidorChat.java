package ChatDistribuido.servidor;
import ChatDistribuido.comun.Seguridad;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal del Servidor.
 * Gestiona conexiones y claves RSA del servidor.
 */
public class ServidorChat {
    public static Map<String, Sala> mapaSalas = new HashMap<>();
    public static PublicKey clavePublicaServidor;
    public static PrivateKey clavePrivadaServidor;

    /**
     * Método principal que inicia el servidor.
     * Pre: Ninguna.
     * Post: El servidor inicia y genera sus claves RSA.
     */
    public static void main(String[] args) {
        int puerto = 5000;
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            // Generar claves RSA del servidor al inicio
            KeyPair claves = Seguridad.generarClaves();
            clavePublicaServidor = claves.getPublic();
            clavePrivadaServidor = claves.getPrivate();

            System.out.println("Servidor iniciado en puerto " + puerto);
            System.out.println("Claves RSA generadas correctamente.");

            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Nuevo cliente conectado.");
                HiloServidor hilo = new HiloServidor(socketCliente);
                hilo.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}