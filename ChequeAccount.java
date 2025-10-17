// ChequeAccount.java
// Represents a cheque account with a per-cheque limit

public class ChequeAccount extends Account {
    private double limitPerCheque; // per-cheque limit

    // constructor with default limit
    public ChequeAccount(int theAccountNumber, int thePIN, double theAvailableBalance, double theTotalBalance) {
        super(theAccountNumber, thePIN, theAvailableBalance, theTotalBalance);
        this.limitPerCheque = 50000.0; // default HK$50,000
    }

    // constructor with explicit limit
    public ChequeAccount(int theAccountNumber, int thePIN, double theAvailableBalance, double theTotalBalance, double limitPerCheque) {
        super(theAccountNumber, thePIN, theAvailableBalance, theTotalBalance);
        this.limitPerCheque = limitPerCheque;
    }

    public double getLimitPerCheque() {
        return limitPerCheque;
    }

    public void setLimitPerCheque(double limitPerCheque) {
        this.limitPerCheque = limitPerCheque;
    }
}
