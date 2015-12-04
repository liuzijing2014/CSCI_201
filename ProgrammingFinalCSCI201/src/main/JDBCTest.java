package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTest {

	public static void main (String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/StudentGrades?user=root");
			Statement st = conn.createStatement();
			String name = "Sheldon";
			//ResultSet rs = st.executeQuery("SELECT * from Student where fname='" + name + "'");
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Student WHERE fname=?");
			ps.setString(1, name); // set first variable in prepared statement
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String fname = rs.getString("fname");
				String lname = rs.getString("lname");
				int studentID = rs.getInt("studentID");
				System.out.println ("fname = " + fname);
				System.out.println ("lname = " + lname);
				System.out.println ("studentID = " + studentID);
			}
			rs.close();
			st.close();
			conn.close();
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
		}
	}
}