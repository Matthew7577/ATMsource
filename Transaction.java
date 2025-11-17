// Transaction.java
// Abstract superclass Transaction represents an ATM transaction

public abstract class Transaction {
   private int accountNumber; // indicates account involved
   private Screen screen; // ATM's screen
   private BankDatabase bankDatabase; // account info database
   private ATMGUI gui; // reference to GUI for screen clearing
   private boolean completedSuccessfully; // tracks if transaction completed successfully

   // constants for transaction operations
   protected final static int CANCELED = -1;
   protected final static int EMPTY = -2;

   // Transaction constructor with GUI reference
   public Transaction(int userAccountNumber, Screen atmScreen,
         BankDatabase atmBankDatabase, ATMGUI atmGUI) {
      accountNumber = userAccountNumber;
      screen = atmScreen;
      bankDatabase = atmBankDatabase;
      gui = atmGUI;
      completedSuccessfully = false;
   } // end Transaction constructor

   // return account number
   public int getAccountNumber() {
      return accountNumber;
   } // end method getAccountNumber

   // return reference to screen
   public Screen getScreen() {
      return screen;
   } // end method getScreen

   // return reference to bank database
   public BankDatabase getBankDatabase() {
      return bankDatabase;
   } // end method getBankDatabase

   // return reference to GUI
   public ATMGUI getGUI() {
      return gui;
   } // end method getGUI

   // return whether transaction completed successfully
   public boolean isCompletedSuccessfully() {
      return completedSuccessfully;
   } // end method isCompletedSuccessfully

   // set whether transaction completed successfully
   protected void setCompletedSuccessfully(boolean completed) {
      completedSuccessfully = completed;
   } // end method setCompletedSuccessfully

   // Check cheque account limit and show alert if exceeded
   // Returns true if limit is valid (or not a cheque account), false if exceeded
   protected boolean checkChequeLimit(double amount) {
      if (bankDatabase.isChequeAccount(accountNumber)) {
         double limit = bankDatabase.getLimitPerCheque(accountNumber);
         if (amount > limit) {
            clearScreen();
            gui.showAlert(
               "Cheque Limit Exceeded",
               String.format("Amount exceeds the limit of HK$%.2f for cheque accounts.", limit), 2.0);
            clearScreen();
            return false;
         }
      }
      return true;
   } // end method checkChequeLimit

   // Show insufficient funds alert
   protected void showInsufficientFundsAlert() {
      clearScreen();
      gui.showAlert("Insufficient funds in your account.", "Please choose a smaller amount.", 2.0);
      clearScreen();
   } // end method showInsufficientFundsAlert

   // clear screen if GUI is available
   protected void clearScreen() {
      gui.clearScreen();
   } // end method clearScreen

   // pause for specified milliseconds to show messages
   protected void pause(int milliseconds) {
      try {
         Thread.sleep(milliseconds);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   } // end method pause

   // Helper method to center text within a fixed width
   protected String centerText(String text, int width) {
      int padding = (width - text.length()) / 2;
      int paddingRight = width - text.length() - padding;
      return String.format("%" + padding + "s%s%" + paddingRight + "s", "", text, "");
   } // end method centerText

   // Display post-transaction menu with custom title and return true if user wants to exit
   private boolean showTransactionMenu(String title) {
      clearScreen();
      screen.displayMessageLine("\n\n\n");
      screen.displayMessageLine(centerText(title, 72));
      screen.displayMessageLine(centerText("Would you like to continue or eject your card?", 72));
      screen.displayMessageLine("\n\n\n\n");

      // Create 2x1 table for menu options
      String topLine = "┌──────────────────────────────────┬──────────────────────────────────┐";
      String botLine = "└──────────────────────────────────┴──────────────────────────────────┘";
      screen.displayMessageLine(topLine);

      screen.displayMessageLine("│" + centerText("", 34) + "│" + centerText("", 34) + "│");
      screen.displayMessageLine("│" + centerText("1. Return to Menu", 34) + "│" + centerText("2. Eject Card", 34) + "│");
      screen.displayMessageLine("│" + centerText("", 34) + "│" + centerText("", 34) + "│");
      screen.displayMessageLine(botLine);
      screen.displayMessageLine("");

      int choice = gui.getPostTransactionMenuSelection();
      return choice == 2; // Return true if user chose to eject card
   } // end method showTransactionMenu

   // Display post-transaction menu and return true if user wants to exit
   protected boolean showPostTransactionMenu() {
      return showTransactionMenu("Transaction complete.");
   } // end method showPostTransactionMenu

   // Display cancel menu and return true if user wants to exit
   protected boolean showCancelMenu() {
      return showTransactionMenu("Transaction Canceled.");
   } // end method showCancelMenu

   // Show confirmation screen and wait for user confirmation
   // returns true if user confirms, false if user cancels
   protected boolean showConfirmation(String title, String... details) {
      clearScreen();

      // Display confirmation centered on screen
      screen.displayMessageLine("\n\n");
      screen.displayMessageLine(centerText(title, 72));
      screen.displayMessageLine("");
      
      // Display all detail lines
      for (String detail : details) {
         screen.displayMessageLine(centerText(detail, 72));
      }
      
      screen.displayMessageLine("");
      screen.displayMessageLine("");
      screen.displayMessageLine(centerText("Press ENTER to confirm or CANCEL to abort", 72));

      // Wait for user to press ENTER or CANCEL
      return gui.waitForEnterOrCancel();
   } // end method showConfirmation

   // perform the transaction (overridden by each subclass)
   // returns true if user wants to exit, false to continue
   abstract public boolean execute();
} // end class Transaction
