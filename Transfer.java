// Transfer.java
// Represents a transfer ATM transaction

public class Transfer extends Transaction {
   private double amount; // amount to transfer
   private Keypad keypad; // reference to keypad
   private int targetAccount; // account to transfer to
   private final static int CANCELED = 0; // constant for cancel option

   // Transfer constructor
   public Transfer(int userAccountNumber, Screen atmScreen,
         BankDatabase atmBankDatabase, Keypad atmKeypad) {
      // initialize superclass variables
      super(userAccountNumber, atmScreen, atmBankDatabase);

      // initialize references to keypad
      keypad = atmKeypad;
   }

   // perform the transfer transaction
   @Override
   public void execute() {
      double availableBalance; // amount available for transfer

      // get available balance of account involved
      availableBalance = getBankDatabase().getAvailableBalance(getAccountNumber());

      // get transfer amount from user and check for cancellation
      amount = promptForTransferAmount();

      if (amount == CANCELED) // user chose to cancel
         return; // return to main menu

      // check whether the user has enough money first
      if (amount > availableBalance) {
         getScreen().displayMessageLine(
               "\nInsufficient funds in your account." +
                     "\n\nPlease choose a smaller amount.");
         return; // return to main menu
      }

      // check if this is a cheque account and amount exceeds limit
      if (getBankDatabase().isChequeAccount(getAccountNumber())) {
         double limit = getBankDatabase().getLimitPerCheque(getAccountNumber());
         if (amount > limit) {
            getScreen().displayMessageLine(
                  String.format("\nTransfer amount exceeds the limit of HK$%.2f for cheque accounts." +
                        "\n\nPlease choose a smaller amount.", limit));
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
         getScreen().displayMessageLine(
               "\nInvalid target account. Transfer canceled.");
         return; // return to main menu
      }

      // at this point, we have a valid amount, sufficient funds, and valid target
      // account
      // perform the transfer
      getBankDatabase().debit(getAccountNumber(), amount);
      getBankDatabase().credit(targetAccount, amount);

      // display success message
      getScreen().displayMessageLine("\nTransfer successful!");
      getScreen().displayMessageLine(
            String.format("Amount transferred: HK$%.2f", amount));
   }

   // prompt user to enter a transfer amount
   private double promptForTransferAmount() {
      Screen screen = getScreen(); // get reference to screen

      // display the prompt
      screen.displayMessage("\nPlease enter transfer amount " +
            "(e.g., 1.50 for HK$1.50, or 0 to cancel): HK$");
      double input = keypad.getInputDouble(); // receive decimal input

      // check whether the user canceled or entered a valid amount
      if (input == CANCELED)
         return CANCELED;
      else
         return input; // return dollar amount directly
   }

   // prompt user to enter a target account number
   private int promptForTargetAccount() {
      Screen screen = getScreen(); // get reference to screen

      // display the prompt
      screen.displayMessage("\nPlease enter target account number " +
            "(or 0 to cancel): ");
      int input = keypad.getInput(); // receive input of account number

      return input; // return account number
   }
}