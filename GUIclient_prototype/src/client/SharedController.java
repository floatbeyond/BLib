package client;

import common.Subscriber;
import common.Librarian;
import common.BookCopy;
import gui.controllers.ClientPortController;
import gui.controllers.LandingWindowController;
import gui.controllers.LoginWindowController;
import gui.controllers.SubscriberFormController;
import gui.controllers.SubscriberMainFrameController;
import gui.controllers.SubscribersTableController;
import gui.controllers.ScanWindowController;
import gui.PersonalDetailsController;
import gui.controllers.BorrowFormController;

public class SharedController {
    private static Subscriber subscriber;
    private static Librarian librarian;
    public static SubscribersTableController stc;
    public static ClientPortController cpc;
    public static SubscriberFormController sfc;
    public static LandingWindowController lwc;
    public static LoginWindowController logwc;
    public static SubscriberMainFrameController smfc;
    public static BorrowFormController bfc;
    public static ScanWindowController swc;
    private static BookCopy bc;
    private static PersonalDetailsController pdc;

    // Setters and getters
 
    public static void setSubscriber(Subscriber sub) { subscriber = sub; }
    public static Subscriber getSubscriber() { return subscriber; }

    public static void setLibrarian(Librarian lib) { librarian = lib; }
    public static Librarian getLibrarian() { return librarian; }

    public static void setBookCopy(BookCopy bc) { SharedController.bc = bc; }
    public static BookCopy getBookCopy() { return bc; }

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

    public static void setBorrowFormController(BorrowFormController controller) { bfc = controller; }
    public static BorrowFormController getBorrowFormController() { return bfc; }

    public static void setScanWindowController(ScanWindowController controller) { swc = controller; }
    public static ScanWindowController getScanWindowController() { return swc; }
    
    public static void setPersonalDetailsController(PersonalDetailsController controller) { pdc = controller; }
    public static PersonalDetailsController getPersonalDetailsController() { return pdc; }

    public static void setSubscriberMainFrameController(SubscriberMainFrameController controller) { smfc = controller; }
    public static SubscriberMainFrameController getSubscriberMainFrameController() { return smfc; }

}




  