package practica1;

public class Thread1 extends Thread {
	private String mensaje; // Variable que contiene el mensaje a imprimir
	private int retardo; // Variable que indica el tiempo de espera
	private int veces; // Var
	
	public Thread1(String mensaje, int retardo, int veces) {
		this.mensaje = mensaje;
		this.retardo = retardo;
		this.veces = veces;
	}
	
	/**
	 * Pre: ---
	 * Post: el metodo run() contiene el codigo a ejecutar por
	 * 		parte del hilo.
	 */
	public void run() {
		for(int i = 1; i <= veces; i++) {
			System.out.println(mensaje);
			try {
				/*
				 * Paraliza le ejecucion del hilo 
				 * [retardo] milisegundos
				 */
				Thread.sleep(retardo);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
