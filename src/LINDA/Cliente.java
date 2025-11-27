package LINDA;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Cliente de consola para interactuar con el sistema Linda.
 * Permite enviar operaciones PostNote, RemoveNote y ReadNote.
 */
public class Cliente {
    /**
     * Método principal que ejecuta la interfaz de usuario.
     * Pre: El servidor Linda debe estar corriendo en localhost:8080.
     * Post: Mantiene la aplicación abierta hasta que el usuario decida salir (forzoso).
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("--- CLIENTE LINDA ---");
            System.out.println("1. PostNote (PN)");
            System.out.println("2. RemoveNote (RN)");
            System.out.println("3. ReadNote (ReadN)");
            System.out.print("Elige opcion: ");
            int opc = Integer.parseInt(sc.nextLine());
            String cmd = (opc == 1) ? "PN" : (opc == 2) ? "RN" : "ReadN";
            System.out.print("Introduce tupla separada por comas (ej. A,20,?X): ");
            String[] tupla = sc.nextLine().split(",");
            try (Socket s = new Socket("10.10.1.124", 8080);
                 ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
                out.writeObject(cmd);
                out.writeObject(tupla);
                Object resp = in.readObject();
                if (resp instanceof String[]) {
                    System.out.println("Respuesta Tupla: " + java.util.Arrays.toString((String[]) resp));
                } else {
                    System.out.println("Respuesta Servidor: " + resp);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}