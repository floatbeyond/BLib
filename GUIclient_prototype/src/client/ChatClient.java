// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.ChatIF;
import common.Subscriber;
import common.SubscriberReport;
import common.ServerMessage;
import common.BookCopy;
import common.BorrowRecordDTO;
import common.BorrowReport;
import common.OrderRecordDTO;
import common.Notification;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  public static boolean awaitResponse = false;
  // public static SubscribersTableController stc;
  // public static ClientPortController cpc;
  public static int connectionStatusFlag = 0;

/**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;


  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable. 
   **/
  public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */

// filepath: /c:/Users/alone/Desktop/G16_GUI_prototype_VS/GUIclient_prototype/src/client/ChatClient.java
  public void handleMessageFromServer(Object msg) {
      System.out.println("--> handleMessageFromServer");
      // System.out.println("Received message type: " + msg.getClass().getName());

      if (msg instanceof ServerMessage) {
          ServerMessage serverMessage = (ServerMessage) msg;
          String user = serverMessage.getUser();
          String type = serverMessage.getType();
          Object data = serverMessage.getData();
          // print server type
          System.out.println("Received message type: " + type + " | to user: " + user);
          awaitResponse = false;
          switch (type) {
                case "LoginStatus":
                    Logic.parseLogin(data);
                    break;
                case "NewNotifications":
                    try {
                        Logic.handleNotifications((List<Notification>) data);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        System.out.println("Error deserializing notifications: " + e.getMessage());
                    }
                    break;
                case "NewSubscriber":
                    Logic.newSubscriber((int) data);
                    break;
                case "foundSubscriber":
                    if (data instanceof Subscriber) {
                        Logic.parseSubscriber((Subscriber) data);
                    }
                    break;
                case "UpdateStatus":
                    Logic.updateSubscriberStatus( data);
                    break;
                case "SubReactivated":
                    Logic.reactivateSubscriberStatus((String) data);
                    break;
                case "SubscriberList":
                    Logic.parseSubscriberList((ArrayList<Subscriber>) data);
                    break;
                case "DataLogsList":
                    Logic.parseDataLogsList(user, (ArrayList<Object>) data);
                    break;
                case "UserBorrowsList":
                    Logic.parseUserBorrowsList(user, (List<BorrowRecordDTO>) data);
                    break;
                case "UserOrdersList":
                    Logic.parseUserOrdersList(user, (List<OrderRecordDTO>) data);
                    break;
                case "BookList":
                    Logic.parseBookList(user, (ArrayList<Object>) data);
                    break;
                case "BookCopy":
                    Logic.parseBookCopy(user, (BookCopy) data);
                    break;
                case "BorrowStatus":
                    Logic.newBorrowStatus((String) data);
                    break;
                case "ReturnStatus":
                    Logic.returnBookStatus((String) data);
                    break;
                case "LostStatus": 
                    Logic.lostBookStatus((String) data);
                    break;
                case "ExtendStatus":
                    Logic.extendBorrowStatus((String) data);
                    break;
                case "OrderStatus":
                    Logic.newOrderStatus(data);
                    break;
                case "CancelStatus":
                    Logic.cancelOrderStatus((String) data);
                    break;
                case "AllBorrowReports":
                    Logic.parseAllBorrowReports((Map<String, List<BorrowReport>>) data);
                    break;
                case "AllSubscriberReports":
                    Logic.parseAllSubscriberReports((Map<String, List<SubscriberReport>>) data);
                    break;
                case "Print":
                    Logic.print((String) data);
                    break;
                case "Error":
                    Logic.printError((String) data);
                    break;
                default:
                    System.out.println("Unknown message type: " + type);
                    break;
          }
      } else {
          System.out.println("Received unknown message format");
      }
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(Object message)  
  {
    if (message instanceof ServerMessage) {
      ServerMessage serverMessage = (ServerMessage) message;
      String type = serverMessage.getType();
      Object data = serverMessage.getData();
      System.out.println("ChatClient: Sending message, Case: " + type + " | data: " + data);
      
	    try {
            if (!isConnected()) {
                try {
                    openConnection();
                    connectionStatusFlag = 1; // Set flag to 1 for success
                } catch (IOException e) {
                    System.out.println("ChatClient: Error opening connection: " + e.getMessage());
                    connectionStatusFlag = 0; // Set flag to 0 for failure
                    e.printStackTrace();
                }
                // print client connected
                System.out.println("ChatClient: Connection successful");
            }

        sendToServer(serverMessage);

        if (type.equals("disconnect") || type.equals("quit")) {
            closeConnection();
            connectionStatusFlag = 0; // Set flag to 0 for failure
            // print client disconnected
            System.out.println("ChatClient: Connection closed");
            awaitResponse = false;
          
        } else {
          System.out.println("ChatClient: Setting awaitResponse to true");
          awaitResponse = true;
        }

        long startTime = System.currentTimeMillis();
        while (awaitResponse) {
            if (System.currentTimeMillis() - startTime > 5000) {
                System.out.println("ChatClient: Response timeout");
                awaitResponse = false;
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("ChatClient: Finished waiting for response");
      } catch(IOException e) {
          System.out.println("ChatClient: Error sending message: " + e.getMessage());
          if (e.getMessage().equals("Connection timed out: connect") || e.getMessage().equals("Connection refused: connect")) {
            connectionStatusFlag = 0; // Set flag to 0 for failure
            }  
          e.printStackTrace();
      }
    } else {
      System.out.println("ChatClient: Unknown message format");
    }
  }

  /**
   * This method gets the connection status flag.
   *
   * @param connectionStatusFlag The connection status flag.
   */
  public int getConnectionStatusFlag() { return connectionStatusFlag; }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }


  @Override
  protected void connectionClosed() {
      System.out.println("Connection closed");
  }


  @Override
  protected void connectionException(Exception exception) {
      System.out.println("Connection exception: " + exception.getMessage());
  }

  @Override
  protected void connectionEstablished() {
      System.out.println("Connection established");
  }


}
//End of ChatClient class
