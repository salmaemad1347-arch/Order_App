public class IllegalOrderTransitionException extends RuntimeException {
    public IllegalOrderTransitionException(OrderStatus from, OrderStatus to) {
        super("Cannot transition order from " + from + " to " + to );
    }
}
