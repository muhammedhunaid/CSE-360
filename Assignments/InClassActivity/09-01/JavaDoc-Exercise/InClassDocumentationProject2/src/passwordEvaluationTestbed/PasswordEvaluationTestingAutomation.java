package passwordEvaluationTestbed;

/*
 * PasswordEvaluationTestingAutomation Class.
 * 
 * Description: A Java demonstration for semi- automation tests
 * 
 * Copyright: Muhammed Hunaid Topiwala © 2024
 * 
 * @author Muhammed Hunaid Topiwala
 * 
 * @version 1.00 2024-10-01 A set of semi-automated test cases
 */
public class PasswordEvaluationTestingAutomation {
	
	static int numPassed = 0;
	static int numFailed = 0;
	
	/*Default Constructor for the class */
	public PasswordEvaluationTestingAutomation() {
	}
	public static void main(String[] args) {
		System.out.println("____________________________________________________________________________");
		System.out.println("\nTesting Automation");
		
		performTestCase(1, "Aa!15678", true);

		performTestCase(2, "A!", false);
		
		performTestCase(3, "Aa!15678", false);
		
		performTestCase(4, "A!", true);
		performTestCase(5, "", true);
		
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}

	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		String resultText= PasswordEvaluator.evaluatePassword(inputText);
		
		System.out.println();
		
		if (resultText != "") {
			if (expectedPass) {
				System.out.println("***Failure*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			}
			else {			
				System.out.println("***Success*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		}
		
		else {	
			if (expectedPass) {	
				System.out.println("***Success*** The password <" + inputText + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			else {
				System.out.println("***Failure*** The password <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}
		displayEvaluation();
	}
	
	private static void displayEvaluation() {
		if (PasswordEvaluator.foundUpperCase)
			System.out.println("At least one upper case letter - Satisfied");
		else
			System.out.println("At least one upper case letter - Not Satisfied");

		if (PasswordEvaluator.foundLowerCase)
			System.out.println("At least one lower case letter - Satisfied");
		else
			System.out.println("At least one lower case letter - Not Satisfied");
	

		if (PasswordEvaluator.foundNumericDigit)
			System.out.println("At least one digit - Satisfied");
		else
			System.out.println("At least one digit - Not Satisfied");

		if (PasswordEvaluator.foundSpecialChar)
			System.out.println("At least one special character - Satisfied");
		else
			System.out.println("At least one special character - Not Satisfied");

		if (PasswordEvaluator.foundLongEnough)
			System.out.println("At least 8 characters - Satisfied");
		else
			System.out.println("At least 8 characters - Not Satisfied");
	}
}
