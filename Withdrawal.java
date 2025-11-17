// Withdrawal.java
// Represents a withdrawal ATM transaction

public class Withdrawal extends Transaction {
   private int amount; // amount to withdraw
   private Keypad keypad; // reference to keypad
   private CashDispenser cashDispenser; // reference to cash dispenser

   // constants for menu options
   private final static int CANCELED = -1;
   private final static int EMPTY = -2;
   private final static int CUSTOM_AMOUNT = 6;

   // Withdrawal constructor
   public Withdrawal(int userAccountNumber, Screen atmScreen,
         BankDatabase atmBankDatabase, Keypad atmKeypad,
         CashDispenser atmCashDispenser, ATMGUI gui) {
      // initialize superclass variables
      super(userAccountNumber, atmScreen, atmBankDatabase, gui);

      // initialize references to keypad and cash dispenser
      keypad = atmKeypad;
      cashDispenser = atmCashDispenser;
   } // end Withdrawal constructor

   // perform transaction
   public void execute() {
      clearScreen(); // clear screen when entering this transaction

      boolean cashDispensed = false; // cash was not dispensed yet
      double availableBalance; // amount available for withdrawal

      // get references to bank database and screen
      BankDatabase bankDatabase = getBankDatabase();
      Screen screen = getScreen();

      // loop until cash is dispensed or the user cancels
      do {
         // obtain a chosen withdrawal amount from the user
         amount = displayMenuOfAmounts();

         // check whether user chose a withdrawal amount or canceled
         if (amount != CANCELED) {
            // Show confirmation screen
            if (!showWithdrawalConfirmation(amount)) {
               // User canceled the confirmation
               clearScreen();
               getGUI().showAlert("Transaction Canceled", "Operation has been canceled.", 2.0);
               return; // return to main menu
            }

            // get available balance of account involved
            availableBalance = bankDatabase.getAvailableBalance(getAccountNumber());

            // check whether the user has enough money in the account
            if (amount <= availableBalance) {
               // check if this is a cheque account and amount exceeds limit
               if (bankDatabase.isChequeAccount(getAccountNumber())) {
                  double limit = bankDatabase.getLimitPerCheque(getAccountNumber());
                  if (amount > limit) {
                     screen.displayMessageLine(
                           String.format("\nWithdrawal amount exceeds the limit of HK$%.2f for cheque accounts." +
                                 "\n\nPlease choose a smaller amount.", limit));
                     continue; // continue the loop to allow another attempt
                  }
               }

               // check whether the cash dispenser has enough money
               if (cashDispenser.isSufficientCashAvailable(amount)) {
                  // update the account involved to reflect withdrawal
                  bankDatabase.debit(getAccountNumber(), amount);

                  cashDispenser.dispenseCash(amount); // dispense cash
                  cashDispensed = true; // cash was dispensed

                  // Build cash breakdown message with line breaks
                  String cashBreakdown = "Please take your cash now";
                  if (amount / 1000 > 0) {
                     cashBreakdown += String.format("\n\nHK$1000 notes: %d", amount / 1000);
                  }
                  if ((amount % 1000) / 500 > 0) {
                     cashBreakdown += String.format("\nHK$500  notes: %d", (amount % 1000) / 500);
                  }
                  if ((amount % 500) / 100 > 0) {
                     cashBreakdown += String.format("\nHK$100  notes: %d", (amount % 500) / 100);
                  }

                  // Show alert with cash dispensed message and wait for cash to be taken
                  getGUI().showAlert("Cash Dispensed", cashBreakdown, 0);
                  getGUI().waitForCashTaken();

                  // Clear screen after cash is taken
                  clearScreen();
               } // end if
               else // cash dispenser does not have enough cash
               {
                  clearScreen();
                  getGUI().showAlert("Insufficient cash available in the ATM", "Please choose a smaller amount.", 2.0);
               }
            } // end if
            else // not enough money available in user's account
            {
               clearScreen();
               getGUI().showAlert("Insufficient funds in your account.", "Please choose a smaller amount.", 2.0);
               clearScreen();
            } // end else
         } // end if
         else // user chose cancel menu option
         {
            clearScreen();
            getGUI().showAlert("Transaction Canceled", "Withdrawal has been canceled.", 2.0);
            return; // return to main menu because user canceled
         } // end else
      } while (!cashDispensed);

   } // end method execute

   // display a menu of withdrawal amounts and the option to cancel;
   // return the chosen amount or CANCELED if the user chooses to cancel
   private int displayMenuOfAmounts() {
      int userChoice = 0; // local variable to store return value

      Screen screen = getScreen(); // get screen reference

      // array of amounts to correspond to menu numbers
      int amounts[] = { 0, 200, 500, 1000, 2000, 5000 };

      // loop while no valid choice has been made
      while (userChoice == 0) {
         // display the menu in table format
         screen.displayMessageLine("\n\n");
         screen.displayMessageLine(centerText("Withdrawal Menu", 72));
         screen.displayMessageLine("\n\n");

         // Create 2x3 table for menu options
         String topLine = "┌──────────────────────────────────┬──────────────────────────────────┐";
         String midLine = "├──────────────────────────────────┼──────────────────────────────────┤";
         String botLine = "└──────────────────────────────────┴──────────────────────────────────┘";
         screen.displayMessageLine(topLine);

         // Row 1: $200 (left) and $500 (right)
         screen.displayMessageLine("│" + centerText("1. $200", 34) + "│" + centerText("2. $500", 34) + "│");
         screen.displayMessageLine(midLine);

         // Row 2: $1,000 (left) and $2,000 (right)
         screen.displayMessageLine("│" + centerText("3. $1,000", 34) + "│" + centerText("4. $2,000", 34) + "│");
         screen.displayMessageLine(midLine);

         // Row 3: $5,000 (left) and Other amount (right)
         screen.displayMessageLine("│" + centerText("5. $5,000", 34) + "│" + centerText("6. Other amount", 34) + "│");
         screen.displayMessageLine(botLine);

         // Get input from GUI with side buttons
         int input = getGUI().getWithdrawalMenuSelection();

         // determine how to proceed based on the input value
         switch (input) {
            case 1: // if the user chose a withdrawal amount
            case 2: // (i.e., chose option 1, 2, 3, 4 or 5), return the
            case 3: // corresponding amount from amounts array
            case 4:
            case 5:
               clearScreen(); // clear screen after selecting an amount
               userChoice = amounts[input]; // save user's choice
               break;
            case CUSTOM_AMOUNT: // user wants to enter a custom amount
               clearScreen(); // clear screen before entering custom amount
               userChoice = promptForCustomAmount();
               break;
            case CANCELED: // the user chose to cancel
               userChoice = CANCELED; // save user's choice
               break;
            case EMPTY: // empty input or invalid input
               getGUI().showAlert("Invalid Selection", "Please select a valid option (1-6)", 2.0);
               clearScreen();
               break;
            default: // the user did not enter a value from 1-6
               getGUI().showAlert("Invalid Selection", "Please select a valid option (1-6)", 2.0);
               clearScreen();
         } // end switch
      } // end while

      return userChoice; // return withdrawal amount or CANCELED
   } // end method displayMenuOfAmounts

   // prompt user to enter a custom withdrawal amount
   private int promptForCustomAmount() {
      Screen screen = getScreen(); // get screen reference
      int amount = 0;

      while (amount == 0) {
         screen.displayMessageLine("\n\n\n\n\n\n");
         screen.displayMessageLine(centerText("Please enter a withdrawal amount", 72));
         screen.displayMessageLine(centerText("(multiples of 100, 500, or 1000 only)", 72));
         screen.displayMessageLine("\n\n\n\n");
         int input = keypad.getInputRightAlign("HK$");

         // check if amount is valid (multiple of 100, 500, or 1000)
         if (input == CANCELED)
            return CANCELED;
         else if (input == EMPTY) {
            // Empty input - show alert
            getGUI().showAlert("Invalid Amount", "Please enter a valid amount", 2);
            clearScreen();
         } else if (input % 100 == 0 && input > 0) {
            // amount is valid if it's a multiple of any of the allowed values
            amount = input;
         } else {
            getGUI().showAlert("Invalid Amount", "Please enter a multiple of 100, 500, or 1000", 2);
            clearScreen();
         }
      }

      return amount;
   } // end method displayMenuOfAmounts

   // show confirmation screen for withdrawal and wait for user confirmation
   // returns true if user confirms, false if user cancels
   private boolean showWithdrawalConfirmation(int amount) {
      Screen screen = getScreen();
      clearScreen();

      // Display withdrawal confirmation centered on screen
      screen.displayMessageLine("\n\n");
      screen.displayMessageLine(centerText("Confirm Withdrawal", 72));
      screen.displayMessageLine("");
      screen.displayMessageLine(centerText(String.format("Amount to withdraw: HK$%.2f", (double)amount), 72));
      screen.displayMessageLine("");
      screen.displayMessageLine("");
      screen.displayMessageLine(centerText("Press ENTER to confirm or CANCEL to abort", 72));

      // Wait for user to press ENTER or CANCEL
      return getGUI().waitForEnterOrCancel();
   } // end method showWithdrawalConfirmation
} // end class Withdrawal
