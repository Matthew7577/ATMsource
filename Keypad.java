
// Keypad.java
// Represents the keypad of the ATM
import java.util.Scanner; // program uses Scanner to obtain user input

public class Keypad {
   private Scanner input; // reads data from the command line
   private ATMGUI gui;

   // Constructor that accepts GUI reference
   public Keypad(ATMGUI gui) {
      this.gui = gui;
      if (gui == null) {
         input = new Scanner(System.in);
      }
   } // end Keypad constructor

   // return an integer value entered by user
   public int getInput() {
      if (gui != null) {
         return gui.getNumericInput();
      } else {
         return input.nextInt(); // we assume that user enters an integer
      }
   } // end method getInput

   // return an integer value entered by user with password masking
   public int getInputPassword() {
      if (gui != null) {
         return gui.getNumericInput(true);
      } else {
         return input.nextInt(); // we assume that user enters an integer
      }
   } // end method getInputPassword

   // return an integer value entered by user with right alignment
   public int getInputRightAlign(String prefix) {
      if (gui != null) {
         return gui.getNumericInput(false, true, prefix);
      } else {
         return input.nextInt(); // we assume that user enters an integer
      }
   } // end method getInputRightAlign

   // return a double value entered by user
   public double getInputDouble() {
      if (gui != null) {
         return gui.getDoubleInput();
      } else {
         return input.nextDouble(); // get decimal number input
      }
   } // end method getInputDouble
} // end class Keypad
