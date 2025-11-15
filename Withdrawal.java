// Withdrawal.java
// Represents a withdrawal ATM transaction

public class Withdrawal extends Transaction {
   private int amount; // amount to withdraw
   private Keypad keypad; // reference to keypad
   private CashDispenser cashDispenser; // reference to cash dispenser

   // constants for menu options
   private final static int CANCELED = -1;
   private final static int CUSTOM_AMOUNT = 6;

   // Withdrawal constructor
   public Withdrawal(int userAccountNumber, Screen atmScreen,
         BankDatabase atmBankDatabase, Keypad atmKeypad,
         CashDispenser atmCashDispenser) {
      // initialize superclass variables
      super(userAccountNumber, atmScreen, atmBankDatabase);

      // initialize references to keypad and cash dispenser
      keypad = atmKeypad;
      cashDispenser = atmCashDispenser;
   } // end Withdrawal constructor
   
   // Withdrawal constructor with GUI
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

                  // instruct user to take cash
                  screen.displayMessageLine(
                        "\nPlease take your cash now.");

                  // display number of cash
                  screen.displayMessageLine(
                        "\nNumber of cash: \n" +
                        ((amount / 1000) > 0 ? String.format("HK$1000 bills: %d  "
                              , amount / 1000) : "") +
                        (((amount % 1000) / 500) > 0 ? String.format("HK$500 bills: %d  "
                              , (amount % 1000) / 500) : "") +
                        (((amount % 500) / 100) > 0 ? String.format("HK$100 bills: %d"
                              , (amount % 500) / 100) : "")
                  );
                  
                  // Pause for 2 seconds to show the success message
                  pause(2000);
               } // end if
               else // cash dispenser does not have enough cash
               {
                  screen.displayMessageLine(
                        "\nInsufficient cash available in the ATM." +
                              "\n\nPlease choose a smaller amount.");
                  // Pause for 2 seconds to show the error message
                  pause(2000);
               }
            } // end if
            else // not enough money available in user's account
            {
               screen.displayMessageLine(
                     "\nInsufficient funds in your account." +
                           "\n\nPlease choose a smaller amount.");
               // Pause for 2 seconds to show the error message
               pause(2000);
            } // end else
         } // end if
         else // user chose cancel menu option
         {
            screen.displayMessageLine("\nCanceling transaction...");
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
         // display the menu
         screen.displayMessageLine("\n" + padRight("1. $200", 40) + "  <");
         screen.displayMessageLine(padRight("2. $500", 40) + "  <");
         screen.displayMessageLine(padRight("3. $1,000", 40) + "  <");
         screen.displayMessageLine(padRight("4. $2,000", 40) + "  <");
         screen.displayMessageLine("" + padRight("", 40) + "  >");
         screen.displayMessageLine("" + padRight("", 40) + ">  5. $5,000");
         screen.displayMessageLine("" + padRight("", 40) + ">  6. Other amount");
         screen.displayMessage("\nChoose a withdrawal option (or press CANCEL): ");

         int input = keypad.getInput(); // get user input through keypad

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
            default: // the user did not enter a value from 1-6
               screen.displayMessageLine(
                     "\nInvalid selection. Try again.");
         } // end switch
      } // end while

      return userChoice; // return withdrawal amount or CANCELED
   } // end method displayMenuOfAmounts

   // prompt user to enter a custom withdrawal amount
   private int promptForCustomAmount() {
      Screen screen = getScreen(); // get screen reference
      int amount = 0;

      while (amount == 0) {
         screen.displayMessageLine("\n\n\n\n\n");
         screen.displayMessageLine(centerText("Please enter a withdrawal amount", 72));
         screen.displayMessageLine(centerText("(multiples of 100, 500, or 1000 only)", 72));
         screen.displayMessageLine("\n\n\n\n");
         int input = keypad.getInputRightAlign();

         // check if amount is valid (multiple of 100, 500, or 1000)
         if (input == CANCELED)
            return CANCELED;
         else if (input % 100 == 0 && input > 0) {
            // amount is valid if it's a multiple of any of the allowed values
            amount = input;
         } else {
            screen.displayMessageLine(
                  "\nInvalid amount. Please enter a multiple of 100, 500, or 1000.");
         }
      }

      return amount;
   } // end method displayMenuOfAmounts
   
   // Helper method to pad string to right
   private String padRight(String s, int n) {
      return String.format("%-" + n + "s", s);
   }
   
   // Helper method to center text
   private String centerText(String text, int width) {
      int padding = (width - text.length()) / 2;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < padding; i++) {
         sb.append(" ");
      }
      sb.append(text);
      return sb.toString();
   }
} // end class Withdrawal

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