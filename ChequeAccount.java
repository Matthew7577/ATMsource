// ChequeAccount.java
// Represents a cheque account with limit per cheque

public class ChequeAccount extends Account {
   private double limitPerCheque; // maximum amount allowed per cheque

   // ChequeAccount constructor
   public ChequeAccount(int accountNumber, int pin,
         double availableBalance, double totalBalance) {
      // initialize superclass variables
      super(accountNumber, pin, availableBalance, totalBalance);

      // initialize limit per cheque with default value of HK$50,000
      limitPerCheque = 50000.0;
   }

   // getter method for limit per cheque
   public double getLimitPerCheque() {
      return limitPerCheque;
   }

   // setter method for limit per cheque
   public void setLimitPerCheque(double newLimit) {
      // validate that limit is positive
      if (newLimit >= 0.0)
         limitPerCheque = newLimit;
   }
}