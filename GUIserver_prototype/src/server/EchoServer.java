// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package server;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.net.UnknownHostException;
import common.ServerMessage;
import common.Subscriber;
import gui.ClientConnectedController;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  public static EchoServer instance;
  private static Connection conn;
  private static ClientConnectedController ccc;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) {
    super(port);
	instance = this;
  }
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
//   public void handleMessageFromClient (Object msg, ConnectionToClient client) {

// 	  	String message = (String) msg;
// 		conn = getDbConnection();
	    
// 		System.out.println("Message received: " + msg + " from " + client);
// 		if (message.startsWith("change")) {
// 			try {
// 				String[] parts = message.split(" "); 
// 				int subscriberId = Integer.parseInt(parts[1]);
// 				// String subscriberName = parts[2] + " " + parts[3];
// 				// int subscriberHistory = Integer.parseInt(parts[4]);
// 		        String phoneNumber = parts[5];
// 		        String email = parts[6];
				
// 	            if (mysqlConnection.updateSubscriber(conn, subscriberId, phoneNumber, email)) {
// 	                client.sendToClient("Subscriber updated successfully.");
// 	            } else {
// 	                client.sendToClient("ERROR: Couldn't update subscriber.");
// 	            }
// 			} catch (IOException e) {
// 				e.printStackTrace();
// 			}
// 	    } else if (message.startsWith("show")) {
// 	        try {
// 	            client.sendToClient("List of subscribers:");
// 	            ArrayList<Subscriber> table = mysqlConnection.getSubscribers(conn);
// 				client.sendToClient(table);
// 	        } catch (Exception e) {
// 	            try {
// 	                client.sendToClient("ERROR: Couldn't show subscribers table.");
// 	            } catch (IOException ioException) {
// 	                ioException.printStackTrace();
// 	            }
// 	        }
// 	    } else if (message.startsWith("details")) {
// 	        try {
// 	            String clientInfo = client.toString();
// 	            String connectionStatus = client.isAlive() ? "Connected" : "Disconnected";
// 				System.out.println("Client Info: " + clientInfo);
// 				System.out.println("Connection Status: " + connectionStatus);

// 				try {
// 					if (ccc == null) {
// 						ccc = getClientConnectedController();
// 					}
// 	           		ccc.loadClientDetails(clientInfo, connectionStatus);
// 					client.sendToClient("Client details loaded");
// 				} catch (Exception e) {
// 					e.printStackTrace();
// 				}
// 	        } catch (Exception e) {
// 	            e.printStackTrace();
// 	        }
// 		} 
// 		else if (message.startsWith("send")) {
// 			try {
// 				int subscriberId = Integer.parseInt(message.split(" ")[1]);
// 				Subscriber foundSubscriber = mysqlConnection.findSubscriber(conn, subscriberId);
// 				if (foundSubscriber != null) {
// 						System.out.println("Subscriber Found: " + foundSubscriber.toString());
// 						client.sendToClient(foundSubscriber);
// 						System.out.println("Subscriber sent");
// 				}
// 				else {
// 					System.out.println("Server: Subscriber not found");
// 					client.sendToClient("Error: Subscriber ID Not Found");
// 			    }
// 	        } catch (IOException e) {
// 	            e.printStackTrace();
// 	        }
// 		} else if (message.startsWith("disconnect")) {
// 			try {
// 				try {
// 					if (ccc == null) {
// 						ccc = getClientConnectedController();
// 					}
// 	           		ccc.loadClientDetails("null", "Disconnected");
// 					client.sendToClient("Server disconnected");
// 				} catch (Exception e) {
// 					e.printStackTrace();
// 				}
// 			} catch (Exception e) {
// 				e.printStackTrace();
// 			}
// 		} else {
// 	        try {
// 				client.sendToClient("ERROR: Invalid command");	    
// 			} catch (IOException e) {
// 	            e.printStackTrace();
// 	        }
// 	    }
// 	}

	
	
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Message received: " + msg + " from " + client);
	
		String message = (String) msg;
		conn = getDbConnection();
	
		try {
			if (message.startsWith("change")) {
				String[] parts = message.split(" ");
				int subscriberId = Integer.parseInt(parts[1]);
				String phoneNumber = parts[5];
				String email = parts[6];
	
				boolean success = mysqlConnection.updateSubscriber(conn, subscriberId, phoneNumber, email);
				ServerMessage response = new ServerMessage("UpdateStatus", success ? "Subscriber updated successfully." : "ERROR: Couldn't update subscriber.");
				client.sendToClient(response);
			} else if (message.startsWith("show")) {
				ArrayList<Subscriber> table = mysqlConnection.getSubscribers(conn);
				ServerMessage response = new ServerMessage("SubscriberList", table);
				client.sendToClient(response);
			} else if (message.startsWith("send")) {
				int subscriberId = Integer.parseInt(message.split(" ")[1]);
				Subscriber foundSubscriber = mysqlConnection.findSubscriber(conn, subscriberId);
	
				ServerMessage response = new ServerMessage("Subscriber", foundSubscriber != null ? foundSubscriber : "Error: Subscriber ID Not Found");
				client.sendToClient(response);
			} else if (message.startsWith("details")) {
				String clientInfo = client.toString();
				String connectionStatus = client.isAlive() ? "Connected" : "Disconnected";
	
				if (ccc == null) {
					ccc = getClientConnectedController();
				}
				ccc.loadClientDetails(clientInfo, connectionStatus);
	
				ServerMessage response = new ServerMessage("Print", "Client details loaded");
				client.sendToClient(response);
			} else if (message.startsWith("disconnect")) {
				if (ccc == null) {
					ccc = getClientConnectedController();
				}
				ccc.loadClientDetails("null", "Disconnected");
	
				ServerMessage response = new ServerMessage("Print", "Server disconnected");
				client.sendToClient(response);
			} else {
				ServerMessage response = new ServerMessage("Error", "ERROR: Invalid command");
				client.sendToClient(response);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static EchoServer getInstance() {
		return instance;
	}

	public static void setClientConnectedController(ClientConnectedController controller) {
        ccc = controller;
    }

	public static ClientConnectedController getClientConnectedController() {
        return ccc;
    }

	public static void setDbConnection(Connection connection) {
        conn = connection;
    }

    public static Connection getDbConnection() {
        return conn;
    }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	    try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ipAddress = inetAddress.getHostAddress();
			System.out.println("Server IP address: " + ipAddress);
            System.out.println("Server listening for connections on port " + getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println("Server has stopped listening for connections.");
  }
}
//End of EchoServer class
