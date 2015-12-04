package main;

public class ThreeThreads {

	public ThreeThreads() {
		System.out.println("first line");
		MyLoopingThread mlt1 = new MyLoopingThread('a');
		MyLoopingThread mlt2 = new MyLoopingThread('b');
		MyLoopingThread mlt3 = new MyLoopingThread('c');
		mlt1.start();
		mlt2.start();
		mlt1.interrupt();
		try {
			mlt2.join();
		} catch (InterruptedException ie) {
			System.out.println("IE: " + ie.getMessage());
		}
		mlt3.start();
		System.out.println("last line");
	}

	public static void main(String [] args) {
		new ThreeThreads();
	}
}

class MyLoopingThread extends Thread {
	private char c;
	public MyLoopingThread(char c) {
		this.c = c;
	}

	public void run() {
		for (int i=0; i < 20; i++) {
			System.out.print(c + "" + i + " ");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				System.out.println("IE: " + ie.getMessage());
				return;
			}
		}
		System.out.println();
	}
}
