import java.util.ArrayList;
import java.util.List;

public class OrderEventPublisher {
    private final List<OrderStatusListener> listeners = new ArrayList<>();

    public void subscribe(OrderStatusListener listener) {
        listeners.add(listener);
    }

    public void publish(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        for (OrderStatusListener listener : listeners) {
            listener.onStatusChanged(order, previousStatus, newStatus);
        }
    }
}
