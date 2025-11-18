// Keypad.java
// Represents the keypad of the ATM

public class Keypad {
   private ATMGUI gui;

   // Constructor that accepts GUI reference
   public Keypad(ATMGUI gui) {
      this.gui = gui;
   } // end Keypad constructor

   // return an integer value entered by user with alignment and prefix
   public int getInput(boolean rightAlign, String prefix) {
      return gui.getNumericInput(false, rightAlign, prefix);
   } // end method getInput

   // return an integer value entered by user with password masking
   public int getInputPassword(boolean rightAlign) {
      return gui.getNumericInput(true, rightAlign, "");
   } // end method getInputPassword

   // return a double value entered by user with alignment and prefix
   public double getInputDouble(boolean rightAlign, String prefix) {
      return gui.getDoubleInput(rightAlign, prefix);
   } // end method getInputDouble
} // end class Keypad
