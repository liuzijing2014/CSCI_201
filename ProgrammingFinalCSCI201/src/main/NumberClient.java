package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class NumberClient {
	private static Lock lock = new ReentrantLock();
	public NumberClient(int num) {
		try {
			Socket s = new Socket("localhost", 6789);
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			pw.println(num);
			pw.flush();
			lock.lock();
			try {
				ArrayList<Integer> numbers = (ArrayList<Integer>)ois.readObject();
				for (int i=0; i < numbers.size(); i++) {
					System.out.print(numbers.get(i));
					if (i < numbers.size() - 1) {
						System.out.print(",");
					}
				}
				System.out.println();
			} finally {
				lock.unlock();
			}
			pw.close();
			ois.close();
			s.close();
		} catch (IOException ioe) {
			System.out.println("IOE in NumberClient constructor: " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("CNFE in NumberClient constructor: " + cnfe.getMessage());
		}
	}
	public static void main(String [] args) {
		ExecutorService executors = Executors.newCachedThreadPool();
		for (int i=0; i < 100; i++) {
			System.out.println("Adding " + i);
			executors.execute(new ClientThread(i));
		}
		executors.shutdown();
	}
}

class ClientThread extends Thread {
	private int num;
	public ClientThread(int num) {
		this.num = num;
	}
	public void run() {
		new NumberClient(num);
	}
}