import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Order {
    private final String id;
    private final String customerId;
    private final String restaurantId;
    private final Address deliveryAddress;
    private final List<LineItem> lineItems;
    private final String promotionCode;
    private final String deliveryNotes;
    private final LocalDateTime placedAt;
    private OrderStatus status;
    private PriceBreakdown priceBreakdown;
    private String assignedRiderId;
    private LocalDateTime readyAt;
    private LocalDateTime assignedAt;
    private LocalDateTime outForDeliveryAt;
    private LocalDateTime deliveredAt;
    Order(String id, String customerId, String restaurantId, Address deliveryAddress,
      List<LineItem> lineItems, String promotionCode, String deliveryNotes, LocalDateTime placedAt) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        this.lineItems = new ArrayList<>(lineItems); // defensive copy
        this.promotionCode = promotionCode;
        this.deliveryNotes = deliveryNotes;
        this.placedAt = placedAt;
        this.status = OrderStatus.PLACED;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }
    public List<LineItem> getLineItems() {
        return Collections.unmodifiableList(lineItems);
    }

    public Optional<String> getPromotionCode() {
        return Optional.ofNullable(promotionCode);
    }

    public Optional<String> getDeliveryNotes() {
        return Optional.ofNullable(deliveryNotes);
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setPriceBreakdown(PriceBreakdown priceBreakdown) {
        this.priceBreakdown = priceBreakdown;
    }

    public PriceBreakdown getPriceBreakdown() {
        return priceBreakdown;}
    public Optional<String> getAssignedRiderId() {
        return Optional.ofNullable(assignedRiderId);
    }

    public Optional<LocalDateTime> getReadyAt() {
        return Optional.ofNullable(readyAt);
    }

    public Optional<LocalDateTime> getDeliveredAt() {
        return Optional.ofNullable(deliveredAt);
    }

    public Optional<LocalDateTime> getOutForDeliveryAt() {
        return Optional.ofNullable(outForDeliveryAt);
    }
    public void transitionTo(OrderStatus target, LocalDateTime when) {
        if (!status.canTransitionTo(target)) {
            throw new IllegalOrderTransitionException(status, target);
        }
        this.status = target;
        if (target == OrderStatus.READY) {
            this.readyAt = when;
        } else if (target == OrderStatus.ASSIGNED) {
            this.assignedAt = when;
        } else if (target == OrderStatus.OUT_FOR_DELIVERY) {
            this.outForDeliveryAt = when;
        } else if (target == OrderStatus.DELIVERED) {
            this.deliveredAt = when;
        }
    }
    public void assignRider(String riderId) {
        if (riderId == null) {
            throw new IllegalArgumentException("riderId cannot be null");
        }
        this.assignedRiderId = riderId;
    }
    public long deliveryDurationSeconds() {
        if (placedAt == null || deliveredAt == null) {
            return 0;
        }
        return Duration.between(placedAt, deliveredAt).getSeconds();
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Order)) {
            return false;
        }
        return this.id.equals(((Order) other).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", deliveryAddress=" + deliveryAddress +
                ", lineItems=" + lineItems +
                ", promotionCode='" + promotionCode + '\'' +
                ", deliveryNotes='" + deliveryNotes + '\'' +
                ", placedAt=" + placedAt +
                ", status=" + status +
                ", priceBreakdown=" + priceBreakdown +
                ", assignedRiderId='" + assignedRiderId + '\'' +
                ", readyAt=" + readyAt +
                ", assignedAt=" + assignedAt +
                ", outForDeliveryAt=" + outForDeliveryAt +
                ", deliveredAt=" + deliveredAt +
                '}';
    }
}
