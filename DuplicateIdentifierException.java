public class DuplicateIdentifierException extends Exception{
    public DuplicateIdentifierException(String type, String id) {
        super( type + " with id '" + id + "' already exists.");
    }

}
