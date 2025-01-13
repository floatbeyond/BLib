// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package server;

import java.net.InetAddress;
import java.net.UnknownHostException;

<<<<<<< HEAD
=======
import common.MessageUtils;
>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
import common.ServerMessage;
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
  private static ClientConnectedController ccc;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) {
    super(port);
	InstanceManager.setInstance(this);
	InstanceManager.setClientConnectedController(ccc);
  }
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
	
	
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		//System.out.println("Message received: " + msg + " from " + client);
    // ccc = InstanceManager.getClientConnectedController();
    
		  if (msg instanceof ServerMessage) {
        ServerMessage serverMessage = (ServerMessage) msg;
        String user = serverMessage.getUser();
        String type = serverMessage.getType();
        Object data = serverMessage.getData();

<<<<<<< HEAD
        System.out.println("Received message, Case: " + type + " from user: " + user);
=======
        System.out.println("Received message, Case: " + type + " | from user: " + user);
>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
        try {
          switch (type) {
            case "login" : {
              Logic.userLogin(user, (int) data, client);
              break;
<<<<<<< HEAD
            }
            case "updateSubscriber": {
=======
            } case "newSubscriber": {
              Logic.newSubscriber(user, data, client);
              break;
            } case "updateSubscriber": {
>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
              Logic.updateSubscriberDetails(user, client);
              break;
            } case "showSubscribersTable": {
              Logic.showSubscribersTable(user, client);
              break;
            } case "sendSubscriber": {
              Logic.specificSubscriber(user, Integer.parseInt(data.toString()), client);
              break;
            } case "sendSearchedBooks": {
              String searchCriteria = (String) data;
              String[] parts = searchCriteria.split(":", 2);
              String searchType = parts[0];
              String searchText = parts[1];
<<<<<<< HEAD
              // print search type and text
=======
>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
              Logic.sendSearchedBooks(user, searchType, searchText, client);
              break;
            } case "scanBookCopy":{
              Logic.scan(user, type, Integer.parseInt(data.toString()), client);
              break;
<<<<<<< HEAD
=======
            } case "newBorrow": {
              // print test
              System.out.println("New borrow");
              Logic.newBorrow(user, data, client);
              break;
>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
            } case "connect": {
              Logic.connect(user, client);
              break;
            } case "disconnect": {
              Logic.disconnect(user, client);
              break;
<<<<<<< HEAD
            }case "sendBooksInOrderCount": {
              Logic.getBooksInOrderCount(user,(int) data, client);
              break;
            } case "sendBooksInBorrowCount":
              Logic.getBooksInBorrowCount(user,(int) data, client);
              break;
            default: {
=======
            } default: {
>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
              MessageUtils.sendResponseToClient(user, "Error", "Invalid command", client);
              break;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("Unknown message type: " + msg.getClass().getName());
      }
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
