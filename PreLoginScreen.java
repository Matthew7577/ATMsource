// PreLoginScreen.java
// Represents the pre-login screen that waits for card insertion

public class PreLoginScreen {
   private ATMGUI gui;

   // Constructor
   public PreLoginScreen(ATMGUI gui) {
      this.gui = gui;
   }

   // Display welcome screen and wait for card insertion
   public void displayWelcomeScreen() {
      gui.clearScreen();
      gui.displayCardInsertionScreen();
   }

   // Wait for card insertion (returns true when card is inserted)
   public boolean waitForCardInsertion() {
      boolean result = gui.waitForCardInsertion();
      gui.clearScreen();
      return result;
   }
}
