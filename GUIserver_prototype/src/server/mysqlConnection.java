package server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import common.Subscriber;
import common.Librarian;
import common.Book;
import common.BookCopy;
import common.BorrowingRecord;
import common.DateUtils;
import common.OrderRecord;
import common.DataLogs;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;

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
				LocalDate frozen_until = DateUtils.toLocalDate(rs.getDate("FreezeUntil"));
                LocalDate join_date = DateUtils.toLocalDate(rs.getDate("Joined"));
                LocalDate exp_date = DateUtils.toLocalDate(rs.getDate("Expiration"));

				return new Subscriber(
					rs.getInt("SubID"), 
					rs.getString("Name"), 
					rs.getString("Status"), 
					rs.getString("PhoneNumber"), 
					rs.getString("Email"),
					rs.getInt("Penalties"),
					frozen_until,
					join_date,
					exp_date,
					rs.getInt("CurrentlyBorrowedBooks"),
					rs.getInt("CurrentlyOrderedBooks")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	// add new subscriber to the DB
	public static int addSubscriber(Connection conn, Subscriber s) {
		String query = "INSERT INTO subscribers (Name, Status, PhoneNumber, Email, Penalties, FreezeUntil, Joined, Expiration) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, s.getSub_name());
			stmt.setString(2, s.getSub_status());
			stmt.setString(3, s.getSub_phone_num());
			stmt.setString(4, s.getSub_email());
			stmt.setInt(5, s.getSub_penalties());
			stmt.setDate(6, DateUtils.toSqlDate(s.getSub_freeze()));
			stmt.setDate(7, DateUtils.toSqlDate(s.getSub_joined()));
			stmt.setDate(8, DateUtils.toSqlDate(s.getSub_expiration()));
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int subId = rs.getInt(1);
				return subId;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	// show to user all the subscribers in the DB
	public static ArrayList<Subscriber> getSubscribers(Connection conn) {
		ArrayList<Subscriber> subscribers = new ArrayList<>();
		String query = "SELECT * FROM subscribers";
		
		try (Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(query)) {
			
			while (rs.next()) {
				LocalDate frozen_until = DateUtils.toLocalDate(rs.getDate("FreezeUntil"));
                LocalDate join_date = DateUtils.toLocalDate(rs.getDate("Joined"));
                LocalDate exp_date = DateUtils.toLocalDate(rs.getDate("Expiration"));

				Subscriber s = new Subscriber(
					rs.getInt("SubID"), 
					rs.getString("Name"), 
					rs.getString("Status"), 
					rs.getString("PhoneNumber"), 
					rs.getString("Email"),
					rs.getInt("Penalties"),
					frozen_until,
					join_date,
					exp_date,
					rs.getInt("CurrentlyBorrowedBooks"),
					rs.getInt("CurrentlyOrderedBooks")
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
	public static Object updateSubscriber(Connection conn, int subscriberId, String phoneNumber, String email) {
		Subscriber s = findSubscriber(conn, subscriberId);
	    String query = "UPDATE subscribers SET PhoneNumber = ?, Email = ? WHERE SubID = ?";
	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setString(1, phoneNumber);
	        ps.setString(2, email);
	        ps.setInt(3, subscriberId);
	        ps.executeUpdate();
			return new Subscriber(subscriberId, s.getSub_name(), s.getSub_status(), phoneNumber, email, s.getSub_penalties(), 
						s.getSub_freeze(), s.getSub_joined(), s.getSub_expiration(), s.getCurrentlyBorrowed(), s.getCurrentlyOrdered());
	    } catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				String errorMessage = e.getMessage();
				if (errorMessage.contains("PhoneNumber")) {
					return "Phone number already exists";
				} else if (errorMessage.contains("Email")) {
					return "Email already exists";
				}
			} else {
				e.printStackTrace();
	        	return "ERROR:Update failed";
			}
	    }
		return "ERROR:Update failed";
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
				LocalDate frozen_until = DateUtils.toLocalDate(rs.getDate("FreezeUntil"));
                LocalDate join_date = DateUtils.toLocalDate(rs.getDate("Joined"));
                LocalDate exp_date = DateUtils.toLocalDate(rs.getDate("Expiration"));
				int currentlyBorrowed = rs.getInt("CurrentlyBorrowedBooks");
				int currentlyOrdered = rs.getInt("CurrentlyOrderedBooks");

				return new Subscriber(sub_id, sub_name, status, phoneNumber, email, penalties, 
							frozen_until, join_date, exp_date, currentlyBorrowed, currentlyOrdered);
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
				int numOfCopies = bookRs.getInt("NumOfCopies");

				// Create a Book object and add it to the results list
				Book book = new Book(bookId, title, author, genre, description, numOfCopies);
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
                        LocalDate borrowDate = DateUtils.toLocalDate(borrowingRecordRs.getDate("BorrowDate"));
                        LocalDate expectedReturnDate = DateUtils.toLocalDate(borrowingRecordRs.getDate("ExpectedReturnDate"));
                        LocalDate actualReturnDate = DateUtils.toLocalDate(borrowingRecordRs.getDate("ActualReturnDate"));
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

	public static boolean addBorrowingRecord(Connection conn, BorrowingRecord br) {
		String query = "INSERT INTO borrowrecords (SubID, CopyID, BorrowDate, ExpectedReturnDate, Status) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, br.getSubId());
			stmt.setInt(2, br.getCopyId());
			stmt.setDate(3, DateUtils.toSqlDate(br.getBorrowDate()));
			stmt.setDate(4, DateUtils.toSqlDate(br.getExpectedReturnDate()));
			stmt.setString(5, br.getStatus());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int borrowId = rs.getInt(1);
				try (Statement st = conn.createStatement()) {
					String updateCopyStatus = "UPDATE bookcopies SET Status = 'Borrowed' WHERE CopyID = " + br.getCopyId();
					st.executeUpdate(updateCopyStatus);
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static boolean returnBook(Connection conn, int subId, int copyId, LocalDate returnDate) {
		String query = "UPDATE borrowrecords SET ActualReturnDate = ?, Status = 'Returned' WHERE SubID = ? AND CopyID = ? AND (Status = 'Borrowed' OR Status = 'Late')";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setDate(1, DateUtils.toSqlDate(returnDate));
			stmt.setInt(2, subId);
			stmt.setInt(3, copyId);
			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				System.out.println("No matching records found to update.");
				return false;
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean addOrderRecord(Connection conn, OrderRecord order) {
        String query = "INSERT INTO orders (order_id, subscriber_id, book_id, order_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, order.getOrderId());
			stmt.setInt(2, order.getSubId());
			stmt.setInt(3, order.getBookId());
			stmt.setDate(4, Date.valueOf(order.getOrderDate()));
			stmt.setString(5, order.getStatus());
			stmt.setDate(6, Date.valueOf(order.getNotificationStamp()));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	public static boolean isOrderExists(Connection conn, int subscriberId, int bookId) {
		String query = "SELECT COUNT(*) FROM orderrecord WHERE subscriber_id = ? AND book_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subscriberId);
			stmt.setInt(2, bookId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// show to user all the logs in the DB
	public static ArrayList<DataLogs> getDataLogs(Connection conn, int subscriberId) {
		ArrayList<DataLogs> dataLogs = new ArrayList<>();
		String query = "SELECT * FROM datalogs WHERE SubID = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subscriberId);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				LocalDate timestamp = DateUtils.toLocalDate(rs.getDate("Timestamp"));
				DataLogs log = new DataLogs(
					rs.getInt("LogID"),
					rs.getInt("SubID"),
					rs.getString("Action"),
					timestamp
				);
				dataLogs.add(log);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataLogs;
	}
}


