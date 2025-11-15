// Transaction.java
// Abstract superclass Transaction represents an ATM transaction

public abstract class Transaction {
   private int accountNumber; // indicates account involved
   private Screen screen; // ATM's screen
   private BankDatabase bankDatabase; // account info database
   private ATMGUI gui; // reference to GUI for screen clearing

   // Transaction constructor invoked by subclasses using super()
   public Transaction(int userAccountNumber, Screen atmScreen,
         BankDatabase atmBankDatabase) {
      accountNumber = userAccountNumber;
      screen = atmScreen;
      bankDatabase = atmBankDatabase;
      gui = null;
   } // end Transaction constructor
   
   // Transaction constructor with GUI reference
   public Transaction(int userAccountNumber, Screen atmScreen,
         BankDatabase atmBankDatabase, ATMGUI atmGUI) {
      accountNumber = userAccountNumber;
      screen = atmScreen;
      bankDatabase = atmBankDatabase;
      gui = atmGUI;
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
   
   // clear screen if GUI is available
   protected void clearScreen() {
      if (gui != null) {
         gui.clearScreen();
      }
   } // end method clearScreen
   
   // pause for specified milliseconds to show messages
   protected void pause(int milliseconds) {
      try {
         Thread.sleep(milliseconds);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   } // end method pause

   // perform the transaction (overridden by each subclass)
   abstract public void execute();
} // end class Transaction

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