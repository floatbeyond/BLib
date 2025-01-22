package server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Subscriber;
import common.SubscriberStatusReport;
import common.Librarian;
import common.Book;
import common.BookCopy;
import common.BorrowRecordDTO;
import common.BorrowTimeReport;
import common.BorrowingRecord;
import common.DateUtils;
import common.OrderRecordDTO;
import common.DataLogs;
import common.Notification;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.sql.Timestamp;

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

	// Fetch notifications for a subscriber
    public static List<Notification> getNotifications(Connection conn, int subId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE SubID = ? ORDER BY Timestamp DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, subId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int notificationId = rs.getInt("NotificationID");
                String message = rs.getString("Message");
                Timestamp timestamp = rs.getTimestamp("Timestamp");
                notifications.add(new Notification(notificationId, subId, message, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

	// Fetch new notifications for a subscriber
	public static List<Notification> getNewNotifications(Connection conn, int subId) {
		List<Notification> notifications = new ArrayList<>();
		String query = "SELECT * FROM notifications WHERE SubID = ? AND Timestamp > (SELECT LastFetched FROM subscribers WHERE SubID = ?) ORDER BY Timestamp DESC";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subId);
			stmt.setInt(2, subId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int notificationId = rs.getInt("NotificationID");
				String message = rs.getString("Message");
				Timestamp timestamp = rs.getTimestamp("Timestamp");
				notifications.add(new Notification(notificationId, subId, message, timestamp));
			}
			// Update the last fetched timestamp
			updateLastFetched(conn, subId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return notifications;
	}

	// Update the last fetched timestamp for a subscriber
	private static void updateLastFetched(Connection conn, int subId) {
		String query = "UPDATE subscribers SET LastFetched = NOW() WHERE SubID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Send a notification to a subscriber
	public static boolean sendNotification(Connection conn, int subId, String message) {
		String query = "INSERT INTO notifications (SubID, Message, Timestamp) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subId);
			stmt.setString(2, message);
			stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}



	public static boolean sendOrderNotification(Connection conn, int orderId, int subId, String bookTitle) {
		try {
			// Step 1: Update the status of the order to 'In-Progress'
			if (orderId != 0) {
				String updateOrderQuery = "UPDATE orderrecords SET Status = 'In-Progress', NotificationTimestamp = NOW() WHERE OrderID = ?";
				try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderQuery)) {
					updateOrderStmt.setInt(1, orderId);
					updateOrderStmt.executeUpdate();
				}
			}

            // Step 2: Insert a notification into the notifications table
            if (subId != 0) {
                String message = "Your book '" + bookTitle + "' is ready for pick-up";
                return sendNotification(conn, subId, message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
	}

	// add new subscriber to the DB
	public static int addSubscriber(Connection conn, Subscriber s) {
		String query = "INSERT INTO subscribers (Name, Status, PhoneNumber, Email, Joined, Expiration) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, s.getSub_name());
			stmt.setString(2, s.getSub_status());
			stmt.setString(3, s.getSub_phone_num());
			stmt.setString(4, s.getSub_email());
			stmt.setDate(5, DateUtils.toSqlDate(s.getSub_joined()));
			stmt.setDate(6, DateUtils.toSqlDate(s.getSub_expiration()));
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int subId = rs.getInt(1);
				return subId;
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				return -1;
			} else {
				e.printStackTrace();
			}
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

	public static boolean reactivateSubscriber(Connection conn, int subscriberId, String libName) {
		String query = "UPDATE subscribers SET Status = 'Active', Expiration = ? WHERE SubID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setDate(1, DateUtils.toSqlDate(LocalDate.now().plusYears(1)));
			stmt.setInt(2, subscriberId);
			String action = "Reactivated subscriber: " + subscriberId + "by librarian " + libName;
			stmt.executeUpdate();
			return logLibrarianActions(conn, subscriberId, action);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static List<BorrowRecordDTO> getUserBorrows(Connection conn, int subId) {
		List<BorrowRecordDTO> borrows = new ArrayList<>();
		String query = "SELECT br.BorrowID, br.CopyID, br.BorrowDate, br.ExpectedReturnDate, br.ActualReturnDate, br.Status, b.Title " +
						"FROM borrowrecords br " +
						"JOIN bookcopies bc ON br.CopyID = bc.CopyID " +
						"JOIN books b ON bc.BookID = b.BookID " +
						"WHERE br.SubID = ? AND br.Status != 'Returned'";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String bookName = rs.getString("Title");
				int borrowId = rs.getInt("BorrowID");
				LocalDate borrowDate = DateUtils.toLocalDate(rs.getDate("BorrowDate"));
				LocalDate expectedReturnDate = DateUtils.toLocalDate(rs.getDate("ExpectedReturnDate"));
				String status = rs.getString("Status");

				borrows.add(new BorrowRecordDTO(bookName, borrowId, borrowDate, expectedReturnDate, status));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return borrows;
	}

	public static List<OrderRecordDTO> getUserOrders(Connection conn, String user, int subId) {
		List<OrderRecordDTO> orders = new ArrayList<>();
		String query;
		if (user.equals("librarian")) {
			query = "SELECT o.OrderID, o.BookID, o.OrderDate, o.Status, o.NotificationTimestamp, b.Title " +
						"FROM orderrecords o " +
						"JOIN books b ON o.BookID = b.BookID " +
						"WHERE o.SubID = ?";
		} else {
			query = "SELECT o.OrderID, o.BookID, o.OrderDate, o.Status, o.NotificationTimestamp, b.Title " +
						"FROM orderrecords o " +
						"JOIN books b ON o.BookID = b.BookID " +
						"WHERE o.SubID = ? AND o.Status NOT IN ('Cancelled', 'Completed')";
		}
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int orderId = rs.getInt("OrderID");
				String bookName = rs.getString("Title");
				LocalDate orderDate = DateUtils.toLocalDate(rs.getDate("OrderDate"));
				String status = rs.getString("Status");
				Timestamp notificationTimestamp = rs.getTimestamp("NotificationTimestamp");

				orders.add(new OrderRecordDTO(orderId, bookName, orderDate, status, notificationTimestamp));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	public static List<Object> searchBooks(Connection conn, String searchType, String searchText) {
		List<Object> results = new ArrayList<>();
		String bookQuery = "SELECT * FROM books WHERE " + searchType + " LIKE ?";
		String bookCopyQuery = "SELECT * FROM bookcopies WHERE BookID = ?";
		String borrowingRecordQuery = "SELECT * FROM borrowrecords WHERE CopyID = ? AND Status IN ('Borrowed', 'Late')";

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

	public static int getBookIdByBorrowId(Connection conn, int borrowId) {
		String query = "SELECT BookID FROM bookcopies WHERE CopyID = (SELECT CopyID FROM borrowrecords WHERE BorrowID = ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, borrowId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("BookID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static Book getBookByCopyId(Connection conn, int copyId) {
		String query = "SELECT BookID FROM bookcopies WHERE CopyID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, copyId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int bookId = rs.getInt("BookID");
				return getBookById(conn, bookId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Book getBookById(Connection conn, int bookId) {
		String query = "SELECT * FROM books WHERE BookID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String title = rs.getString("Title");
				String author = rs.getString("Author");
				String genre = rs.getString("Genre");
				String description = rs.getString("Description");
				int numOfCopies = rs.getInt("NumOfCopies");
				return new Book(bookId, title, author, genre, description, numOfCopies);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getCopyIdByCancelledOrder(Connection conn, int subId, int bookId) {
		String query = "SELECT CopyID FROM bookcopies WHERE BookID = ? AND Status = ? ORDER BY CopyID ASC LIMIT 1";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);
			stmt.setString(2, "Ordered by " + subId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int copyId = rs.getInt("CopyID");
				return copyId;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean setBookCopyOrdered(Connection conn, int copyId, int subId) {
		String query = "UPDATE bookcopies SET Status = ? WHERE CopyID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(2, copyId);
			stmt.setString(1, "Ordered by " + subId);
			int affectedRows = stmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
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

	public static boolean extendBorrow(Connection conn, int subId, int borrowId, LocalDate extensionDate) {
		String query = "UPDATE borrowrecords SET ExpectedReturnDate = ? WHERE SubID = ? AND BorrowID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setDate(1, DateUtils.toSqlDate(extensionDate));
			stmt.setInt(2, subId);
			stmt.setInt(3, borrowId);
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

	public static boolean logLibrarianActions(Connection conn, int subId, String action) {
		String query = "INSERT INTO datalogs (SubID, Action, Timestamp) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subId);
			stmt.setString(2, action);
			stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean logExtensionByLibrarian(Connection conn, int subId, int bookId, String libName) {
		String bookTitleQuery = "SELECT Title FROM books WHERE BookID = ?";
		try (PreparedStatement bookTitleStmt = conn.prepareStatement(bookTitleQuery)) {
	
			// Query the book title
			bookTitleStmt.setInt(1, bookId);
			ResultSet rs = bookTitleStmt.executeQuery();
			if (rs.next()) {
				String bookTitle = rs.getString("Title");
				String action = "Extended book '" + bookTitle + "' by librarian " + libName;
				return logLibrarianActions(conn, subId, action);
			} else {
				System.out.println("Book not found.");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean returnBook(Connection conn, int subId, int copyId, LocalDate returnDate) {
		String selectQuery = "SELECT ExpectedReturnDate FROM borrowrecords WHERE SubID = ? AND CopyID = ? AND (Status = 'Borrowed' OR Status = 'Late')";
		String updateQuery = "UPDATE borrowrecords SET ActualReturnDate = ?, Status = ? WHERE SubID = ? AND CopyID = ? AND (Status = 'Borrowed' OR Status = 'Late')";
		try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
			 PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
			// Retrieve the expected return date
			selectStmt.setInt(1, subId);
			selectStmt.setInt(2, copyId);
			ResultSet rs = selectStmt.executeQuery();

			if (rs.next()) {
				
				LocalDate expectedReturnDate = rs.getDate("ExpectedReturnDate").toLocalDate();
				String status = "Returned";
				
				// Determine the status based on the return date
				if (returnDate.isAfter(expectedReturnDate)) {
					status = "ReturnedLate";
				}
				
				// Update the borrow record with the actual return date and status
				// Debugging logs
				System.out.println("Parameters: " + returnDate + ", " + status + ", " + subId + ", " + copyId);

				updateStmt.setDate(1, java.sql.Date.valueOf(returnDate));
				updateStmt.setString(2, status);
				updateStmt.setInt(3, subId);
				updateStmt.setInt(4, copyId);
				int affectedRows = updateStmt.executeUpdate();
				
				if (affectedRows == 0) {
					System.out.println("No matching records found to update.");
					return false;
				}
				Book returnedBook = getBookByCopyId(conn, copyId);
				System.out.println("Returned book: " + returnedBook.getBookId());
				Integer nextSubId = mysqlConnection.getFirstWaitingSubId(conn, returnedBook.getBookId());
				if (nextSubId != null) {
					mysqlConnection.setBookCopyOrdered(conn, copyId, (int) nextSubId);
				}
				return true;
			} else {
				System.out.println("No matching records found to update.");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

    public static boolean addOrderRecord(Connection conn, int bookId, int subId) {
        String query = "INSERT INTO orderrecords (BookID, SubID, OrderDate, Status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, subId);
            stmt.setDate(3, DateUtils.toSqlDate(LocalDate.now()));
            stmt.setString(4, "Waiting");
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	public static Integer getFirstWaitingSubId(Connection conn, int bookId) {
		String query = "SELECT SubID FROM orderrecords WHERE BookID = ? AND Status = 'Waiting'";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("SubID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

    public static boolean isOrderExists(Connection conn, int bookId, int subId) {
        String query = "SELECT 1 FROM orderrecords WHERE SubID = ? AND BookID = ? AND Status IN (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, subId);
            stmt.setInt(2, bookId);
            stmt.setString(3, "Waiting");
            stmt.setString(4, "In-Progress");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

	public static boolean anyOrderExists(Connection conn, int bookId) {
        String query = "SELECT 1 FROM orderrecords WHERE BookID = ? AND Status IN (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.setString(2, "Waiting");
            stmt.setString(3, "In-Progress");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

	public static boolean areOrdersCapped(Connection conn, int bookId) {
		String copiesQuery = "SELECT NumOfCopies FROM books WHERE BookID = ?";
		String ordersCountQuery = "SELECT COUNT(*) AS orderCount FROM orderrecords WHERE BookID = ? AND Status IN ('Waiting', 'In-Progress')";
		try (PreparedStatement copiesStmt = conn.prepareStatement(copiesQuery);
			 PreparedStatement ordersCountStmt = conn.prepareStatement(ordersCountQuery)) {
			
			// Query the number of copies available for the book
			copiesStmt.setInt(1, bookId);
			ResultSet copiesRs = copiesStmt.executeQuery();
			if (copiesRs.next()) {
				int numOfCopies = copiesRs.getInt("NumOfCopies");
				
				// Count the number of orders for the book that are either "Waiting" or "In-Progress"
				ordersCountStmt.setInt(1, bookId);
				ResultSet ordersCountRs = ordersCountStmt.executeQuery();
				if (ordersCountRs.next()) {
					int orderCount = ordersCountRs.getInt("orderCount");
					
					// Compare the count of orders with the number of copies
					return orderCount >= numOfCopies;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int cancelOrder(Connection conn, int orderId) {
		int bookId = 0;
		String checkStatusQuery = "SELECT BookID, Status FROM orderrecords WHERE OrderID = ?";
		String updateQuery = "UPDATE orderrecords SET Status = 'Cancelled' WHERE OrderID = ?";
		try (PreparedStatement checkStatusStmt = conn.prepareStatement(checkStatusQuery);
		PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
			checkStatusStmt.setInt(1, orderId);
			ResultSet rs = checkStatusStmt.executeQuery();
			if (rs.next()) {
				bookId = rs.getInt("BookID");
				String currentStatus = rs.getString("Status");
				if ("Cancelled".equals(currentStatus)) {
					return 0; // Already cancelled
				}
			}
			updateStmt.setInt(1, orderId);
			updateStmt.executeUpdate();
			return bookId;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static Integer notifyNextOrder(Connection conn, int bookId) {
		String query = "SELECT OrderID, BookID, SubID FROM orderrecords WHERE BookID = ? AND Status = 'Waiting' ORDER BY OrderID ASC LIMIT 1";
		int orderId = 0;
		int subId = 0;
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				subId = rs.getInt("SubID");
				orderId = rs.getInt("OrderID");
				Book b = getBookById(conn, bookId);
				// print book title
				System.out.println("Order ID: " + orderId);
				System.out.println("Sub ID: " + subId);
				System.out.println("Book Title: " + b.getTitle());
				sendOrderNotification(conn, orderId, subId, b.getTitle());
				return subId;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// show to user all the logs in the DB
	public static ArrayList<DataLogs> getDataLogs(Connection conn, int subscriberId) {
		ArrayList<DataLogs> dataLogs = new ArrayList<>();
		String query = "SELECT LogID, SubID, Action, CONVERT_TZ(Timestamp, '+00:00', '+03:00') AS Timestamp\n" + //
						"FROM datalogs WHERE SubID = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subscriberId);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				int logId = rs.getInt("LogID");
				int subId = rs.getInt("SubID");
				String logAction = rs.getString("Action");
				Timestamp timestamp = rs.getTimestamp("Timestamp");

				DataLogs logs = new DataLogs(logId, subId, logAction, timestamp);
            	dataLogs.add(logs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataLogs;
	}

	// Reports

	public static List<BorrowTimeReport> fetchBorrowTimesReport(Connection conn, LocalDate now) {
        List<BorrowTimeReport> report = new ArrayList<>();
        String query = "SELECT b.Title, br.Status, br.BorrowDate, br.ExpectedReturnDate, br.ActualReturnDate " +
                       "FROM borrowrecords br " +
                       "JOIN bookcopies bc ON br.CopyID = bc.CopyID " +
                       "JOIN books b ON bc.BookID = b.BookID " +
                       "WHERE (MONTH(br.BorrowDate) = ? AND YEAR(br.BorrowDate) = ?) " +
                       "OR (MONTH(br.ActualReturnDate) = ? AND YEAR(br.ActualReturnDate) = ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, now.getMonthValue());
            stmt.setInt(2, now.getYear());
            stmt.setInt(3, now.getMonthValue());
            stmt.setInt(4, now.getYear());
            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, List<Integer>> heldDaysMap = new HashMap<>();
                Map<String, List<Integer>> lateDaysMap = new HashMap<>();
                Map<String, Integer> borrowCountMap = new HashMap<>();

                while (rs.next()) {
                    String bookTitle = rs.getString("Title");
                    String status = rs.getString("Status");
                    LocalDate borrowDate = rs.getDate("BorrowDate").toLocalDate();
                    LocalDate expectedReturnDate = rs.getDate("ExpectedReturnDate").toLocalDate();
                    LocalDate actualReturnDate = rs.getDate("ActualReturnDate") != null ? rs.getDate("ActualReturnDate").toLocalDate() : null;

					// Calculate days held for returned books
					if (status.equals("Returned")) {
						int daysHeld = (int) ChronoUnit.DAYS.between(borrowDate, actualReturnDate);
						heldDaysMap.computeIfAbsent(bookTitle, k -> new ArrayList<>()).add(daysHeld);
					}

					// Calculate days late for late or lost books
					if (status.equals("ReturnedLate") || status.equals("Late") || status.equals("Lost")) {
						int daysLate = (int) ChronoUnit.DAYS.between(expectedReturnDate, actualReturnDate != null ? actualReturnDate : now);
						daysLate = Math.abs(daysLate); // Ensure the value is positive
						lateDaysMap.computeIfAbsent(bookTitle, k -> new ArrayList<>()).add(daysLate);
					}

					// Count total books borrowed this month
					if (borrowDate.getMonthValue() == now.getMonthValue() && borrowDate.getYear() == now.getYear()) {
						borrowCountMap.put(bookTitle, borrowCountMap.getOrDefault(bookTitle, 0) + 1);
					}
				}

				// Calculate averages and create report entries
				for (String bookTitle : heldDaysMap.keySet()) {
					List<Integer> heldDays = heldDaysMap.get(bookTitle);
					double averageHeldDays = heldDays.stream().mapToInt(Integer::intValue).average().orElse(0.0);
					report.add(new BorrowTimeReport(bookTitle, "Returned", averageHeldDays));
				}

				for (String bookTitle : lateDaysMap.keySet()) {
					List<Integer> lateDays = lateDaysMap.get(bookTitle);
					double averageLateDays = lateDays.stream().mapToInt(Integer::intValue).average().orElse(0.0);
					report.add(new BorrowTimeReport(bookTitle, "Late", averageLateDays));
				}

				for (String bookTitle : borrowCountMap.keySet()) {
					int borrowCount = borrowCountMap.get(bookTitle);
					report.add(new BorrowTimeReport(bookTitle, "BorrowedThisMonth", borrowCount));
				}
			}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }

    public static List<SubscriberStatusReport> fetchSubscriberStatusReport(Connection conn, LocalDate now) {
        List<SubscriberStatusReport> report = new ArrayList<>();
        String query = "SELECT Status, COUNT(SubID) AS Count " +
                       "FROM subscribers " +
                       "WHERE MONTH(Joined) = ? AND YEAR(Joined) = ? " +
                       "GROUP BY Status";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, now.getMonthValue());
            stmt.setInt(2, now.getYear());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("Status");
                    int count = rs.getInt("Count");
                    report.add(new SubscriberStatusReport(status, count));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }
}


