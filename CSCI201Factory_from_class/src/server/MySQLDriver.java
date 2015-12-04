package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MySQLDriver {
	private Connection conn;
	
	private final static String selectName = "SELECT * FROM FACTORYORDERS WHERE NAME=?";
	private final static String addProduct = "INSERT INTO FACTORYORDERS (NAME, CREATED) VALUES (?, ?)";
	private final static String updateProduct = "UPDATE FACTORYORDERS SET CREATED=? WHERE Name=?";
	
	public MySQLDriver() {
		try {
			new com.mysql.jdbc.Driver();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void Connect() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/factory?user=root&password=root");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void Stop() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean doesExist(String productName) {
		try {
			PreparedStatement ps = conn.prepareStatement(selectName);
			ps.setString(1, productName);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				FactoryServerGUI.addMessage(result.getString(1) + " exists with count: " + result.getInt(2));
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		FactoryServerGUI.addMessage("Unable to find product with name: " + productName);
		return false;		
	}
	
	public void Add(String productName) {
		try {
			PreparedStatement ps = conn.prepareStatement(addProduct);
			ps.setString(1, productName);
			ps.setInt(2, 0);
			ps.executeUpdate();
			FactoryServerGUI.addMessage("Adding product: " + productName + " to table with count 0");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void incrementCreatedCount(String productName) {
		try {
			PreparedStatement ps = conn.prepareStatement(selectName);
			ps.setString(1, productName);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				int newAmount = result.getInt(2)+1;
				PreparedStatement ps2 = conn.prepareStatement(updateProduct);
				ps2.setInt(1, newAmount);
				ps2.setString(2, productName);
				ps2.executeUpdate();
				FactoryServerGUI.addMessage("Incrementing created count of product " + productName + " to " + newAmount);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
