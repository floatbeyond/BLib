// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import common.MessageUtils;
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
		  if (msg instanceof ServerMessage) {
        ServerMessage serverMessage = (ServerMessage) msg;
        String user = serverMessage.getUser();
        String type = serverMessage.getType();
        Object data = serverMessage.getData();

        System.out.println("Received message, Case: " + type + " | from user: " + user);
        try {
          switch (type) {
            case "login" : {
              Logic.userLogin(user, (int) data, client);
              break;
            } case "fetchNotifications": {
              Logic.fetchNotifications(user, data, client);
              break;
            } case "newSubscriber": {
              Logic.newSubscriber(user, data, client);
              break;
            } case "updateSubscriber": {
              Logic.updateSubscriberDetails(user, data, client);
              break;
            } case "reactivateSubscriber": {
              Logic.reactivateSubscriber(user, data, client);
              break;
            } case "showSubscribersTable": {
              Logic.showSubscribersTable(user, client);
              break;
            } case "sendSubscriber": {
              Logic.specificSubscriber(user, Integer.parseInt(data.toString()), client);
              break;
            } case "userLogs": {
              Logic.sendDataLogs(user, Integer.parseInt(data.toString()), client);
              break;
            } case "userBorrows": {
              Logic.sendUserBorrows(user, data, client);
              break;
            } case "userOrders": {
              Logic.sendUserOrders(user, data, client);
              break;
            } case "sendSearchedBooks": {
              Logic.sendSearchedBooks(user, data, client);
              break;
            } case "scanBookCopy":{
              Logic.scan(user, type, Integer.parseInt(data.toString()), client);
              break;
            } case "newBorrow": {
              Logic.newBorrow(user, data, client);
              break;
            } case "returnBook": {
              Logic.returnBook(user, data, client);
              break;
            } case "returnLostBook": {
              Logic.returnLostBook(user, data, client);
              break;
            } case "markLost": {
              Logic.lostBook(user, data, client);
            } case "newOrder": {
              Logic.newOrder(user, data, client);
              break;
            } case "checkOrder": {
              Logic.checkOrder(user, data, client);
              break;
            } case "cancelOrder": {
              Logic.cancelOrder(user, data, client);
              break;
            } case "fetchAllReports": {
              Logic.fetchMonthlyReports(user, client);
            } case "connect": {
              Logic.connect(user, client);
              break;
            } case "disconnect": {
              Logic.disconnect(user, client);
              break;
            }default: {
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
