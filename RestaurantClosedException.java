public class RestaurantClosedException extends RuntimeException{
    public RestaurantClosedException(String restaurantId) {
        super("Restaurant '" + restaurantId + "' is currently closed.");
    }
}
