package ChatDistribuido.cliente;
import ChatDistribuido.comun.Mensaje;
import ChatDistribuido.comun.Seguridad;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

/**
 * Clase principal del Cliente.
 * Gestiona claves RSA, entrada de usuario y descifrado de mensajes.
 */
public class ClienteChat {
    private static ObjectOutputStream out;
    private static boolean conectado = true;
    private static PublicKey clavePublicaServidor;
    private static PrivateKey clavePrivadaPropia;
    private static boolean enSala = false;

    /**
     * Método principal del cliente.
     * Pre: Ninguna.
     * Post: Inicia conexión, handshake y bucle de chat.
     */
    public static void main(String[] args) {
        try {
            // Generar claves propias
            KeyPair misClaves = Seguridad.generarClaves();
            clavePrivadaPropia = misClaves.getPrivate();
            PublicKey clavePublicaPropia = misClaves.getPublic();

            Socket socket = new Socket("localhost", 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // 1. Handshake: Recibir clave del servidor
            Mensaje msjServerKey = (Mensaje) in.readObject();
            if (msjServerKey.getTipo().equals("CLAVE_PUBLICA")) {
                clavePublicaServidor = Seguridad.stringAClavePublica(msjServerKey.getContenido());
            }

            // 2. Handshake: Enviar mi clave pública
            String miClaveStr = Seguridad.clavePublicaAString(clavePublicaPropia);
            out.writeObject(new Mensaje("CLAVE_PUBLICA", miClaveStr, "CLIENTE"));
            out.flush();

            System.out.println("Conexión segura establecida (RSA).");

            // Hilo lector
            Thread lector = new Thread(() -> {
                try {
                    while (conectado) {
                        Mensaje msj = (Mensaje) in.readObject();

                        // Si es mensaje o info cifrada, descifrar
                        if (msj.getTipo().equals("MENSAJE") || msj.getTipo().equals("INFO_CIFRADO")) {
                            String textoMostrar = Seguridad.descifrar(msj.getContenido(), clavePrivadaPropia);

                            // Detectar si nos unimos a una sala
                            if (msj.getTipo().equals("INFO_CIFRADO")) {
                                if (textoMostrar.contains("Sala creada con ID:") ||
                                    textoMostrar.contains("Te has unido a la sala")) {
                                    enSala = true;
                                    System.out.println("\n[SISTEMA]: " + textoMostrar);
                                    System.out.println("[SISTEMA]: Ahora puedes escribir mensajes (máx. 140 caracteres)");
                                    System.out.println("[SISTEMA]: Escribe /disconnect para salir de la sala");
                                } else if (textoMostrar.contains("ERROR:")) {
                                    System.out.println("\n[ERROR]: " + textoMostrar);
                                } else {
                                    System.out.println("\n[SISTEMA]: " + textoMostrar);
                                }
                            } else {
                                // Mensaje de chat
                                System.out.println("\n>> " + textoMostrar);
                            }
                        }

                        // Mostrar prompt apropiado
                        if (enSala) {
                            System.out.print("Tú: ");
                        }
                    }
                } catch (Exception e) {
                    if (conectado) {
                        System.out.println("\nConexión perdida con el servidor.");
                    }
                    conectado = false;
                }
            });
            lector.setDaemon(true);
            lector.start();

            Scanner sc = new Scanner(System.in);
            System.out.println("Introduce tu usuario:");
            String usuario = sc.nextLine();
            buclePrincipal(sc, usuario);
            socket.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Bucle principal para leer comandos.
     * Pre: Conexión establecida.
     * Post: Envía comandos o mensajes cifrados.
     */
    private static void buclePrincipal(Scanner sc, String usuario) throws Exception {
        while (conectado) {
            // Mostrar menú solo si no está en sala
            if (!enSala) {
                mostrarMenuPrincipal();
            }

            String input = sc.nextLine().trim();

            if (input.equals("/disconnect")) {
                out.writeObject(new Mensaje("DESCONECTAR", "", usuario));
                out.flush();
                conectado = false;
                enSala = false;
                System.out.println("Desconectando...");
            } else if (!enSala) {
                // Usuario no está en sala, procesar comandos del menú
                procesarComandoMenu(input, sc, usuario);
            } else {
                // Usuario está en sala, enviar mensaje de chat
                if (input.isEmpty()) {
                    continue;
                }

                if (input.length() <= 140) {
                    // CIFRAR mensaje antes de enviar
                    String cifrado = Seguridad.cifrar(input, clavePublicaServidor);
                    out.writeObject(new Mensaje("MENSAJE", cifrado, usuario));
                    out.flush();
                } else {
                    System.out.println("[ERROR]: El mensaje excede 140 caracteres (" + input.length() + " caracteres).");
                    System.out.print("Tú: ");
                }
            }
        }
    }

    /**
     * Muestra el menú principal de opciones.
     * Pre: El usuario no está en ninguna sala.
     * Post: Muestra las opciones disponibles.
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║       CHAT DISTRIBUIDO - MENÚ      ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("1. Crear sala");
        System.out.println("2. Unirse a sala");
        System.out.println("3. Listar salas disponibles");
        System.out.println("/disconnect - Salir del servidor");
        System.out.print("\nSelecciona una opción: ");
    }

    /**
     * Procesa los comandos del menú principal.
     * Pre: El usuario no está en ninguna sala.
     * Post: Envía el comando correspondiente al servidor (cifrado si es necesario).
     */
    private static void procesarComandoMenu(String opcion, Scanner sc, String usuario) throws Exception {
        switch (opcion) {
            case "1":
                System.out.print("\nNombre de la sala: ");
                String nombreSala = sc.nextLine().trim();

                if (nombreSala.isEmpty()) {
                    System.out.println("[ERROR]: El nombre de la sala no puede estar vacío.");
                    break;
                }

                System.out.print("¿Desea proteger con contraseña? (S/N): ");
                String respuesta = sc.nextLine().trim().toUpperCase();
                String datosSala;

                if (respuesta.equals("S")) {
                    System.out.print("Contraseña: ");
                    String password = sc.nextLine().trim();
                    datosSala = nombreSala + ";" + password;
                } else {
                    datosSala = nombreSala + ";";
                }

                // Cifrar datos de la sala antes de enviar
                String datosCifrados = Seguridad.cifrar(datosSala, clavePublicaServidor);
                out.writeObject(new Mensaje("CREAR_SALA", datosCifrados, usuario));
                out.flush();
                System.out.println("Creando sala...");
                break;

            case "2":
                System.out.print("\nID de la sala: ");
                String idSala = sc.nextLine().trim();

                if (idSala.isEmpty()) {
                    System.out.println("[ERROR]: El ID de la sala no puede estar vacío.");
                    break;
                }

                System.out.print("Contraseña (dejar vacío si no tiene): ");
                String passwordUnir = sc.nextLine().trim();
                String datosUnir = idSala + ";" + passwordUnir;

                // Cifrar datos antes de enviar
                String datosUnirCifrados = Seguridad.cifrar(datosUnir, clavePublicaServidor);
                out.writeObject(new Mensaje("UNIR_SALA", datosUnirCifrados, usuario));
                out.flush();
                System.out.println("Intentando unirse...");
                break;

            case "3":
                out.writeObject(new Mensaje("LISTAR", "", usuario));
                out.flush();
                break;

            default:
                System.out.println("[ERROR]: Opción inválida. Por favor, selecciona 1, 2, 3 o /disconnect.");
                break;
        }
    }
}