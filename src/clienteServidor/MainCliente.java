package clienteServidor;

import java.io.IOException;

//Clase principal que hará uso del cliente
public class MainCliente {
  public static void main(String[] args) throws IOException {
      if (args.length != 2) {
          System.out.println("Uso: java clienteServidor.MainCliente <IP_servidor> <puerto>");
          System.out.println("Ejemplo: java clienteServidor.MainCliente 192.168.1.100 6000");
          System.out.println("Para localhost: java clienteServidor.MainCliente localhost 6000");
          System.exit(1);
      }

      String ipServidor = args[0];
      try {
          int puerto = Integer.parseInt(args[1]);
          Cliente cli = new Cliente(ipServidor, puerto); //Se crea el cliente

          System.out.println("Conectando a " + ipServidor + ":" + puerto + "\n");
          cli.startClient(); //Se inicia el cliente
      } catch (NumberFormatException e) {
          System.out.println("Error: El puerto debe ser un número válido");
          System.exit(1);
      } catch (IOException e) {
          System.out.println("Error de conexión: " + e.getMessage());
          System.exit(1);
      }
  }
}