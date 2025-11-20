package practica1;

public class Main2 {

	/**
	 * Pre: ---
	 * Post: crea 3 hilos distintos, cada uno con sus atributos
	 * 		correspondientes y los ejecuta. Podemos observar
	 * 		la ejecución entrelazada.
	 */
	public static void main(String[] args) {
		/*
		 * Creación de los hilos
		 */
		Thread2[] t = new Thread2[3];
		t[0] = new Thread2("Soy A", 100, 10);
		t[1] = new Thread2("Soy B", 150, 15);
		t[2] = new Thread2("Soy C", 300, 5);
		/*
		 * Inicialización de los hilos
		 */
		t[0].start();
		t[1].start();
		t[2].start();
		/*
		 * Esperamos a que terminen los hilos
		 */
		try {
			t[0].join();
			t[1].join();
			t[2].join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Fin");
	}
}
