package Blockchain;

public class HashUtil {

    /**
     * Método estático que calcula el hash SHA-256 de una cadena de texto dada.
     * Pre: El parámetro input debe ser una cadena de texto válida.
     * Post: Retorna una cadena de texto que representa el hash SHA-256 del input, útil para identificar bloques y transacciones.
     */
    public static String calcularHash(String input) {

        try {
            //Pedimos usar el motor SHA-256 para generar el hash
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");

            //Creamos una lista de bytes el cual pasa el texto a bytes y el algoritmo digest lo procesa para hacer el hash
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            //Creamos una herramienta para poder traducir bytes a texto legible
            StringBuilder hexString = new StringBuilder();

            //revisa cada byte del paso anterior
            for (byte b : hash) {

                //Transforma cada byte a su texto hexadecimal | 0-9 y de la a-f
                String hex = Integer.toHexString(0xff & b);

                //Si solo da un caracter le escribe un 0 delante para que no se descuadre la longitud del hash
                if (hex.length() == 1) hexString.append('0');

                //Pega el texto final en la cadena de texto
                hexString.append(hex);
            }

            //Convierte todo a texto y lo devuelve
            return hexString.toString();

        } catch (Exception e) {
            //ERROR
            throw new RuntimeException(e);
        }
    }
}