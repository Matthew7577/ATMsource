// Screen.java
// Represents the screen of the ATM

public class Screen {
   private ATMGUI gui;

   // Constructor that accepts GUI reference
   public Screen(ATMGUI gui) {
      this.gui = gui;
   }

   // displays a message without a carriage return
   public void displayMessage(String message) {
      gui.displayMessage(message);
   } // end method displayMessage

   // display a message with a carriage return
   public void displayMessageLine(String message) {
      gui.displayMessageLine(message);
   } // end method displayMessageLine

   // display a dollar amount
   public void displayDollarAmount(double amount) {
      String formatted = String.format("HK$%,.2f", amount);
      gui.displayMessage(formatted);
   } // end method displayDollarAmount
} // end class Screen
