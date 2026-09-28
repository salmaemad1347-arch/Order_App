public class StockShortageException extends RuntimeException {
    public StockShortageException(String itemId, int requested, int available) {
        super("item" + itemId + "': requested " + requested + " but only " + available + " available.");
    }

}
