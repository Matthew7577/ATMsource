// SavingAccount.java
// Represents a savings account with interest rate

public class SavingAccount extends Account {
    private double interestRate; // annual interest rate (e.g., 0.25% = 0.0025)

    // constructor with default interest rate
    public SavingAccount(int theAccountNumber, int thePIN, double theAvailableBalance, double theTotalBalance) {
        super(theAccountNumber, thePIN, theAvailableBalance, theTotalBalance);
        this.interestRate = 0.0025; // default 0.25% per annum
    }

    // constructor with explicit interest rate
    public SavingAccount(int theAccountNumber, int thePIN, double theAvailableBalance, double theTotalBalance, double interestRate) {
        super(theAccountNumber, thePIN, theAvailableBalance, theTotalBalance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}
