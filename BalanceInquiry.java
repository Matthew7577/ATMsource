// BalanceInquiry.java
// Represents a balance inquiry ATM transaction

public class BalanceInquiry extends Transaction {
   // BalanceInquiry constructor
   public BalanceInquiry(int userAccountNumber, Screen atmScreen,
         BankDatabase atmBankDatabase, ATMGUI gui) {
      super(userAccountNumber, atmScreen, atmBankDatabase, gui);
   } // end BalanceInquiry constructor

   // performs the transaction
   public void execute() {
      clearScreen(); // clear screen when entering this transaction

      // get references to bank database and screen
      BankDatabase bankDatabase = getBankDatabase();
      Screen screen = getScreen();

      // get the available balance for the account involved
      double availableBalance = bankDatabase.getAvailableBalance(getAccountNumber());

      // get the total balance for the account involved
      double totalBalance = bankDatabase.getTotalBalance(getAccountNumber());

      // get the account type
      String accountType = bankDatabase.getAccountTypeName(getAccountNumber());

      // display the balance information on the screen centered
      screen.displayMessageLine("\n\n");
      screen.displayMessageLine(centerText("Balance Information", 72));
      screen.displayMessageLine("");
      screen.displayMessageLine(centerText("Account Number: " + getAccountNumber(), 72));
      screen.displayMessageLine(centerText("Account Type: " + accountType, 72));
      screen.displayMessageLine("");
      screen.displayMessageLine(centerText(String.format("Available balance: HK$%.2f", availableBalance), 72));
      screen.displayMessageLine(centerText(String.format("Total balance: HK$%.2f", totalBalance), 72));
      screen.displayMessageLine("");
      screen.displayMessageLine("");

      // Prompt user to press ENTER to continue
      screen.displayMessageLine(centerText("Press ENTER to return to main menu...", 72));

      // Wait for user to press ENTER
      getGUI().waitForEnter();
   } // end method execute
} // end class BalanceInquiry
