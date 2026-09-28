public class RiderUnavailableException extends RuntimeException{
    public RiderUnavailableException(String riderId) {
        super("Rider" + riderId + "is not available");
    }
}
