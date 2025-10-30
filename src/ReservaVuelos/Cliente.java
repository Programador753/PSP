package ReservaVuelos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Cliente del sistema de reservas de vuelos.
 * Intenta reservar el máximo número de plazas posible de forma automática.
 */
public class Cliente extends Conexion {
    private static final Object CONSOLE_LOCK = new Object();
    private final String nombreCliente;

    /**
     * Pre: nombre != null y no vacío
     * Post: Se crea un cliente conectado al servidor con el nombre especificado
     *
     * @param nombre Nombre identificador del cliente
     */
    public Cliente(String nombre) throws IOException {
        super("cliente");
        this.nombreCliente = nombre;
    }

    /**
     * Pre: Debe estar conectado al servidor
     * Post: El cliente intenta reservar plazas hasta que el vuelo esté completo.
     *       Se muestra un resumen con el número de plazas reservadas.
     *       Todos los recursos se cierran correctamente.
     */
    public void startClient() {
        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            in = new DataInputStream(cs.getInputStream());
            out = new DataOutputStream(cs.getOutputStream());

            imprimirEncabezado();

            String mensajeInicio = "INICIO COMPRA:" + nombreCliente;
            out.writeUTF(mensajeInicio);
            out.flush();

            String respuesta = in.readUTF();

            if (!respuesta.equals("BIENVENIDO AL SERVICIO")) {
                synchronized (CONSOLE_LOCK) {
                    System.err.println("[" + nombreCliente + "] Error: No se recibió bienvenida del servidor");
                }
                return;
            }

            synchronized (CONSOLE_LOCK) {
                System.out.println("[" + nombreCliente + "] Conexión establecida. Iniciando reservas...");
            }

            int plazasReservadas = realizarReservas(in, out);

            mostrarResumen(plazasReservadas);

        } catch (IOException e) {
            synchronized (CONSOLE_LOCK) {
                System.err.println("[" + nombreCliente + "] Error de comunicación con el servidor: " + e.getMessage());
            }
        } catch (Exception e) {
            synchronized (CONSOLE_LOCK) {
                System.err.println("[" + nombreCliente + "] Error inesperado: " + e.getMessage());
            }
        } finally {
            cerrarRecursos(in, out);
        }
    }

    /**
     * Pre: ninguna
     * Post: Imprime el encabezado del cliente de forma sincronizada
     */
    private void imprimirEncabezado() {
        synchronized (CONSOLE_LOCK) {
            System.out.println();
            System.out.println("[" + nombreCliente + "] ==========================================");
            System.out.println("[" + nombreCliente + "]   CLIENTE: " + nombreCliente);
            System.out.println("[" + nombreCliente + "] ==========================================");
        }
    }

    /**
     * Pre: in != null, out != null
     * Post: Intenta reservar plazas hasta que el vuelo esté completo.
     *       Retorna el número de plazas reservadas exitosamente.
     *
     * @param in Stream de entrada para recibir respuestas del servidor
     * @param out Stream de salida para enviar peticiones al servidor
     */
    private int realizarReservas(DataInputStream in, DataOutputStream out)
            throws IOException, InterruptedException {

        int plazasReservadas = 0;
        Random random = new Random();
        char[] asientos = {'A', 'B', 'C', 'D'};

        while (true) {
            int fila = random.nextInt(4) + 1;
            char asiento = asientos[random.nextInt(4)];
            String plaza = "" + fila + asiento;

            String peticion = "RESERVAR:" + plaza;
            out.writeUTF(peticion);
            out.flush();

            String respuesta = in.readUTF();

            if (respuesta.startsWith("RESERVADA:")) {
                plazasReservadas++;
                String plazaReservada = respuesta.substring(10);
                synchronized (CONSOLE_LOCK) {
                    System.out.println("[" + nombreCliente + "] ✓ Plaza " + plazaReservada + " reservada (Total: " + plazasReservadas + ")");
                }
            } else if (respuesta.equals("VUELO COMPLETO")) {
                synchronized (CONSOLE_LOCK) {
                    System.out.println("[" + nombreCliente + "] Vuelo completo - No hay más plazas disponibles");
                }
                break;
            } else if (respuesta.startsWith("ERROR:")) {
                synchronized (CONSOLE_LOCK) {
                    System.err.println("[" + nombreCliente + "] Error del servidor: " + respuesta.substring(6));
                }
            }

            Thread.sleep(100);
        }

        return plazasReservadas;
    }

    /**
     * Pre: plazasReservadas >= 0
     * Post: Muestra en pantalla un resumen con el número de plazas reservadas
     *
     * @param plazasReservadas Número de plazas reservadas por el cliente
     */
    private void mostrarResumen(int plazasReservadas) {
        synchronized (CONSOLE_LOCK) {
            System.out.println("[" + nombreCliente + "] ==========================================");
            System.out.println("[" + nombreCliente + "]   RESUMEN - " + nombreCliente);
            System.out.println("[" + nombreCliente + "] ==========================================");
            System.out.println("[" + nombreCliente + "] Plazas reservadas: " + plazasReservadas);
            System.out.println("[" + nombreCliente + "] ==========================================");
        }
    }

    /**
     * Pre: Los recursos pueden ser null o estar abiertos
     * Post: Todos los recursos se cierran de forma segura. Si ocurre un error
     *       durante el cierre, se informa al usuario.
     *
     * @param in Stream de entrada a cerrar
     * @param out Stream de salida a cerrar
     */
    private void cerrarRecursos(DataInputStream in, DataOutputStream out) {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (cs != null && !cs.isClosed()) {
                cs.close();
            }
            synchronized (CONSOLE_LOCK) {
                System.out.println("[" + nombreCliente + "] Desconectado del servidor\n");
            }
        } catch (IOException e) {
            synchronized (CONSOLE_LOCK) {
                System.err.println("[" + nombreCliente + "] Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}

