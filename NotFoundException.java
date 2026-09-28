public class NotFoundException extends RuntimeException {
    public NotFoundException(String type, String id) {

        super("No " + type + " found with id '" + id );
    }
}
