import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditLogListener implements OrderStatusListener {
    public record AuditEntry(String orderId, OrderStatus from, OrderStatus to, LocalDateTime at) {}

    private final List<AuditEntry> entries = new ArrayList<>();

    @Override
    public void onStatusChanged(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        entries.add(new AuditEntry(order.getId(), previousStatus, newStatus, LocalDateTime.now()));
    }

    public List<AuditEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

}
