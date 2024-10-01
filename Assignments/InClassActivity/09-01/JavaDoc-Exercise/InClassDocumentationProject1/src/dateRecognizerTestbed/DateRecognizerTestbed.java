package dateRecognizerTestbed;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/*******
 * <p> DateRecognizerTestbed. </p>
 * 
 * <p> Description: A JavaFX demonstration application and baseline for a sequence of projects </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2024 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00	2024-09-13 The mainline of a JavaFX-based GUI implementation of a date
 * 					Recognizer testbed
 * 
 */

public class DateRecognizerTestbed extends Application {
	
	/** The width of the application's GUI interface. */
	public final static double WINDOW_WIDTH = 500;
	
	/** The height of the application's GUI interface. */
	public final static double WINDOW_HEIGHT = 250;
	
	/** An object to hold the reference to the UserInterface (but it is not used). */
	public UserInterface theGUI;
	
	/** Default constructor (There are no special actions). */
	public DateRecognizerTestbed() {
	}


	/**********
	 * This is the start method that is called once the application has been loaded into memory and is 
	 * ready to get to work.
	 * 
	 * In designing this application I have elected to IGNORE all opportunities for automatic layout
	 * support and instead have elected to manually position each GUI element and its properties in
	 * order to exercise complete control over the user interface look and feel.
	 * 
	 */
	@Override
	public void start(Stage theStage) throws Exception {
		
		theStage.setTitle("Lynn Robert Carter");			// Label the stage (a window)
		
		Pane theRoot = new Pane();							// Create a pane within the window
		
		theGUI = new UserInterface(theRoot);				// Create the Graphical User Interface
		
		Scene theScene = new Scene(theRoot, WINDOW_WIDTH, WINDOW_HEIGHT);	// Create the scene
															// using the specified width and height
		
		theStage.setScene(theScene);						// Set the scene on the stage
		
		theStage.show();									// Show the stage to the user
		
		// When the stage is shown to the user, the pane within the window is visible.  This means
		// that the labels, fields, and buttons of the Graphical User Interface (GUI) are visible 
		// and it is now possible for the user to select input fields and enter values into them, 
		// click on buttons, and read the labels, see the results, and see the error messages.
	}
	


	/*******************************************************************************************************/

	/*******************************************************************************************************
	 * This is the method that launches the JavaFX application
	 * 
	 * @param args This parameter holds the command line parameters.
	 * 
	 */
	public static void main(String[] args) {				// This method may not be required
		launch(args);										// for all JavaFX applications using
	}														// other IDEs.
}
