public class Mutex {
	private volatile boolean isTaken = false;

	public synchronized void acquire() { // compare
		while (isTaken) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isTaken = true;
	}

	public synchronized void release() {
		isTaken = false;
		this.notify();
	}

}
