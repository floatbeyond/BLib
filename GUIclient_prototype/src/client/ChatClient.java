// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.ChatIF;
import common.Subscriber;
import gui.ClientPortController;
import gui.SubscribersTableController;
import common.ServerMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
  
  public static Subscriber s1 = new Subscriber(0, "" ,0 , "", "");
  public static boolean awaitResponse = false;
  public static SubscribersTableController stc;
  public static ClientPortController cpc;
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
//   public void handleMessageFromServer(Object msg) {
//       System.out.println("--> handleMessageFromServer");
//       System.out.println("Received message type: " + msg.getClass().getName());

//       if (msg instanceof Subscriber) {
//           Subscriber receivedSubscriber = (Subscriber) msg;
//           System.out.println("Received Subscriber: " + receivedSubscriber);
//           ChatClient.s1 = receivedSubscriber;
//       } else if (msg instanceof String) {
//           String message = (String) msg;
//           if (message.equals("Error: Subscriber ID Not Found")) {
//               System.out.println("Subscriber not found");
//               ChatClient.s1 = null;
//           } else if (message.equals("ERROR: Invalid command")) {
//               System.out.println("Invalid command");
//           }
//           System.out.println("Received message: " + message);
//       } else if (msg instanceof ArrayList<?>) {
//           ArrayList<?> list = (ArrayList<?>) msg;
//           System.out.println("Received ArrayList of size: " + list.size());

//           if (!list.isEmpty() && list.stream().allMatch(item -> item instanceof Subscriber)) {
//               System.out.println("Loading table");
//               List<Subscriber> subscriberList = (List<Subscriber>) list;
//               ObservableList<Subscriber> subscribers = FXCollections.observableArrayList(subscriberList);
//               if (stc != null) {
//                 System.out.println("subscribersTableController is initialized");
//                 stc.parseSubscriberList(subscribers);
//               } else {
//                 System.out.println("subscribersTableController is null");
//               }
//           }
//       } else {
//           System.out.println("Received unknown message type: " + msg);
//       }
//       awaitResponse = false;
// }
// filepath: /c:/Users/alone/Desktop/G16_GUI_prototype_VS/GUIclient_prototype/src/client/ChatClient.java
  public void handleMessageFromServer(Object msg) {
      System.out.println("--> handleMessageFromServer");
      // System.out.println("Received message type: " + msg.getClass().getName());

      if (msg instanceof ServerMessage) {
          ServerMessage serverMessage = (ServerMessage) msg;
          String type = serverMessage.getType();
          Object data = serverMessage.getData();
          // print server type
          System.out.println("Received message type: " + type);
          awaitResponse = false;
          switch (type) {
              case "Subscriber":
                  if (data instanceof Subscriber) {
                      Subscriber receivedSubscriber = (Subscriber) data;
                      System.out.println("Received Subscriber: " + receivedSubscriber);
                      ChatClient.s1 = receivedSubscriber;
                  }
                  break;
              case "SubscriberList":
                  if (data instanceof ArrayList<?>) {
                      ArrayList<?> list = (ArrayList<?>) data;
                      System.out.println("Received ArrayList of size: " + list.size());

                      if (!list.isEmpty() && list.stream().allMatch(item -> item instanceof Subscriber)) {
                          System.out.println("Loading table");
                          List<Subscriber> subscriberList = (List<Subscriber>) list;
                          // Handle the subscriber list
                          ObservableList<Subscriber> subscribers = FXCollections.observableArrayList(subscriberList);
                          if (stc != null) {
                            System.out.println("subscribersTableController is initialized");
                            stc.parseSubscriberList(subscribers);
                          } else {
                            System.out.println("subscribersTableController is null");
                        }
                      }
                  }
                  break;
              case "Print":
                  if (data instanceof String) {
                      String message = (String) data;
                      System.out.println("Received message: " + message);
                  }
                  break;
              case "Error":
                  if (data instanceof String) {
                      String errorMessage = (String) data;
                      if (errorMessage.equals("Subscriber ID Not Found")) {
                          System.out.println("Subscriber not found");
                          ChatClient.s1 = null;
                      } else if (errorMessage.equals("Invalid command")) {
                          System.out.println("Invalid command");
                      }
                      System.out.println("Received error message: " + errorMessage);
                  }
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
  public void handleMessageFromClientUI(String message)  
  {
	    try {
        System.out.println("ChatClient: Sending to server: " + message);
        //if (message.equals("details")) {
        openConnection();
        connectionStatusFlag = 1; // Set flag to 1 for success
            // print client connected
        System.out.println("ChatClient: Connection successful");
        //}

        if (message.equals("disconnect")) {
            closeConnection();
            connectionStatusFlag = 0; // Set flag to 0 for failure
            // print client disconnected
            System.out.println("ChatClient: Connection closed");
        }

        System.out.println("ChatClient: Setting awaitResponse to true");
        awaitResponse = true;
        sendToServer(message);

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
        }  // if (cpc != null) {
          //   cpc.displayMessage("Invalid IP");
        // } else {
        e.printStackTrace();
        }
        
        //quit();
  }

  public static void setSubscribersTableController(SubscribersTableController controller) {
    stc = controller;
  }

  public static void setClientPortController(ClientPortController controller) {
    cpc = controller;
  }

  public int getConnectionStatusFlag() {
    return connectionStatusFlag;
}

  /**
   * This method terminates the client.
   */

  // public void setupConnection() {
  //   try {

  //   } catch (IOException e) {
  //     System.err.println("Error: Can't connect to server! Terminating client.");
  //     System.exit(1);
  //   }
  // }



  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
