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
            // Inicialización de streams de comunicación
            in = new DataInputStream(cs.getInputStream());
            out = new DataOutputStream(cs.getOutputStream());

            System.out.println("\n[" + nombreCliente + "] ==========================================");
            System.out.println("[" + nombreCliente + "]   CLIENTE: " + nombreCliente);
            System.out.println("[" + nombreCliente + "] ==========================================");

            // PASO 1: Protocolo de inicio - Enviar identificación
            String mensajeInicio = "INICIO COMPRA:" + nombreCliente;
            out.writeUTF(mensajeInicio);
            out.flush();

            String respuesta = in.readUTF();

            if (!respuesta.equals("BIENVENIDO AL SERVICIO")) {
                System.err.println("[" + nombreCliente + "] Error: No se recibió bienvenida del servidor");
                return;
            }

            System.out.println("[" + nombreCliente + "] Conexión establecida. Iniciando reservas...");

            // PASO 2: Bucle de reservas - Intentar reservar plazas
            int plazasReservadas = realizarReservas(in, out);

            // PASO 3: Mostrar resumen final
            mostrarResumen(plazasReservadas);

        } catch (IOException e) {
            System.err.println("[" + nombreCliente + "] Error de comunicación con el servidor: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[" + nombreCliente + "] Error inesperado: " + e.getMessage());
        } finally {
            // Cierre de recursos
            cerrarRecursos(in, out);
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
            // Generar plaza aleatoria (ej: "3B")
            int fila = random.nextInt(4) + 1; // 1-4
            char asiento = asientos[random.nextInt(4)]; // A-D
            String plaza = "" + fila + asiento;

            // Enviar petición de reserva
            String peticion = "RESERVAR:" + plaza;
            out.writeUTF(peticion);
            out.flush();

            String respuesta = in.readUTF();

            if (respuesta.startsWith("RESERVADA:")) {
                // Reserva exitosa
                plazasReservadas++;
                String plazaReservada = respuesta.substring(10);
                System.out.println("[" + nombreCliente + "] ✓ Plaza " + plazaReservada + " reservada (Total: " + plazasReservadas + ")");
            } else if (respuesta.equals("VUELO COMPLETO")) {
                // No quedan plazas disponibles
                System.out.println("[" + nombreCliente + "] Vuelo completo - No hay más plazas disponibles");
                break;
            } else if (respuesta.startsWith("ERROR:")) {
                // Error en la petición
                System.err.println("[" + nombreCliente + "] Error del servidor: " + respuesta.substring(6));
            }
            // Si la plaza está ocupada, se reintenta con otra (sin mostrar mensaje)

            // Pausa breve para simular comportamiento realista
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
        System.out.println("[" + nombreCliente + "] ==========================================");
        System.out.println("[" + nombreCliente + "]   RESUMEN - " + nombreCliente);
        System.out.println("[" + nombreCliente + "] ==========================================");
        System.out.println("[" + nombreCliente + "] Plazas reservadas: " + plazasReservadas);
        System.out.println("[" + nombreCliente + "] ==========================================");
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
            System.out.println("[" + nombreCliente + "] Desconectado del servidor\n");
        } catch (IOException e) {
            System.err.println("[" + nombreCliente + "] Error al cerrar conexión: " + e.getMessage());
        }
    }
}

