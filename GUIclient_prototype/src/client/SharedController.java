package client;

import gui.SubscribersTableController;
import gui.ClientPortController;
import gui.LandingWindowController;
import gui.SubscriberFormController;
import gui.LoginWindowController;
import common.Subscriber;
import common.Librarian;
import gui.SubscriberMainFrameController;

public class SharedController {
    private static Subscriber subscriber;
    private static Librarian librarian;
    public static SubscribersTableController stc;
    public static ClientPortController cpc;
    public static SubscriberFormController sfc;
    public static LandingWindowController lwc;
<<<<<<< HEAD
    public static LoginWindowController logwc;
<<<<<<< HEAD
=======
    public static SubMainFrameController smfc;
>>>>>>> 2270b72 (submainframecontrolller java and fxml)
=======
    public static SubscriberMainFrameController smfc;
>>>>>>> 6b00fd9 (Connected login to subscriber main frame)

    // Setters and getters
 
    public static void setSubscriber(Subscriber sub) { subscriber = sub; }
    public static Subscriber getSubscriber() { return subscriber; }

    public static void setLibrarian(Librarian lib) { librarian = lib; }
    public static Librarian getLibrarian() { return librarian; }

    public static void setSubscribersTableController(SubscribersTableController controller) { stc = controller; }
    public static SubscribersTableController getSubscribersTableController() { return stc; }

    public static void setClientPortController(ClientPortController controller) { cpc = controller; }
    public static ClientPortController getClientPortController() { return cpc; }

    public static void setSubscriberFormController(SubscriberFormController controller) { sfc = controller; }
    public static SubscriberFormController getSubscriberFormController() { return sfc; }

    public static void setLandingWindowController(LandingWindowController controller) { lwc = controller; }
    public static LandingWindowController getLandingWindowController() { return lwc; }

    public static void setLoginWindowController(LoginWindowController controller) { logwc = controller; }
    public static LoginWindowController getLoginWindowController() { return logwc; }

<<<<<<< HEAD
    public static SubscriberFormController getSubscriberFormController() {
      return sfc;
    }

    public static void setLandingWindowController(LandingWindowController controller) {
      lwc = controller;
    }

    public static LandingWindowController getLandingWindowController() {
      return lwc;
    }

    public static void setSubMainFrameController(SubMainFrameController controller) {
      smfc = controller;
    }
    public static SubMainFrameController getSubMainFrameController() {
      return smfc;
     
    }
=======
    public static void setSubscriberMainFrameController(SubscriberMainFrameController controller) { smfc = controller; }
    public static SubscriberMainFrameController getSubscriberMainFrameController() { return smfc; }
>>>>>>> 6b00fd9 (Connected login to subscriber main frame)


}




  