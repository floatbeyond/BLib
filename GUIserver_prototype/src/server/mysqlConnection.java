package server;

import java.sql.Connection;
import java.util.ArrayList;

import common.Subscriber;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class mysqlConnection {
    
	public static Connection connectToDB(String ip, String user, String password) 
	{		
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
        	 System.out.println("Driver definition failed");
        	 }
        try 
        {
			String url = String.format("jdbc:mysql://%s/prototype?serverTimezone=IST", ip);
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("SQL connection succeed");
            return conn;
     	} catch (SQLException ex) 
     	    {/* handle any errors*/
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
            }
   	}
	
	// show to user all the subscribers in the DB
	public static ArrayList<Subscriber> getSubscribers(Connection conn) {
		ArrayList<Subscriber> subscribers = new ArrayList<>();
		String query = "SELECT * FROM Subscribers";
		
		try (Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(query)) {
			
			while (rs.next()) {
				Subscriber s = new Subscriber(
					rs.getInt("subscriber_id"), 
					rs.getString("subscriber_name"), 
					rs.getInt("detailed_subscription_history"), 
					rs.getString("subscriber_phone_number"), 
					rs.getString("subscriber_email")
				);
				subscribers.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return subscribers;
	}
	
	
	/**
     * update a specific phone number and email of a subscriber
     * get - id(PK)
	 */
	public static boolean updateSubscriber(Connection con, int subscriberId, String phoneNumber, String email) {
	    String query = "UPDATE Subscribers SET subscriber_phone_number = ?, subscriber_email = ? WHERE subscriber_id = ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setString(1, phoneNumber);
	        ps.setString(2, email);
	        ps.setInt(3, subscriberId);
	        ps.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	

	/**
     * find a specific subscriber by id
     * get - id(PK)
     * return - true if exists in DB otherwise false
     */
	   public static Subscriber findSubscriber(Connection conn, int subscriberId) {
	       String query = "SELECT * FROM subscribers WHERE subscriber_id = ?";
	       try (PreparedStatement stmt = conn.prepareStatement(query)) {
	           stmt.setInt(1, subscriberId);
	           ResultSet rs = stmt.executeQuery();
	           if (rs.next()) {
	               int id = rs.getInt("subscriber_id");
	               String name = rs.getString("subscriber_name");
	               int history = rs.getInt("detailed_subscription_history");
	               String phoneNumber = rs.getString("subscriber_phone_number");
	               String email = rs.getString("subscriber_email");
	               return new Subscriber(id, name, history, phoneNumber, email);
	           }
	       } catch (SQLException e) {
	           e.printStackTrace();
	       }
	       return null;
	   }

}


