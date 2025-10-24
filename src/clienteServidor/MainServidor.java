package clienteServidor;

import java.io.IOException;

//Clase principal que hará uso del servidor
public class MainServidor {
  public static void main(String[] args) throws IOException {
      if (args.length != 1) {
          System.out.println("Uso: java clienteServidor.MainServidor <puerto>");
          System.out.println("Ejemplo: java clienteServidor.MainServidor 6000");
          System.exit(1);
      }

      try {
          int puerto = Integer.parseInt(args[0]);
          Servidor serv = new Servidor(puerto); //Se crea el servidor

          System.out.println("Iniciando servidor en puerto " + puerto + "\n");
          serv.startServer(); //Se inicia el servidor
          System.out.println("Servidor finalizado");
      } catch (NumberFormatException e) {
          System.out.println("Error: El puerto debe ser un número válido");
          System.exit(1);
      }
  }
}