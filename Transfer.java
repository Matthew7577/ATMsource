// Transfer.java
// Represents a transfer ATM transaction

public class Transfer extends Transaction {
    private Keypad keypad; // reference to keypad

    private final static int CANCELED = 0;

    public Transfer(int userAccountNumber, Screen atmScreen, BankDatabase atmBankDatabase, Keypad atmKeypad) {
        super(userAccountNumber, atmScreen, atmBankDatabase);
        this.keypad = atmKeypad;
    }

    @Override
    public void execute() {
        Screen screen = getScreen();
        BankDatabase bankDatabase = getBankDatabase();

        screen.displayMessageLine("\nTransfer Funds:");

        screen.displayMessage("Enter destination account number (or 0 to cancel): ");
        int destAccount = keypad.getInput();
        if (destAccount == CANCELED) {
            screen.displayMessageLine("\nCanceling transaction...");
            return;
        }

        // check destination account exists
        if (!bankDatabase.accountExists(destAccount)) {
            screen.displayMessageLine("\nDestination account not found.");
            return;
        }

        screen.displayMessage("Enter amount to transfer in dollars (no decimals): ");
        int inputAmount = keypad.getInput();
        double amount = (double) inputAmount; // treat as whole dollars

        // check balance
        double availableBalance = bankDatabase.getAvailableBalance(getAccountNumber());
        if (amount <= 0) {
            screen.displayMessageLine("\nInvalid amount. Transaction canceled.");
            return;
        }
        if (amount > availableBalance) {
            screen.displayMessageLine("\nInsufficient funds.");
            return;
        }

        // perform transfer
        bankDatabase.debit(getAccountNumber(), amount);
        bankDatabase.credit(destAccount, amount);

        screen.displayMessageLine("\nTransfer completed successfully.");
    }
}
