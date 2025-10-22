// SavingAccount.java
// Represents a saving account with interest rate

public class SavingAccount extends Account
{
   private double interestRate; // interest rate per annum

   // SavingAccount constructor
   public SavingAccount(int accountNumber, int pin, 
      double availableBalance, double totalBalance)
   {
      // initialize superclass variables
      super(accountNumber, pin, availableBalance, totalBalance);
      
      // initialize interest rate with default value of 0.25%
      interestRate = 0.0025; // 0.25% as decimal
   } // end SavingAccount constructor
   
   // getter method for interest rate
   public double getInterestRate()
   {
      return interestRate;
   } // end method getInterestRate
   
   // setter method for interest rate
   public void setInterestRate(double newRate)
   {
      // validate that rate is positive
      if (newRate >= 0.0)
         interestRate = newRate;
   } // end method setInterestRate
} // end class SavingAccount