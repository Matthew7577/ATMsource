// Transfer.java
// Represents a transfer ATM transaction

public class Transfer extends Transaction {
   private double amount; // amount to transfer
   private Keypad keypad; // reference to keypad
   private int targetAccount; // account to transfer to
   private final static int CANCELED = -1; // constant for cancel option
   private final static int EMPTY = -2; // constant for empty input

   // Transfer constructor
   public Transfer(int userAccountNumber, Screen atmScreen,
         BankDatabase atmBankDatabase, Keypad atmKeypad, ATMGUI gui) {
      // initialize superclass variables
      super(userAccountNumber, atmScreen, atmBankDatabase, gui);

      // initialize references to keypad
      keypad = atmKeypad;
   }

   // perform the transfer transaction
   public void execute() {
      clearScreen(); // clear screen when entering this transaction

      double availableBalance; // amount available for transfer

      // get available balance of account involved
      availableBalance = getBankDatabase().getAvailableBalance(getAccountNumber());

      // get transfer amount from user and check for cancellation
      amount = promptForTransferAmount();

      if (amount == CANCELED) // user chose to cancel
         return; // return to main menu

      // check whether the user has enough money first
      if (amount > availableBalance) {
         clearScreen();
         getGUI().showAlert("Insufficient funds in your account.", "Please choose a smaller amount.", 2.0);
         return; // return to main menu
      }

      // check if this is a cheque account and amount exceeds limit
      if (getBankDatabase().isChequeAccount(getAccountNumber())) {
         double limit = getBankDatabase().getLimitPerCheque(getAccountNumber());
         if (amount > limit) {
            clearScreen();
            getGUI().showAlert("Transfer amount exceeds the limit of HK$" + limit + " for cheque accounts.",
                  "Please choose a smaller amount.", 2.0);
            return; // return to main menu
         }
      }

      // get target account from user
      targetAccount = promptForTargetAccount();

      if (targetAccount == CANCELED) // user chose to cancel
         return; // return to main menu

      // check whether target account exists and is different from source account
      if (!getBankDatabase().accountExists(targetAccount) ||
            targetAccount == getAccountNumber()) {
         clearScreen();
         getGUI().showAlert("Invalid target account", "Transfer canceled", 2.0);
         return; // return to main menu
      }

      // at this point, we have a valid amount, sufficient funds, and valid target
      // account
      // perform the transfer
      getBankDatabase().debit(getAccountNumber(), amount);
      getBankDatabase().credit(targetAccount, amount);

      // display success message
      clearScreen();
      getGUI().showAlert("Transfer successful!",
            "Amount transferred: HK$" + amount + "\nTarget account: " + targetAccount, 2);
   }

   // prompt user to enter a transfer amount
   private double promptForTransferAmount() {
      Screen screen = getScreen(); // get reference to screen

      while (true) {
         // display the prompt centered on screen
         screen.displayMessageLine("\n\n\n\n\n\n");
         screen.displayMessageLine(centerText("Please enter transfer amount", 72));
         screen.displayMessageLine(centerText("(or press CANCEL)", 72));
         screen.displayMessageLine("\n\n\n\n");
         int input = keypad.getInputRightAlign("HK$"); // receive input with right alignment and HK$ prefix

         // check whether the user canceled or entered a valid amount
         if (input == CANCELED)
            return CANCELED;
         else if (input == EMPTY) {
            // Empty input - show alert and retry
            getGUI().showAlert("Invalid Amount", "Please enter a valid amount", 2);
            clearScreen();
         } else if (input > 0) {
            return (double) input; // return dollar amount as double
         } else {
            // Invalid amount (zero or negative)
            getGUI().showAlert("Invalid Amount", "Please enter a positive amount", 2);
            clearScreen();
         }
      }
   }

   // prompt user to enter a target account number
   private int promptForTargetAccount() {
      Screen screen = getScreen(); // get reference to screen

      while (true) {
         // display the prompt centered on screen
         clearScreen();
         screen.displayMessageLine("\n\n\n\n\n\n");
         screen.displayMessageLine(centerText("Please enter target account number", 72));
         screen.displayMessageLine(centerText("(or press CANCEL)", 72));
         screen.displayMessageLine("\n\n\n\n");
         int input = keypad.getInputRightAlign("Account: "); // receive input with right alignment (no prefix)

         if (input == CANCELED)
            return CANCELED;
         else if (input == EMPTY) {
            // Empty input - show alert and retry
            getGUI().showAlert("Invalid Account", "Please enter a valid account number", 2);
         } else if (input > 0) {
            return input; // return account number
         } else {
            // Invalid account number (zero or negative)
            getGUI().showAlert("Invalid Account", "Please enter a positive account number", 2);
         }
      }
   }
}