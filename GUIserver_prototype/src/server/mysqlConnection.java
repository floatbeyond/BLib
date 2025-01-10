package server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import common.Subscriber;
import common.Librarian;
import common.Book;
import common.BookCopy;
import common.BorrowingRecord;
import common.OrderRecord;
import common.DataLogs;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;

public class mysqlConnection {
    
	public static Connection connectToDB(String ip, String user, String password) 
	{		
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
        	 System.out.println("Driver definition failed");
        	 }
        try {
			String url = String.format("jdbc:mysql://%s/blib?serverTimezone=IST", ip);
            Connection conn = DriverManager.getConnection(url, user, password);
			InstanceManager.setDbConnection(conn);
            System.out.println("SQL connection succeed");
            return conn;
     	} catch (SQLException ex) {
			/* handle any errors*/
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        }
   	}

	/**
	 * check if the user exists in the DB
	 * get - id(PK)
	 * return - true if exists in DB otherwise false
	 */

	public static Object userLogin(Connection conn, int userId) {
		// Check librarians first
		String librarianQuery = "SELECT * FROM librarians WHERE LibID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(librarianQuery)) {
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Librarian(
					rs.getInt("LibID"), 
					rs.getString("Name")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Check subscribers next
		String subscriberQuery = "SELECT * FROM subscribers WHERE SubID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(subscriberQuery)) {
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Subscriber(
					rs.getInt("SubID"), 
					rs.getString("Name"), 
					rs.getString("Status"), 
					rs.getString("PhoneNumber"), 
					rs.getString("Email"),
					rs.getInt("Penalties"),
					rs.getDate("FreezeUntil"),
					rs.getDate("Joined"),
					rs.getDate("Expiration")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	// show to user all the subscribers in the DB
	public static ArrayList<Subscriber> getSubscribers(Connection conn) {
		ArrayList<Subscriber> subscribers = new ArrayList<>();
		String query = "SELECT * FROM subscribers";
		
		try (Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(query)) {
			
			while (rs.next()) {
				Subscriber s = new Subscriber(
					rs.getInt("SubID"), 
					rs.getString("Name"), 
					rs.getString("Status"), 
					rs.getString("PhoneNumber"), 
					rs.getString("Email"),
					rs.getInt("Penalties"),
					rs.getDate("FreezeUntil"),
					rs.getDate("Joined"),
					rs.getDate("Expiration")
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
	    String query = "UPDATE subscribers SET PhoneNumber = ?, Email = ? WHERE SubID = ?";
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
		String query = "SELECT * FROM subscribers WHERE SubID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subscriberId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int sub_id = rs.getInt("SubID");
				String sub_name = rs.getString("Name");
				String status = rs.getString("Status"); 
				String phoneNumber = rs.getString("PhoneNumber");
				String email = rs.getString("Email");
				int penalties = rs.getInt("Penalties");
				Date frozen_until = rs.getDate("FreezeUntil");
				Date join_date = rs.getDate("Joined");
				Date exp_date = rs.getDate("Expiration");
				return new Subscriber(sub_id, sub_name, status, phoneNumber, email, 
										penalties, frozen_until, join_date, exp_date);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Object> searchBooks(Connection conn, String searchType, String searchText) {
		List<Object> results = new ArrayList<>();
		String bookQuery = "SELECT * FROM books WHERE " + searchType + " LIKE ?";
		String bookCopyQuery = "SELECT * FROM bookcopies WHERE BookID = ?";
		String borrowingRecordQuery = "SELECT * FROM borrowrecords WHERE CopyID = ? AND Status = 'Borrowed'";

		try (PreparedStatement bookStmt = conn.prepareStatement(bookQuery);
			PreparedStatement bookCopyStmt = conn.prepareStatement(bookCopyQuery);
			PreparedStatement borrowingRecordStmt = conn.prepareStatement(borrowingRecordQuery)) {

			// Set the search text for the book query
			bookStmt.setString(1, "%" + searchText + "%");
			ResultSet bookRs = bookStmt.executeQuery();

			// Iterate through the result set of the book query
			while (bookRs.next()) {
				int bookId = bookRs.getInt("BookID");
				String title = bookRs.getString("Title");
				String author = bookRs.getString("Author");
				String genre = bookRs.getString("Genre");
				String description = bookRs.getString("Description");

				// Create a Book object and add it to the results list
				Book book = new Book(bookId, title, author, genre, description);
				results.add(book);

				// Set the BookID for the book copy query
				bookCopyStmt.setInt(1, bookId);
				ResultSet bookCopyRs = bookCopyStmt.executeQuery();

				// Iterate through the result set of the book copy query
				while (bookCopyRs.next()) {
					int copyId = bookCopyRs.getInt("CopyID");
					String location = bookCopyRs.getString("Location");
					String status = bookCopyRs.getString("Status");

					// Create a BookCopy object and add it to the results list
					BookCopy bookCopy = new BookCopy(copyId, bookId, location, status);
					results.add(bookCopy);

					// Set the CopyID for the borrowing record query
                    borrowingRecordStmt.setInt(1, copyId);
                    ResultSet borrowingRecordRs = borrowingRecordStmt.executeQuery();

                    // Iterate through the result set of the borrowing record query
                    while (borrowingRecordRs.next()) {
                        int borrowId = borrowingRecordRs.getInt("BorrowID");
                        int subId = borrowingRecordRs.getInt("SubID");
                        Date borrowDate = borrowingRecordRs.getDate("BorrowDate");
                        Date expectedReturnDate = borrowingRecordRs.getDate("ExpectedReturnDate");
                        Date actualReturnDate = borrowingRecordRs.getDate("ActualReturnDate");
                        String borrowStatus = borrowingRecordRs.getString("Status");

                        // Create a BorrowingRecord object and add it to the results list
                        BorrowingRecord borrowingRecord = new BorrowingRecord(borrowId, copyId, subId, borrowDate, expectedReturnDate, actualReturnDate, borrowStatus);
                        results.add(borrowingRecord);
                    }
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}
	

	public static BookCopy findBookCopy(Connection conn, int bookCopyId) {
		String query = "SELECT * FROM bookcopies WHERE CopyID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookCopyId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int copy_id = rs.getInt("CopyID");
				int book_id = rs.getInt("BookID");
				String location = rs.getString("Location");
				String status = rs.getString("Status");
				return new BookCopy(copy_id, book_id, location, status);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean addBorrowingRecord(Connection conn, int subscriberId, int bookCopyId, Date borrowDate, Date expectedReturnDate) {
		String query = "INSERT INTO borrowrecords (SubID, CopyID, BorrowDate, ReturnDate, Status) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, subscriberId);
			stmt.setInt(2, bookCopyId);
			stmt.setDate(3, borrowDate);
			stmt.setDate(4, expectedReturnDate);
			stmt.setString(5, "Borrowed");
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int borrowId = rs.getInt(1);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	// show to user all the logs in the DB
	public static ArrayList<DataLogs> getDataLogs(Connection conn) {
		ArrayList<DataLogs> dataLogs = new ArrayList<>();
		String query = "SELECT * FROM datalogs";
		
		try (Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(query)) {
			
			while (rs.next()) {
				DataLogs log = new DataLogs(
					rs.getInt("LogID"),
					rs.getInt("SubID"),
					rs.getString("Action"),
					rs.getDate("Timestamp")
				);
<<<<<<< HEAD
				dataLogs.add(s);
=======
				dataLogs.add(log);
>>>>>>> 79059ca (PersonalDetails+DataLogs)
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dataLogs;
	}
<<<<<<< HEAD
=======

	public static int getBooksInOrderCount(Connection conn, int subscriberId) {
        String query = "SELECT COUNT(*) FROM orderrecord WHERE status = 'In Order' AND subscriber_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, subscriberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getBooksInBorrowCount(Connection conn, int subscriberId) {
        String query = "SELECT COUNT(*) FROM borrowingrecord WHERE status = 'Borrowed' AND subscriber_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, subscriberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
>>>>>>> 79059ca (PersonalDetails+DataLogs)
	

}


