// Keypad.java
// Represents the keypad of the ATM

public class Keypad {
   private ATMGUI gui;

   // Constructor that accepts GUI reference
   public Keypad(ATMGUI gui) {
      this.gui = gui;
   } // end Keypad constructor

   // return an integer value entered by user
   public int getInput() {
      return gui.getNumericInput();
   } // end method getInput

   // return an integer value entered by user with password masking
   public int getInputPassword() {
      return gui.getNumericInput(true);
   } // end method getInputPassword

   // return an integer value entered by user with right alignment
   public int getInputRightAlign(String prefix) {
      return gui.getNumericInput(false, true, prefix);
   } // end method getInputRightAlign

   // return a double value entered by user
   public double getInputDouble() {
      return gui.getDoubleInput();
   } // end method getInputDouble
} // end class Keypad
