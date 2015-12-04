package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class NumberServer {
	private ArrayList<Integer> numbers;
	public NumberServer() {
		ServerSocket ss = null;
		try {
			numbers = new ArrayList<Integer>();
			ss = new ServerSocket(6789);
			while(true) {
				System.out.println("Waiting for connection...");
				Socket s = ss.accept();
				System.out.println("Accepted connection: " + s.getInetAddress() + ":" + s.getPort());
				ServerThread st = new ServerThread(this, s);
				st.start();
			}
		} catch (IOException ioe) {
			System.out.println("IOE in NumberServer constructor: " + ioe.getMessage());
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					System.out.println("IOE closing ss: " + ioe.getMessage());
				}
			}
		}
	}

	public synchronized ArrayList<Integer> addNumberAndGetArrayList(int num) {
		addNumber(num);
		return getArrayList();
	}
	private synchronized void addNumber(int num) {
		numbers.add(num);
	}
	private synchronized ArrayList<Integer> getArrayList() {
		return numbers;
	}

	public static void main(String [] args) {
		new NumberServer();
	}
}

class ServerThread extends Thread {
	private Socket s;
	private NumberServer ns;
	public ServerThread(NumberServer ns, Socket s) {
		this.s = s;
		this.ns = ns;
	}
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			int num = Integer.parseInt(br.readLine());
			synchronized(ns) {
				ArrayList<Integer> numbers = ns.addNumberAndGetArrayList(num);
				oos.writeObject(numbers);
				oos.flush();
			}
			oos.close();
			br.close();
			s.close();
		} catch (IOException ioe) {
			System.out.println("IOE in ServerThread.run(): " + ioe.getMessage());
		}
	}
}