import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ReadyOrderQueue {
    public record Entry(String orderId, boolean goldCustomer, LocalDateTime readyAt) {}

    private static final Comparator<Entry> DISPATCH_ORDER =
     Comparator.comparing(Entry::goldCustomer).reversed().thenComparing(Entry::readyAt);

    private final PriorityQueue<Entry> queue = new PriorityQueue<>(DISPATCH_ORDER);

    public void enqueue(String orderId, boolean goldCustomer, LocalDateTime readyAt) {
        queue.add(new Entry(orderId, goldCustomer, readyAt));
    }
    public Entry dequeueNext() {
        return queue.poll();
    }

    public Entry peekNext() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public void removeOrder(String orderId) {
        queue.removeIf(e -> e.orderId().equals(orderId));
    }
}
