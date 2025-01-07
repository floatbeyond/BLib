package client;

import gui.SubscribersTableController;
import gui.ClientPortController;
import common.Subscriber;

public class SharedController {
    private static Subscriber subscriber;
    public static SubscribersTableController stc;
    public static ClientPortController cpc;

    // Setters and getters

    public static void setSubscriber(Subscriber sub) {
        subscriber = sub;
    }

    public static Subscriber getSubscriber() {
        return subscriber;
    }

    public static void setSubscribersTableController(SubscribersTableController controller) {
      stc = controller;
    }

    public static SubscribersTableController getSubscribersTableController() {
      return stc;
    }
  
    public static void setClientPortController(ClientPortController controller) {
      cpc = controller;
    }

    public static ClientPortController getClientPortController() {
      return cpc;
    }
}




  