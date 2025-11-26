package Examen1_EV;

import java.util.Scanner;
import java.util.Random;

/**
 * Clase principal del juego de cartas.
 *
 * @author Antonio
 * @version 1.0
 */
public class Main {
    private static final int TAMAÑO_MANO = 4;

    /**
     * Pre: ---
     * Post: Inicia el juego y espera a que el usuario elija una carta.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Inicializar mazo y barajar.
        Mazo mazo = inicializarMazo();
        Mano mano = new Mano(TAMAÑO_MANO);
        Elixir hiloElixir = new Elixir();

        // Llenar mano inicial.
        for (int i = 0; i < TAMAÑO_MANO; i++) {
            mano.setCarta(i, mazo.desapilar());
        }

        // Iniciar hilo de elixir.
        hiloElixir.start();

        System.out.println("¡Bienvenido al Simulador de Batalla!");
        System.out.println("El elixir se genera automáticamente cada 2 segundos.\n");

        // Bucle principal del juego.
        while (!juegoTerminado(mazo, mano)) {
            mostrarEstado(hiloElixir, mano);
            System.out.print("Elige una carta (0-3): ");

            try {
                int indice = scanner.nextInt();

                if (indice < 0 || indice >= TAMAÑO_MANO || mano.getCarta(indice) == null) {
                    System.out.println("Índice no válido. Inténtalo de nuevo.\n");
                    continue;
                }

                Carta cartaSeleccionada = mano.getCarta(indice);

                if (hiloElixir.consumir(cartaSeleccionada.getCoste())) {
                    System.out.println("¡Carta jugada: " + cartaSeleccionada.getNombre() + "!\n");
                    mano.setCarta(indice, mazo.desapilar());
                } else {
                    System.out.println("No tienes suficiente elixir. Necesitas " +
                            cartaSeleccionada.getCoste() + " pero solo tienes " +
                            hiloElixir.getCantidad() + "\n");
                }

            } catch (Exception e) {
                System.out.println("Entrada no válida.\n");
                scanner.nextLine();
            }
        }

        // Fin del juego
        System.out.println("\n¡PARTIDA TERMINADA! Has jugado todas las cartas.");
        hiloElixir.detener();
        scanner.close();
    }

    /**
     * Inicializa y baraja el mazo.
     */
    private static Mazo inicializarMazo() {
        Carta[] cartasIniciales = {
                new Carta(3, "Caballero"),
                new Carta(2, "Arquera"),
                new Carta(5, "Gigante"),
                new Carta(4, "Mago"),
                new Carta(1, "Esqueletos"),
                new Carta(4, "Dragon"),
                new Carta(4, "Valquiria"),
                new Carta(8, "Golem"),
                new Carta(3, "Tornado"),
                new Carta(4, "Bola de Fuego")
        };

        // Barajar.
        Random random = new Random();
        for (int i = cartasIniciales.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Carta temp = cartasIniciales[i];
            cartasIniciales[i] = cartasIniciales[j];
            cartasIniciales[j] = temp;
        }

        // Crear mazo.
        Mazo mazo = new Mazo(cartasIniciales.length);
        for (Carta carta : cartasIniciales) {
            mazo.apilar(carta);
        }

        return mazo;
    }

    /**
     * Muestra el estado actual del juego.
     */
    private static void mostrarEstado(Elixir hiloElixir, Mano mano) {
        System.out.println("=== ESTADO DEL JUEGO ===");
        System.out.println("Elixir disponible: " + hiloElixir.getCantidad());
        System.out.println("\nTu mano:");
        mano.mostrar();
        System.out.println();
    }

    /**
     * Verifica si el juego ha terminado.
     */
    private static boolean juegoTerminado(Mazo mazo, Mano mano) {
        return mazo.estaVacio() && mano.estaVacia();
    }
}
