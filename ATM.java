// ATM.java
// Represents an automated teller machine

public class ATM {
   private boolean userAuthenticated; // whether user is authenticated
   private int currentAccountNumber; // current user's account number
   private Screen screen; // ATM's screen
   private Keypad keypad; // ATM's keypad
   private CashDispenser cashDispenser; // ATM's cash dispenser
   private BankDatabase bankDatabase; // account information database
   private ATMGUI gui; // ATM's GUI

   // constants corresponding to main menu options
   private static final int BALANCE_INQUIRY = 1;
   private static final int WITHDRAWAL = 2;
   private static final int TRANSFER = 3;
   private static final int EXIT = 4;

   // Constructor that accepts GUI reference
   public ATM(ATMGUI gui) {
      this.gui = gui;
      userAuthenticated = false; // user is not authenticated to start
      currentAccountNumber = 0; // no current account number to start
      screen = new Screen(gui); // create screen with GUI
      keypad = new Keypad(gui); // create keypad with GUI
      cashDispenser = new CashDispenser(); // create cash dispenser
      bankDatabase = new BankDatabase(); // create acct info database
   } // end ATM constructor

   // start ATM
   public void run() {
      // welcome and authenticate user; perform transactions
      while (true) {
         // Show pre-login screen and wait for card insertion
         PreLoginScreen preLoginScreen = new PreLoginScreen(gui);
         preLoginScreen.displayWelcomeScreen();
         preLoginScreen.waitForCardInsertion();
         
         // loop while user is not yet authenticated
         boolean cancelled = false;
         while (!userAuthenticated && !cancelled) {
            screen.displayMessageLine("\nWelcome!");
            cancelled = authenticateUser(); // authenticate user, returns true if cancelled
         } // end while

         if (!cancelled) {
            performTransactions(); // user is now authenticated
         }
         
         userAuthenticated = false; // reset before next ATM session
         currentAccountNumber = 0; // reset before next ATM session
         
         // Reset card state for next session
         gui.resetCardState();
      } // end while
   } // end method run

   // attempts to authenticate user against database
   // returns true if user cancelled, false otherwise
   private boolean authenticateUser() {
      screen.displayMessage("\nPlease enter your account number: ");
      int accountNumber = keypad.getInput(); // input account number
      
      // Check if user canceled
      if (accountNumber == -1) {
         if (gui != null) {
            gui.clearScreen();
            gui.showAlert("Session Cancelled", "Please take your card\nThank you for using our ATM", 0);
            gui.waitForCardRemoval(); // Wait for user to take card
            gui.clearScreen();
         }
         return true; // Return true to indicate cancellation
      }
      
      screen.displayMessage("\nEnter your PIN: "); // prompt for PIN
      int pin = keypad.getInputPassword(); // input PIN with masking
      
      // Check if user canceled
      if (pin == -1) {
         if (gui != null) {
            gui.clearScreen();
            gui.showAlert("Session Cancelled", "Please take your card\nThank you for using our ATM", 0);
            gui.waitForCardRemoval(); // Wait for user to take card
            gui.clearScreen();
         }
         return true; // Return true to indicate cancellation
      }

      // set userAuthenticated to boolean value returned by database
      userAuthenticated = bankDatabase.authenticateUser(accountNumber, pin);

      // check whether authentication succeeded
      if (userAuthenticated) {
         currentAccountNumber = accountNumber; // save user's account #
         if (gui != null) {
            gui.clearScreen(); // clear screen after successful login
         }
      } // end if
      else {
         if (gui != null) {
            gui.showAlert("Login Failed", "Invalid account number or PIN\nPlease try again", 2.0);
            gui.clearScreen(); // clear screen after alert
         } else {
            screen.displayMessageLine(
                  "Invalid account number or PIN. Please try again.");
         }
      }
      return false; // Return false to indicate no cancellation
   } // end method authenticateUser

   // display the main menu and perform transactions
   private void performTransactions() {
      // local variable to store transaction currently being processed
      Transaction currentTransaction = null;

      boolean userExited = false; // user has not chosen to exit

      // loop while user has not chosen option to exit system
      while (!userExited) {
         // show main menu and get user selection
         int mainMenuSelection = displayMainMenu();

         // decide how to proceed based on user's menu selection
         switch (mainMenuSelection) {
            // user chose to perform one of three transaction types
            case BALANCE_INQUIRY:
            case WITHDRAWAL:
            case TRANSFER:

               // initialize as new object of chosen type
               currentTransaction = createTransaction(mainMenuSelection);

               currentTransaction.execute(); // execute transaction
               
               // Clear screen after transaction completes for clean menu display
               if (gui != null) {
                  gui.clearScreen();
               }
               break;
            case EXIT: // user chose to terminate session
               if (gui != null) {
                  gui.clearScreen();
                  gui.showAlert("Session Ended", "Please take your card\nThank you for using our ATM", 0);
                  gui.waitForCardRemoval(); // Wait for user to take card
                  gui.clearScreen();
               } else {
                  screen.displayMessageLine("\nExiting the system...");
               }
               userExited = true; // this ATM session should end
               break;
            default: // user did not enter an integer from 1-4
               if (gui != null) {
                  gui.showAlert("Invalid Selection", "Please select a valid option (1-4).", 2.0);
                  gui.clearScreen();
               } else {
                  screen.displayMessageLine(
                        "\nYou did not enter a valid selection. Try again.");
               }
               break;
         } // end switch
      } // end while
   } // end method performTransactions

   // display the main menu and return an input selection
   private int displayMainMenu() {
      // Create a centered header
      screen.displayMessageLine("\n");
      screen.displayMessageLine(centerText("Please select an operations", 72));
      screen.displayMessageLine("\n");
      
      // Create 2x2 table for menu options
      String topLine = "┌──────────────────────────────────┬──────────────────────────────────┐";
      String midLine = "├──────────────────────────────────┼──────────────────────────────────┤";
      String botLine = "└──────────────────────────────────┴──────────────────────────────────┘";
      screen.displayMessageLine(topLine);
      
      // Row 1: Balance (left) and Withdraw (right)
      screen.displayMessageLine("│" + centerText("", 34) + "│" + centerText("", 34) + "│");
      screen.displayMessageLine("│" + centerText("1. View Balance", 34) + "│" + centerText("2. Withdraw", 34) + "│");
      screen.displayMessageLine("│" + centerText("", 34) + "│" + centerText("", 34) + "│");
      screen.displayMessageLine(midLine);
      
      // Row 2: Transfer (left) and Exit (right)
      screen.displayMessageLine("│" + centerText("", 34) + "│" + centerText("", 34) + "│");
      screen.displayMessageLine("│" + centerText("3. Transfer", 34) + "│" + centerText("4. Exit", 34) + "│");
      screen.displayMessageLine("│" + centerText("", 34) + "│" + centerText("", 34) + "│");
      screen.displayMessageLine(botLine);
      screen.displayMessageLine("");
      
      int choice;
      if (gui != null) {
         choice = gui.getMenuSelection();
      } else {
         choice = keypad.getInput();
      }
      return choice; // return user's selection
   } // end method displayMainMenu
   
   // Helper method to center text in a fixed width
   private String centerText(String text, int width) {
      int padding = (width - text.length()) / 2;
      int paddingRight = width - text.length() - padding;
      return String.format("%" + padding + "s%s%" + paddingRight + "s", "", text, "");
   }

   // return object of specified Transaction subclass
   private Transaction createTransaction(int type) {
      Transaction temp = null; // temporary Transaction variable

      // determine which type of Transaction to create
      switch (type) {
         case BALANCE_INQUIRY: // create new BalanceInquiry transaction
            temp = new BalanceInquiry(
                  currentAccountNumber, screen, bankDatabase, gui);
            break;
         case WITHDRAWAL: // create new Withdrawal transaction
            temp = new Withdrawal(currentAccountNumber, screen,
                  bankDatabase, keypad, cashDispenser, gui);
            break;
         case TRANSFER: // create new Transfer transaction
            temp = new Transfer(currentAccountNumber, screen,
                  bankDatabase, keypad, gui);
            break;
      } // end switch

      return temp; // return the newly created object
   } // end method createTransaction
} // end class ATM

/**************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and *
 * Pearson Education, Inc. All Rights Reserved. *
 * *
 * DISCLAIMER: The authors and publisher of this book have used their *
 * best efforts in preparing the book. These efforts include the *
 * development, research, and testing of the theories and programs *
 * to determine their effectiveness. The authors and publisher make *
 * no warranty of any kind, expressed or implied, with regard to these *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or *
 * consequential damages in connection with, or arising out of, the *
 * furnishing, performance, or use of these programs. *
 *************************************************************************/