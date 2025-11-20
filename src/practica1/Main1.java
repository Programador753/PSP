package practica1;

public class Main1 {

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
		Thread1 t1 = new Thread1("Soy A", 100, 10);
		Thread1 t2 = new Thread1("Soy B", 150, 15);
		Thread1 t3 = new Thread1("Soy C", 300, 5);
		/*
		 * Inicialización de los hilos
		 */
		t1.start();
		t2.start();
		t3.start();
		/*
		 * Esperamos a que terminen los hilos
		 */
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Fin");
	}
}
