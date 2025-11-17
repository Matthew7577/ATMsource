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
      if (gui != null) {
         gui.displayMessage(message);
      } else {
         System.out.print(message);
      }
   } // end method displayMessage

   // display a message with a carriage return
   public void displayMessageLine(String message) {
      if (gui != null) {
         gui.displayMessageLine(message);
      } else {
         System.out.println(message);
      }
   } // end method displayMessageLine

   // display a dollar amount
   public void displayDollarAmount(double amount) {
      String formatted = String.format("HK$%,.2f", amount);
      if (gui != null) {
         gui.displayMessage(formatted);
      } else {
         System.out.print(formatted);
      }
   } // end method displayDollarAmount
} // end class Screen
