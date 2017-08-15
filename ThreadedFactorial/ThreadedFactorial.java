public class ThreadedFactorial {
	//ABNTODO: fractional values when input not perfectly divisible by cores

	public static void main(String[] args) {
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService threadManager = Executors.newFixedThreadPool(cores);
		// threadManager.submit(implementedRunnable);
		Scanner sc = new Scanner(System.in);
		double target = sc.nextDouble();
		double opsPerThread = target/cores;
		MultiplicationThread[] threads = new MultiplicationThread[cores]; //???
		for (int i=cores-1;i>=0;i--) {
			threads[i] = new MultiplicationThread(opsPerThread*(i), opsPerThread*(i+1));
			threadManager.submit(threads[i]);
		}
		threads[cores-1].complete.wait();

		double result = 1;
		for (int i=cores;i>=0;i--) {
			while(!threads[i].complete) {
			//ABNTODO: may cause race/infinite wait when complete is made true and notified just before wait begins
				threads[i].complete.wait();
			}
			result*=threads[i].result;
		}
		System.out.println(result);
	}

	private static void completionListener() {
		double result = 1;
		for (int i=cores;i>=0;i--) {
			if(!threads[i].complete) {
				return;
			}
			result*=threads[i].result;
		}
		System.out.println(result);
	}
}

public class MultiplicationThread implements Runnable {
	private double from;
	private double to;
	public synchronized double result;
	public synchronized boolean complete;
	public MultiplicationThread(double from, double to) {
		this.complete = false;
		if(to>=from) {
			this.from = from;
			this.to = to;
		}
		else {
			this.to = from;
			this.from = to;	
		}
	}

	@override
	public void run() {
		result = from;
		for (i=from+1;i<=to;i++) {
			result*=i;
		}
		complete = true;
		complete.notify();
	}
}
