public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Money required, Money available) {
        super("Insufficient wallet balance: required " + required + " but only " + available + " available.");
    }
}
