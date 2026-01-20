package ChatDistribuido.servidor;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una sala de chat en el servidor.
 * Almacena información de conexión y usuarios.
 */
public class Sala {
    private String nombre;
    private String idUnico;
    private String password;
    private List<HiloServidor> usuariosConectados;

    /**
     * Constructor de la Sala.
     * Pre: Nombre e idUnico son obligatorios.
     * Post: Se inicializa la sala sin usuarios.
     */
    public Sala(String nombre, String idUnico, String password) {
        this.nombre = nombre;
        this.idUnico = idUnico;
        this.password = password;
        this.usuariosConectados = new ArrayList<>();
    }

    /**
     * Añade un usuario a la lista de la sala.
     * Pre: El usuario no debe ser nulo.
     * Post: El usuario es añadido a la lista.
     */
    public void agregarUsuario(HiloServidor usuario) {
        this.usuariosConectados.add(usuario);
    }

    /**
     * Elimina un usuario de la sala.
     * Pre: El usuario debe existir en la lista.
     * Post: El usuario es eliminado.
     */
    public void eliminarUsuario(HiloServidor usuario) {
        this.usuariosConectados.remove(usuario);
    }

    /**
     * Obtiene la lista de usuarios.
     * Pre: Ninguna.
     * Post: Devuelve la lista de hilos conectados.
     */
    public List<HiloServidor> getUsuarios() {
        return usuariosConectados;
    }

    /**
     * Valida si la contraseña es correcta.
     * Pre: pass puede ser null.
     * Post: Devuelve true si coincide o si la sala no tiene password.
     */
    public boolean validarPassword(String pass) {
        if (this.password == null || this.password.isEmpty()) return true;
        return this.password.equals(pass);
    }

    public String getIdUnico() { return idUnico; }
    public String getNombre() { return nombre; }
}