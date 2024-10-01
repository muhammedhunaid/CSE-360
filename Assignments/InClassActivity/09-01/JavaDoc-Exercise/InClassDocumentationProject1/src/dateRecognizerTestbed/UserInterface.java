package dateRecognizerTestbed;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * <p> UserInterface Class. </p>
 * 
 * <p> Description: The Java/FX-based user interface testbed to develop and test UI ideas.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2024 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2024-09-13 The JavaFX-based GUI for the implementation of a testbed
 *  
 */

public class UserInterface {
	
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
	
	/* Constants used to parameterize the graphical user interface.  We do not use a layout manager
	   for this application. Rather we manually control the location of each graphical element for
	   exact control of the look and feel. */
	private final double BUTTON_WIDTH = 40;

	// These are the application values required by the user interface
	private Label label_ApplicationTitle = new Label("Date Recognizer Testbed");
	private Label label_DateValue = new Label("Enter a dd/mm/yyyy formatted date and then press 'Go'!");
	private TextField text_DateValue = new TextField();
	private Button button_Go= new Button("Go");
	private Label label_errDateValue = new Label("");	
    private Label noInputFound = new Label("");
	private TextFlow errDateValue;
    private Text errDateValuePart1 = new Text();
    private Text errDateValuePart2 = new Text();
    private Label validDateValue = new Label("");

	
	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

	/**********
	 * This method initializes all of the elements of the graphical user interface. These assignments
	 * determine the location, size, font, color, and change and event handlers for each GUI object.
	 * 
	 * @param theRoot  The Pane object to be used to holder the graphical user interface.
	 */
	public UserInterface(Pane theRoot) {
		
		// Label theScene with the name of the testbed, centered at the top of the pane
		setupLabelUI(label_ApplicationTitle, "Arial", 24, DateRecognizerTestbed.WINDOW_WIDTH, 
				Pos.CENTER, 0, 10);
		
		// Label the date input field with a title just above it, left aligned
		setupLabelUI(label_DateValue, "Arial", 14, DateRecognizerTestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 10, 50);
		
		// Establish the text input operand field and when anything changes in the text value,
		// the code will process the entire input to ensure that it is valid or in error.
		setupTextUI(text_DateValue, "Arial", 18, DateRecognizerTestbed.WINDOW_WIDTH-20,
				Pos.BASELINE_LEFT, 10, 70, true);
		text_DateValue.textProperty().addListener((observable, oldValue, newValue) 
				-> {resetDateValue(); });
		
		// Establish an error message for when there is no input
		noInputFound.setTextFill(Color.RED);
		noInputFound.setAlignment(Pos.BASELINE_LEFT);
		setupLabelUI(noInputFound, "Arial", 14, DateRecognizerTestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 10, 110);		
		
		// Establish an error message for the date value, left aligned
		label_errDateValue.setTextFill(Color.RED);
		label_errDateValue.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(label_errDateValue, "Arial", 14,  
						DateRecognizerTestbed.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 22, 126);		
		
		// Establish the Go button, position it, and link it to methods to accomplish its work
		setupButtonUI(button_Go, "Symbol", 24, BUTTON_WIDTH, Pos.BASELINE_LEFT, 
						DateRecognizerTestbed.WINDOW_WIDTH/2-BUTTON_WIDTH/2, 180);
		button_Go.setOnAction((event) -> { performGo(); });
		
		// Error Message components for the Email Address
		errDateValuePart1.setFill(Color.BLACK);
	    errDateValuePart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
	    errDateValuePart2.setFill(Color.RED);
	    errDateValuePart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
	    errDateValue = new TextFlow(errDateValuePart1, errDateValuePart2);
		errDateValue.setMinWidth(DateRecognizerTestbed.WINDOW_WIDTH-10); 
		errDateValue.setLayoutX(20);  
		errDateValue.setLayoutY(100);
		
		// Valid Email Address message
		validDateValue.setTextFill(Color.GREEN);
		validDateValue.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(validDateValue, "Arial", 18,  
						DateRecognizerTestbed.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 22, 126);				
		
		// Place all of the just-initialized GUI elements into the pane
		theRoot.getChildren().addAll(label_ApplicationTitle, label_DateValue, text_DateValue, 
				noInputFound, label_errDateValue, button_Go, errDateValue, validDateValue);

	}
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
	
	
	/**********************************************************************************************

	User Interface Actions
	
	**********************************************************************************************/
	// Reset the various variables used in the user interface
	private void resetDateValue() {
		label_errDateValue.setText("");
		noInputFound.setText("");
		errDateValuePart1.setText("");
		errDateValuePart2.setText("");
		validDateValue.setText("");
//		performGo();			// To debug, remove the comment "//" at the beginning of the line
	}
	
	// Perform the check of the date and return a detailed error message or an empty String if there
	// are no syntax errors in the input
	private void performGo() {
		// Fetch the input from the GUI
		String inputText = text_DateValue.getText();
		
		// If there is no input,, let the user know, but don't try to process it.
		if (inputText.isEmpty())
		    noInputFound.setText("No input text found!");
		else
		{
			// The input is not empty so use the recognizer to check the syntax
			String errMessage = DateRecognizer.checkForValidDate(inputText);
			
			// If the returned value is not empty, it is an error message, so display it on the GUI
			if (errMessage != "") {
				System.out.println(errMessage);
				label_errDateValue.setText(DateRecognizer.dateRecognizerErrorMessage);
				
				// Be specific where the error is by means of an up arrow (if the index is valid).
				if (DateRecognizer.dateRecognizerIndexofError <= -1) return;
				String input = DateRecognizer.dateRecognizerInput;
				errDateValuePart1.setText(input.substring(0, 
						DateRecognizer.dateRecognizerIndexofError));
				errDateValuePart2.setText("\u21EB");	// Unicode for an up arrow
			}
			else {
				// The returned value was an empty string, so the input was recognized with no
				// errors
				System.out.println("Success! The date is valid.");
				validDateValue.setText("Success! The date is valid.");
			}
		}
	}
}
