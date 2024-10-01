package dateRecognizerTestbed;

/**
 * <p> FSM-translated DateRecognizer. </p>
 * 
 * <p> Description: A demonstration of the mechanical translation of Finite State Machine 
 * diagram into an executable Java program using the Date Recognizer. The code 
 * detailed design is based on a while loop with a select list</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2024 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2024-09-13	Initial baseline derived from the Even Recognizer
 */
public class DateRecognizer {

	/*
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	/** Empty if no error, otherwise, it contains the error message. */
	public static String dateRecognizerErrorMessage = "";
	
	/** The String that holds the date the class will process */
	public static String dateRecognizerInput = "";
	
	/** If -1, there is no index for an error if there is one.  Otherwise it is the index of the
	 * character where an error was detected.
	 */
	public static int dateRecognizerIndexofError = -1;

	// The private variable for the recognizer
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running
	private static int dateValueSize = 0;			// A numeric value may not exceed 16 characters
	
	/** Default constructor (There are no special actions). */
	public DateRecognizer() {
	}

	/**********
	 * This private method display the input line and then on a line under it displays an up arrow
	 * at the point where an error should one be detected.  This method is designed to be used to 
	 * display the error message on the console terminal.
	 * 
	 * @param input				The input string
	 * @param currentCharNdx	The location where an error was found
	 * @return					Two lines, the entire input line followed by a line with an up arrow
	 */
	private static String displayInput(String input, int currentCharNdx) {
		// Display the entire input line
		String result = input.substring(0,currentCharNdx) + "?\n";

		return result;
	}

	// Private method to display debugging data
	private static void displayDebuggingInfo() {
		// Display the current state of the FSM as part of an execution trace
		if (currentCharNdx >= inputLine.length())
			// display the line with the current state numbers aligned
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "None");
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
					((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
					nextState + "     " + dateValueSize);
	}
	
	// Private method to move to the next character within the limits of the input line
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it is a String
	 * 						with a helpful description of the error
	 */
	public static String checkForValidDate(String input) {
		if(input.length() <= 0) return "";
		// The following are the local variable used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		dateRecognizerInput = input;		// Save a copy of the input
		running = true;						// Start the loop
		nextState = -1;						// There is no next state
		System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// Reset the Date Value size
				dateValueSize = 0;

				// State 0 has 3 valid transition that are addressed by an if statement.
				
				// The current character must be checked against 10 options. If any are matched
				// the FSM must go to one of three states
				// 1, 2 -> State 1
				if (currentChar == '1' || currentChar == '2' ) {		// Check for 1 and 2
					nextState = 1;
					
					// Count the first digit of a day
					dateValueSize++;
				}
				// 0 -> State 2
				else if (currentChar == '0') {							// Check for zero
					nextState = 2;
					
					// Count a leading zero of a day
					dateValueSize++;								
				}
				// 3 -> State 3
				else if (currentChar == '3' ) {							// Check for 3
					nextState = 3;
					
					// Count the digit 3 of a day
					dateValueSize++;				
				}				
				// 4-9 -> State 3
				else if (currentChar >= '4' && currentChar <= '9') {	// Check for 4-9
					nextState = 4;
					
					// Count the character when the currentChar is one of the range 4-9
					dateValueSize++;				
				}				
				// If it is none of those characters, the FSM halts
				else 
					running = false;
				
				// The execution of this state is finished
				break;
			
			case 1: 
				// State 1 has two valid transitions, a 0-9 digit that transitions to
				// state 5, or a / that transitions to state 6 

				// 0-9 -> State 5
				if (currentChar >= '0' && currentChar <= '9') {			// Check for 0-9
					nextState = 5;
					
					// Count the second digit of a day
					dateValueSize++;
				}
				// / -> State 6
				else if (currentChar == '/') {							// Check for /
					nextState = 6;
					
					// Count the / between a day and a month
					dateValueSize++;

				}				
				// If it is none of those characters, the FSM halts
				else
					running = false;
				
				// The execution of this state is finished
				break;			
				
			case 2: 
				// State 2 deals with digits 1-9.
				
				// The current character is must be checked against 9 options. If any are matched
				// the FSM must go to state 4
				// 1-9 -> State 1
				if (currentChar >= '1' && currentChar <= '9') {
					nextState = 4;
					
					// Count the non-zero digit after a leading zero of a day
					dateValueSize++;

				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
	
			case 3:
				// State 3 has two valid transition.  Each is addressed by an if statement.
				
				// 0, 1 -> State 4
				if (currentChar == '0' || currentChar == '1') {			// Check for 0, 1
					nextState = 4;
					
					// Count the second day digit
					dateValueSize++;
				}
				// / -> State 6
				else if (currentChar == '/') {							// Check for /
					nextState = 6;
					
					// Count the / between a day and a month
					dateValueSize++;				
				}				

				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 4:
				// State 4 has one valid transition.
				
				// / -> State 6
				if (currentChar == '/') {								// Check for /
					nextState = 6;
					
					// Count the / between the day and the month
					dateValueSize++;
				}

				// If it is not that character, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 5:
				// State 5 has one valid transition.
				
				// / -> State 6
				if (currentChar == '/') {								// Check for /
					nextState = 6;
					
					// Count the / between the day and the month
					dateValueSize++;
				}

				// If it is not that character, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 6:
				// State 6 has three valid transition.  Each is addressed by an if statement.
				
				// 0 -> State 7
				if (currentChar == '0') {								// Check for 0
					nextState = 7;
					
					// Count a leading month digit of zero
					dateValueSize++;
				}
				// 1 -> State 9
				else if (currentChar == '1') {							// Check for 1
					nextState = 9;
					
					// Count a leading month digit of 1
					dateValueSize++;				
				}
				// 2-9 -> State 8
				else if (currentChar >= '2' && currentChar <= '9') {	// Check for 2-9
					nextState = 8;
					
					// Count a leading month digit of 2-9
					dateValueSize++;				
				}				
				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 7:
				// State 7 has one valid transition.
				
				// 1-9 -> State 8
				if (currentChar >= '1' && currentChar <= '9') {			// Check for 1-9
					nextState = 8;
					
					// Count the second digit of the month with a leading zero
					dateValueSize++;
				}

				// If it is not that characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 8:
				// State 8 has one valid transition.
				
				// / -> State 10
				if (currentChar == '/') {								// Check for /
					nextState = 10;
					
					// Count the / between the month and the year
					dateValueSize++;
				}

				// If it is not that character, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 9:
				// State 9 has two valid transition.
				
				// / -> State 10
				if (currentChar == '/') {								// Check for /
					nextState = 10;
					
					// Count the / between the month and the year
					dateValueSize++;
				}
				// 0-2 -> State 8
				else if (currentChar >= '0' && currentChar <= '2') {	// Check for 0-2
					nextState = 8;
					
					// Count the second digit of a two digit month
					dateValueSize++;				
				}				

				// If it is not that character, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 10:
				// State 10 has one valid transition.
				
				// 1, 2 -> State 10
				if (currentChar == '1' || currentChar == '2') {			// Check for 1, 2
					nextState = 11;
					
					// Count the first digit of a four digit year
					dateValueSize++;
				}

				// If it is not that character, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 11:
				// State 11 has one valid transition.
				
				// 0-9 -> State 12
				if (currentChar >= '0' && currentChar <= '9') {			// Check for 0-9
					nextState = 12;
					
					// Count the second digit of a four digit year
					dateValueSize++;
				}

				// If it is not that character, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 12:
				// State 12 has one valid transition.
				
				// 0-9 -> State 13
				if (currentChar >= '0' && currentChar <= '9') {			// Check for 0-9
					nextState = 13;
					
					// Count the third digit of a four digit year
					dateValueSize++;
				}

				// If it is not that character, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 13:
				// State 13 has one valid transition.
				
				// 0-9 -> State 14
				if (currentChar >= '0' && currentChar <= '9') {			// Check for 0-9
					nextState = 14;
					
					// Count the fourth digit of a four digit year
					dateValueSize++;
				}

				// If it is not that character, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;
				
			case 14:
				// State 14 has no transitions and is a final state.
					running = false;

				// The execution of this state is finished
				break;
			}
			
			if (running) {
				displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next character
				// in the input and if there is one, it fetches that character and updates the 
				// currentChar.  If there is no next character the currentChar is set to a blank.
				moveToNextCharacter();
				
				// Let's ensure the date is not too long
				if (dateValueSize > 10) {
					dateRecognizerErrorMessage = "A valid date value must be no longer than 10 characters.\n";
					return dateRecognizerErrorMessage + displayInput(input, 10);
				}

				// Move to the next state
				state = nextState;
				
				// Is the new state a final state?  If so, signal this fact.
				if (state == 14) finalState = true;
				
				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again
	
		}
		displayDebuggingInfo();
		
		System.out.println("The loop has ended.");

		dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		
		// The following code is for both console and GUI output.		
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "A date must start with a numeric digit.\n";
			return dateRecognizerErrorMessage;

		case 1:
			// State 1 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After a digit of 1 or 2, the only valid digits in a day are 0 through 9.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 2:
			// State 2 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After a digit of 0, the only valid digits in a day are 1 through 9.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 3:
			// State 3 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After a digit of 3, the only valid digits in a day are 0 or 1.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 4:
			// State 4 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After the day, the only valid character is a /.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 5:
			// State 5 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After the day, the only valid character is a /.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 6:
			// State 6 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After a /, the only valid digits in a month are 0 through 9.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 7:
			// State 7 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After a leading zero, the only valid digits in a month are 1 through 9.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 8:
			// State 8 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After a month, the only valid character is a /.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 9:
			// State 9 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After a 1 in a month, the only valid characters are 0, 1, 2, or /.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 10:
			// State 10 is not a final state, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "After the / following a month, the only valid character are 1 or 2.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 11:
			// States 11is not a final states, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "The second through fourth digits in a year must be 0 through 9.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 12:
			// States 12 is not a final states, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "The second through fourth digits in a year must be 0 through 9.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);

		case 13:
			// States 13 is not a final states, so we can return a very specific error message
			dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
			dateRecognizerErrorMessage = "The second through fourth digits in a year must be 0 through 9.\n";
			return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);
						
		case 14:
			// State 3 is a Final State, so this is not an error if the input is empty.
			if (currentCharNdx<input.length()) {
				// If not all of the string has been consumed, we point to the current character
				// in the input line and specify what that character must be in order to move
				// forward.
				dateRecognizerIndexofError = currentCharNdx;		// Copy the index of the current character;
				dateRecognizerErrorMessage = "A year may only have four digits.\n";
				return dateRecognizerErrorMessage + displayInput(input, currentCharNdx);
			}
			else 
			{
				// This code disables the output if we go past the end of the input
				// This should not happen
				dateRecognizerIndexofError = -1;
				dateRecognizerErrorMessage = "";
				return dateRecognizerErrorMessage;
			}
			
		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should not happen
			return "";
		}
	}
}
