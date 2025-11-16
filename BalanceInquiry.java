// BalanceInquiry.java
// Represents a balance inquiry ATM transaction

public class BalanceInquiry extends Transaction {
   // BalanceInquiry constructor
   public BalanceInquiry(int userAccountNumber, Screen atmScreen,
         BankDatabase atmBankDatabase) {
      super(userAccountNumber, atmScreen, atmBankDatabase);
   } // end BalanceInquiry constructor
   
   // BalanceInquiry constructor with GUI
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
      
      // Wait for user to press ENTER (getGUI will handle this)
      if (getGUI() != null) {
         getGUI().waitForEnter();
      }
   } // end method execute
} // end class BalanceInquiry

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