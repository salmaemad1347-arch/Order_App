import java.util.EnumMap;
import java.util.Map;

public class StatisticsListener implements OrderStatusListener{
    private final Map<OrderStatus, Integer> countsByStatus = new EnumMap<>(OrderStatus.class);

    @Override
    public void onStatusChanged(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        if (previousStatus != null) {
            countsByStatus.merge(previousStatus, -1, Integer::sum);
        }
        countsByStatus.merge(newStatus, 1, Integer::sum);
    }

    public Map<OrderStatus, Integer> getCountsByStatus() {
        return Map.copyOf(countsByStatus);
    }
}
