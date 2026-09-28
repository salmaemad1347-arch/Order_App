import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {
    private String id;
    private String customerId;
    private String restaurantId;
    private Address deliveryAddress;
    private final List<LineItem> lineItems = new ArrayList<>();
    private String promotionCode;
    private String deliveryNotes;
    private LocalDateTime placedAt = LocalDateTime.now();

    public OrderBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public OrderBuilder forCustomer(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderBuilder fromRestaurant(String restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public OrderBuilder deliverTo(Address address) {
        this.deliveryAddress = address;
        return this;
    }

    public OrderBuilder addItem(MenuItem item, double quantity) {
        this.lineItems.add(new LineItem(item, quantity));
        return this;
    }

    public OrderBuilder withPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
        return this;
    }

    public OrderBuilder withDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
        return this;
    }

    public OrderBuilder placedAt(LocalDateTime when) {
        this.placedAt = when;
        return this;
    }

    public Order build() {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("customerId is required");
        }
        if (restaurantId == null) {
            throw new IllegalArgumentException("restaurantId is required");
        }
        if (deliveryAddress == null) {
            throw new IllegalArgumentException("deliveryAddress is required");
        }
        return new Order(id, customerId, restaurantId, deliveryAddress,
                lineItems, promotionCode, deliveryNotes, placedAt);
    }
}
