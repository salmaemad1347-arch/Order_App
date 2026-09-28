public class ItemUnavailableException extends RuntimeException{
    public ItemUnavailableException(String itemId) {
        super("Menu item '" + itemId + "' is not available.");
    }
}
