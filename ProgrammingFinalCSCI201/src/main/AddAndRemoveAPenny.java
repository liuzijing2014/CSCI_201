package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AddAndRemoveAPenny implements Runnable {
	private static PiggyBank piggy = new AddAndRemoveAPenny().new PiggyBank();
	private boolean isWithdrawal;

	public void run() {
		if (isWithdrawal) {
			piggy.withdraw((int)(Math.random() * 9 + 1));
		}
		else {
			piggy.deposit((int)(Math.random() * 9 + 1));
		}
	}

	public static void main(String [] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		for (int i=0; i < 100; i++) {
			AddAndRemoveAPenny penny = new AddAndRemoveAPenny();
			if (i < 50) { // exactly 50 threads will withdraw
				penny.isWithdrawal = false;
			}
			else { // and exactly 50 threads will deposit
				penny.isWithdrawal = true;
			}
			executor.execute(penny);
		}
		executor.shutdown();
		// wait until all tasks are finished
		while(!executor.isTerminated()) { }

		System.out.println("Balance = " + piggy.getBalance());
	}

	private class PiggyBank {
		private int balance = 0;
		private Lock lock = new ReentrantLock();
		private Condition depositMade = lock.newCondition();
		public int getBalance() {
			return balance;
		}
		public void withdraw(int amount) {
			lock.lock();
			try {
				while (balance < amount) {
					System.out.print("\tWaiting for deposit to withdraw $" + amount);
					System.out.println(" from balance of $" + balance + "...");
					depositMade.await();
				}
				balance -= amount;
				System.out.println("\t$" + amount + " withdrawn, leaving balance of $" + balance);
			} catch (InterruptedException ie) {
				System.out.println("IE: " + ie.getMessage());
			} finally {
				lock.unlock();
			}
		}
		public void deposit(int amount) {
			lock.lock(); // acquires lock
			try {
				balance += amount;
				System.out.println("$" + amount + " deposited, making balance of $" + balance);
				depositMade.signalAll();
			} finally {
				lock.unlock(); // release the lock
			}

		}
	}
}
