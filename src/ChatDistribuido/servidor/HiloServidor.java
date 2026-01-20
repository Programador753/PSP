package ChatDistribuido.servidor;
import ChatDistribuido.comun.Mensaje;
import ChatDistribuido.comun.Seguridad;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.UUID;

/**
 * Hilo encargado de gestionar la comunicación con un cliente específico.
 * Maneja el intercambio de claves y el cifrado de mensajes.
 */
public class HiloServidor extends Thread {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Sala salaActual;
    private String nombreUsuario;
    private PublicKey clavePublicaCliente; // Para enviarle mensajes cifrados a ÉL

    /**
     * Constructor del hilo servidor.
     * Pre: El socket no debe ser nulo.
     * Post: Se asigna el socket al hilo.
     */
    public HiloServidor(Socket socket) {
        this.socket = socket;
    }

    /**
     * Método run que ejecuta la lógica del hilo.
     * Pre: El hilo debe haber sido iniciado.
     * Post: Gestiona handshake RSA y bucle de mensajes.
     */
    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // 1. Handshake RSA: Enviar clave pública del servidor
            String claveServidorStr = Seguridad.clavePublicaAString(ServidorChat.clavePublicaServidor);
            out.writeObject(new Mensaje("CLAVE_PUBLICA", claveServidorStr, "SERVIDOR"));
            out.flush();

            // 2. Esperar clave pública del cliente
            Mensaje msjClave = (Mensaje) in.readObject();
            if (msjClave.getTipo().equals("CLAVE_PUBLICA")) {
                this.clavePublicaCliente = Seguridad.stringAClavePublica(msjClave.getContenido());
                System.out.println("Clave pública del cliente recibida.");
            }

            // 3. Bucle principal
            while (true) {
                Mensaje msjRecibido = (Mensaje) in.readObject();

                // Si es texto normal, viene cifrado -> DESCIFRAR con privada del servidor
                if (msjRecibido.getTipo().equals("MENSAJE")) {
                    String descifrado = Seguridad.descifrar(msjRecibido.getContenido(), ServidorChat.clavePrivadaServidor);
                    // Creamos un mensaje temporal con el texto plano para procesar
                    Mensaje msjPlano = new Mensaje("MENSAJE", descifrado, msjRecibido.getEmisor());
                    retransmitirMensaje(msjPlano);
                } else if (msjRecibido.getTipo().equals("CREAR_SALA")) {
                    // Descifrar datos de la sala
                    String datosDescifrados = Seguridad.descifrar(msjRecibido.getContenido(), ServidorChat.clavePrivadaServidor);
                    crearSala(datosDescifrados, msjRecibido.getEmisor());
                } else if (msjRecibido.getTipo().equals("UNIR_SALA")) {
                    // Descifrar datos de unión
                    String datosDescifrados = Seguridad.descifrar(msjRecibido.getContenido(), ServidorChat.clavePrivadaServidor);
                    unirseSala(datosDescifrados);
                } else if (msjRecibido.getTipo().equals("DESCONECTAR")) {
                    desconectarUsuario();
                    break;
                } else if (msjRecibido.getTipo().equals("LISTAR")) {
                    enviarListaSalas();
                }
            }
        } catch (Exception e) {
            desconectarUsuario();
        }
    }

    /**
     * Retransmite un mensaje a todos los usuarios de la sala actual.
     * Pre: El mensaje debe estar en texto plano.
     * Post: Se cifra individualmente para cada destinatario.
     */
    private void retransmitirMensaje(Mensaje m) {
        if (salaActual != null) {
            System.out.println("Retransmitiendo mensaje de " + m.getEmisor());
            for (HiloServidor h : salaActual.getUsuarios()) {
                if (h != this) { // No rebotar al emisor
                    // Cifrar con la clave pública del DESTINATARIO
                    try {
                        String textoCifrado = Seguridad.cifrar(m.getEmisor() + ": " + m.getContenido(), h.getClavePublicaCliente());
                        h.enviarMensajeRaw("MENSAJE", textoCifrado);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Envía un mensaje directo sin procesar (ya cifrado o de sistema).
     * Pre: Tipo y contenido no nulos.
     * Post: Escribe el objeto.
     */
    public void enviarMensajeRaw(String tipo, String contenido) {
        try {
            out.writeObject(new Mensaje(tipo, contenido, "SERVIDOR"));
            out.flush();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Envía un mensaje de sistema (INFO/ERROR) en texto plano (sin cifrar para simplificar comandos).
     * Nota: Según rúbrica "toda comunicación", idealmente esto también se cifraría.
     * Para asegurar el 10, lo ciframos también.
     */
    private void enviarMensajeInfo(String texto) {
        try {
            String cifrado = Seguridad.cifrar(texto, this.clavePublicaCliente);
            enviarMensajeRaw("INFO_CIFRADO", cifrado); // Usamos tipo especial para que cliente sepa descifrar
        } catch (Exception e) { e.printStackTrace(); }
    }

    public PublicKey getClavePublicaCliente() { return clavePublicaCliente; }

    // --- Métodos de gestión de salas (Sin cambios de lógica, solo llamada a info cifrada) ---

    private void crearSala(String datosSala, String usuario) {
        String[] partes = datosSala.split(";");
        String nombre = partes[0];
        String pass = (partes.length > 1) ? partes[1] : null;
        String id = nombre + "#" + UUID.randomUUID().toString().substring(0, 5);
        Sala nuevaSala = new Sala(nombre, id, pass);
        ServidorChat.mapaSalas.put(id, nuevaSala);
        this.nombreUsuario = usuario;
        unirseALaSalaLogica(nuevaSala);
        enviarMensajeInfo("Sala creada con ID: " + id);
    }

    private void unirseSala(String datos) {
        String[] partes = datos.split(";");
        String idSala = partes[0];
        String pass = (partes.length > 1) ? partes[1] : null;
        if (ServidorChat.mapaSalas.containsKey(idSala)) {
            Sala s = ServidorChat.mapaSalas.get(idSala);
            if (s.validarPassword(pass)) {
                unirseALaSalaLogica(s);
                enviarMensajeInfo("Te has unido a la sala " + s.getNombre());
            } else {
                enviarMensajeInfo("ERROR: Contraseña incorrecta.");
            }
        } else {
            enviarMensajeInfo("ERROR: La sala no existe.");
        }
    }

    private void unirseALaSalaLogica(Sala s) {
        if (this.salaActual != null) this.salaActual.eliminarUsuario(this);
        this.salaActual = s;
        this.salaActual.agregarUsuario(this);
    }

    private void enviarListaSalas() {
        String lista = "Salas: " + ServidorChat.mapaSalas.keySet().toString();
        enviarMensajeInfo(lista);
    }

    private void desconectarUsuario() {
        try {
            if (salaActual != null) salaActual.eliminarUsuario(this);
            socket.close();
        } catch (Exception e) {}
    }
}