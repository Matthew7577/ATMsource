// BankDatabase.java
// Represents the bank account information database 

public class BankDatabase
{
   private Account accounts[]; // array of Accounts
   
   // no-argument BankDatabase constructor initializes accounts
   public BankDatabase()
   {
      accounts = new Account[ 50 ]; // Maximum 50 accounts
      // Create some sample accounts (both saving and cheque accounts)
      accounts[ 0 ] = new SavingAccount( 12345, 54321, 1000.0, 1200.0 );
      accounts[ 1 ] = new ChequeAccount( 98765, 56789, 200.0, 200.0 );
      accounts[ 2 ] = new SavingAccount( 11451, 19198, 19000000000.0, 19000000001.0 );
      accounts[ 3 ] = new ChequeAccount( 77777, 99999, 799.0, 7999.0 );
   } // end no-argument BankDatabase constructor
   
   // determine if the account is a saving account
   public boolean isSavingAccount( int accountNumber )
   {
      Account account = getAccount( accountNumber );
      return account instanceof SavingAccount;
   } // end method isSavingAccount
   
   // determine if the account is a cheque account
   public boolean isChequeAccount( int accountNumber )
   {
      Account account = getAccount( accountNumber );
      return account instanceof ChequeAccount;
   } // end method isChequeAccount
   
   // get interest rate for a saving account
   public double getInterestRate( int accountNumber )
   {
      Account account = getAccount( accountNumber );
      if ( account instanceof SavingAccount )
         return ((SavingAccount) account).getInterestRate();
      return 0.0; // return 0 if not a saving account
   } // end method getInterestRate
   
   // set interest rate for a saving account
   public void setInterestRate( int accountNumber, double newRate )
   {
      Account account = getAccount( accountNumber );
      if ( account instanceof SavingAccount )
         ((SavingAccount) account).setInterestRate( newRate );
   } // end method setInterestRate
   
   // get limit per cheque for a cheque account
   public double getLimitPerCheque( int accountNumber )
   {
      Account account = getAccount( accountNumber );
      if ( account instanceof ChequeAccount )
         return ((ChequeAccount) account).getLimitPerCheque();
      return 0.0; // return 0 if not a cheque account
   } // end method getLimitPerCheque
   
   // set limit per cheque for a cheque account
   public void setLimitPerCheque( int accountNumber, double newLimit )
   {
      Account account = getAccount( accountNumber );
      if ( account instanceof ChequeAccount )
         ((ChequeAccount) account).setLimitPerCheque( newLimit );
   } // end method setLimitPerCheque
   
   // check whether an account with specified account number exists
   public boolean accountExists( int accountNumber )
   {
      // attempt to retrieve the account with the account number
      return getAccount( accountNumber ) != null;
   } // end method accountExists

   // retrieve Account object containing specified account number
   private Account getAccount( int accountNumber )
   {
      // loop through accounts searching for matching account number
      for ( Account currentAccount : accounts )
      {
         // return current account if match found
         if ( currentAccount.getAccountNumber() == accountNumber )
            return currentAccount;
      } // end for

      return null; // if no matching account was found, return null
   } // end method getAccount

   // determine whether user-specified account number and PIN match
   // those of an account in the database
   public boolean authenticateUser( int userAccountNumber, int userPIN )
   {
      // attempt to retrieve the account with the account number
      Account userAccount = getAccount( userAccountNumber );

      // if account exists, return result of Account method validatePIN
      if ( userAccount != null )
         return userAccount.validatePIN( userPIN );
      else
         return false; // account number not found, so return false
   } // end method authenticateUser

   // return available balance of Account with specified account number
   public double getAvailableBalance( int userAccountNumber )
   {
      return getAccount( userAccountNumber ).getAvailableBalance();
   } // end method getAvailableBalance

   // return total balance of Account with specified account number
   public double getTotalBalance( int userAccountNumber )
   {
      return getAccount( userAccountNumber ).getTotalBalance();
   } // end method getTotalBalance

   // credit an amount to Account with specified account number
   public void credit( int userAccountNumber, double amount )
   {
      getAccount( userAccountNumber ).credit( amount );
   } // end method credit

   // debit an amount from of Account with specified account number
   public void debit( int userAccountNumber, double amount )
   {
      getAccount( userAccountNumber ).debit( amount );
   } // end method debit
} // end class BankDatabase



/**************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/