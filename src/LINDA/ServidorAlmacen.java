package LINDA;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Servidor encargado del almacenamiento de tuplas en memoria.
 * Escucha peticiones de escritura y lectura en un puerto específico.
 */
public class ServidorAlmacen {
    /** Lista segura para hilos que actúa como base de datos en memoria. */
    private static List<String[]> almacen = Collections.synchronizedList(new ArrayList<>());

    /**
     * Método principal que inicia el servidor de almacenamiento.
     * Pre: El usuario debe introducir un puerto válido por consola.
     * Post: El servidor queda en bucle infinito aceptando conexiones.
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce el PUERTO para este servidor (ej. 9001): ");
        int puerto = sc.nextInt();
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor ALMACEN escuchando en puerto " + puerto);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> manejarPeticion(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestiona la petición de un cliente conectado.
     * Pre: El socket debe estar conectado y enviar el protocolo correcto.
     * Post: Realiza la operación (PN, RN, ReadN) y devuelve la respuesta al socket.
     */
    private static void manejarPeticion(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            String operacion = (String) in.readObject();
            String[] datos = (String[]) in.readObject();
            System.out.println("Recibido: " + operacion + " " + Arrays.toString(datos));
            if (operacion.equals("PN")) {
                almacen.add(datos);
                out.writeObject("OK");
            } else if (operacion.equals("RN") || operacion.equals("ReadN")) {
                String[] encontrado = null;
                synchronized (almacen) {
                    Iterator<String[]> it = almacen.iterator();
                    while (it.hasNext()) {
                        String[] t = it.next();
                        if (LindaDriver.coincide(t, datos)) {
                            encontrado = t;
                            if (operacion.equals("RN")) {
                                it.remove();
                            }
                            break;
                        }
                    }
                }
                out.writeObject(encontrado);
            }
        } catch (Exception e) {
            System.err.println("Error procesando petición: " + e.getMessage());
        }
    }
}