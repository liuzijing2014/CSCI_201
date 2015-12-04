package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Driver;

public class MySQLDriver {
	
	private final static String selectName = "SELECT * FROM FACTORYORDERS WHERE NAME=?";
	private final static String addProduct = "INSERT INTO FACTORYORDERS(NAME,CREATED) VALUES(?,?)";
	private final static String updateProduct = "UPDATE FACTORYORDERS SET CREATED=CREATED+1 WHERE NAME=?";
	
	private Connection con;
	
	public MySQLDriver()
	{
		try{
			new Driver();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void connect(){
		try{
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/factory?user=root&password=Steven!951208");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void stop(){
		try{
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public boolean doesExist(String productName){
		try{
			PreparedStatement ps = con.prepareStatement(selectName);
			ps.setString(1, productName);
			ResultSet result = ps.executeQuery();
			while(result.next()){
				FactoryServerGUI.addMessage(result.getString(1)+ "exists with count: " + result.getInt(2));
				return true;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		FactoryServerGUI.addMessage("Unable to find product with name: " + productName);
		return false;
	}
	
	public void add(String productName)
	{
		try{
			PreparedStatement ps = con.prepareStatement(addProduct);
			ps.setString(1, productName);
			ps.setInt(2, 0);
			ps.executeUpdate();
			FactoryServerGUI.addMessage("Adding product:" + productName + " to table with count 0");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void updateNum(String productName){
		try{
			PreparedStatement ps = con.prepareStatement(updateProduct);
			ps.setString(1, productName);
			ps.executeUpdate();
			FactoryServerGUI.addMessage("Updating product:" + productName + " by 1");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
