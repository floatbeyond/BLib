package server;

import javafx.application.Application;
import javafx.stage.Stage;
import gui.SqlConnectionController;

/**
 * The ServerUI class is the entry point for the server application.
 * It initializes the server and starts the GUI for SQL connection.
 */
public class ServerUI extends Application {
	private static ReportScheduler reportScheduler;
	final public static int DEFAULT_PORT = 5555;

	/**
     * The main method launches the JavaFX application.
     *
     * @param args command line arguments
     * @throws Exception if an error occurs during application launch
     */
	public static void main( String args[] ) throws Exception {   
		launch(args);
	}
	
	/**
     * The start method initializes the primary stage and starts the SQL connection controller.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs during initialization
     */
	@Override
	public void start(Stage primaryStage) throws Exception {		  		
		SqlConnectionController serverFrame = new SqlConnectionController();
		serverFrame.start(primaryStage);

	}
	
	/**
     * The runServer method starts the server on the specified port.
     * It also initializes the report scheduler.
     *
     * @param p the port number as a string
     */
	public static void runServer(String p) {
		int port = 0; //Port to listen on

	    try {
	        port = Integer.parseInt(p); //Set port to 5555
	    } catch(Throwable t) {
	        System.out.println("ERROR - Could not connect!");
	    }

	    EchoServer sv = new EchoServer(port);
		// Initialize the report scheduler
	        
	    try {
			reportScheduler = new ReportScheduler();
			InstanceManager.setReportScheduler(reportScheduler);
			sv.listen(); //Start listening for connections
	    } catch (Exception ex) {
	        System.out.println("ERROR - Could not listen for clients!");
	    }
	}

}
