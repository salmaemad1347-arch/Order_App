import java.time.LocalDateTime;
import java.util.*;

public class Masrdeliveryplatform {
    private final Map<String, Restaurant> restaurants = new LinkedHashMap<>();
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final Map<String, Rider> riders = new LinkedHashMap<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final Map<String, Promotion> promotions = new LinkedHashMap<>();
    private final ReadyOrderQueue readyOrderQueue = new ReadyOrderQueue();
    private final OrderEventPublisher eventPublisher = new OrderEventPublisher();
    private final StatisticsListener statisticsListener = new StatisticsListener();
    private final AuditLogListener auditLogListener = new AuditLogListener();

    private final PriceCalculator priceCalculator;
    private long nextOrderNumber = 1;
    private LocalDateTime when;

    public Masrdeliveryplatform(DistanceService distanceService, AppConfig config) {
        this.priceCalculator = new PriceCalculator(distanceService, config);
        eventPublisher.subscribe(new CustomerNotificationListener());
        eventPublisher.subscribe(new RiderDashboardListener());
        eventPublisher.subscribe(auditLogListener);
        eventPublisher.subscribe(statisticsListener);
    }

    public void registerRestaurant(Restaurant restaurant) throws DuplicateIdentifierException {
        if (restaurants.containsKey(restaurant.getId())) {
            throw new DuplicateIdentifierException("restaurant", restaurant.getId());
        }
        restaurants.put(restaurant.getId(), restaurant);
    }

    public void registerCustomer(Customer customer) throws DuplicateIdentifierException {
        if (customers.containsKey(customer.getId())) {
            throw new DuplicateIdentifierException("customer", customer.getId());
        }
        customers.put(customer.getId(), customer);
    }

    public void registerRider(Rider rider) throws DuplicateIdentifierException {
        if (riders.containsKey(rider.getId())) {
            throw new DuplicateIdentifierException("rider", rider.getId());
        }
        riders.put(rider.getId(), rider);
    }

    public void registerPromotion(Promotion promotion) {
        promotions.put(promotion.getCode().toUpperCase(), promotion);
    }

    public Optional<Restaurant> findRestaurant(String id) {
        return Optional.ofNullable(restaurants.get(id));
    }

    public Optional<Customer> findCustomer(String id) {
        return Optional.ofNullable(customers.get(id));
    }

    public Optional<Rider> findRider(String id) {
        return Optional.ofNullable(riders.get(id));
    }

    public Optional<Order> findOrder(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public Collection<Restaurant> allRestaurants() {
        return Collections.unmodifiableCollection(restaurants.values());
    }

    public Collection<Customer> allCustomers() {
        return Collections.unmodifiableCollection(customers.values());
    }

    public Collection<Rider> allRiders() {
        return Collections.unmodifiableCollection(riders.values());
    }

    public Collection<Order> allOrders() {
        return Collections.unmodifiableCollection(orders.values());
    }

    public void removeRestaurant(String id) {
        restaurants.remove(id);
    }

    public ReadyOrderQueue readyOrderQueue() {
        return readyOrderQueue;
    }

    public AuditLogListener auditLog() {
        return auditLogListener;
    }

    public StatisticsListener statistics() {
        return statisticsListener;
    }

    public static class ItemRequest {
        public String menuItemId;
        public double quantity;

        public ItemRequest(String menuItemId, double quantity) {
            this.menuItemId = menuItemId;
            this.quantity = quantity;
        }
    }

    public Order placeOrder(String customerId, String restaurantId, Address deliveryAddress,
                            List<MenuItem> items, String promotionCode, String deliveryNotes) throws ValidationException {
        Customer customer = findCustomer(customerId).orElseThrow();
        Restaurant restaurant = findRestaurant(restaurantId).orElseThrow();

        if (!restaurant.isOpen()) {
            throw new RestaurantClosedException(restaurantId);
        }
        if (!customer.ownsAddress(deliveryAddress)) {
            throw new ValidationException("The delivery address must belong to the customer placing the order.");
        }
        if (items == null || items.isEmpty()) {
            throw new ValidationException("An order must contain at least one line item.");
        }List<LineItem> lineItems = new ArrayList<>();
        for (MenuItem req : items) {
            MenuItem item = restaurant.getMenuItem(req.menuItemId);
            if (item == null || !item.isAvailable()) {
                throw new ItemUnavailableException(req.menuItemId);
            }
            if (req.quantity <= 0) {
                throw new ValidationException("Quantity for '" + req.menuItemId + "' must be greater than zero.");
            }
            int available = restaurant.getRemainingStock(req.menuItemId);
            int requested = (int) Math.ceil(req.quantity);
            if (requested > available) {
                throw new StockShortageException(req.menuItemId, requested, available);
            }
            lineItems.add(new LineItem(item, req.quantity));
        }

        Promotion promotion = null;
        if (promotionCode != null && !promotionCode.isBlank()) {
            promotion = promotions.get(promotionCode.toUpperCase());
            if (promotion == null) {
                throw new PromotionException("Promotion code '" + promotionCode + "' does not exist.");
            }}
            OrderBuilder builder = new OrderBuilder()
                    .withId(nextOrderId())
                    .forCustomer(customerId)
                    .fromRestaurant(restaurantId)
                    .deliverTo(deliveryAddress)
                    .withPromotionCode(promotionCode)
                    .withDeliveryNotes(deliveryNotes);
            for (LineItem lineItem : lineItems) {
                builder.addItem(lineItem.getMenuItem(), lineItem.getQuantity());
            }
            Order order = builder.build();
        PriceBreakdown breakdown = priceCalculator.calculate(order, restaurant, customer, promotion);
        order.setPriceBreakdown(breakdown);
        for (MenuItem req : items) {
            restaurant.reserveStock(req.menuItemId, req.quantity);
        }
        orders.put(order.getId(), order);
        eventPublisher.publish(order, null, OrderStatus.PLACED);
        return order;
    }

    private String nextOrderId() {
        return String.valueOf(nextOrderNumber++);
    }

    public void payFromWallet(String orderId) {
        Order order = requireOrder(orderId);
        Customer customer = findCustomer(order.getCustomerId()).orElseThrow(() -> new NotFoundException("customer", order.getCustomerId()));
        customer.charge(order.getPriceBreakdown().getTotal());
    }
    public void transitionOrder(String orderId, OrderStatus target, LocalDateTime when) {
        Order order = requireOrder(orderId);
        OrderStatus previous = order.getStatus();
        order.transitionTo(target, when);
        if (target == OrderStatus.READY) {
            Customer customer = findCustomer(order.getCustomerId()).orElseThrow(() -> new NotFoundException("customer", order.getCustomerId()));
            readyOrderQueue.enqueue(orderId, customer.getLoyaltyTier() == LoyaltyTier.GOLD, when);
        }
        if (target == OrderStatus.DELIVERED) {
            Restaurant restaurant = findRestaurant(order.getRestaurantId()).orElseThrow(() -> new NotFoundException("restaurant", order.getRestaurantId()));
            Customer customer = findCustomer(order.getCustomerId()).orElseThrow(() -> new NotFoundException("customer", order.getCustomerId()));
            restaurant.recordCompletedOrder();
            customer.recordCompletedOrder(when);
            order.getAssignedRiderId().flatMap(this::findRider)
                    .ifPresent(r -> r.completeDelivery(order.deliveryDurationSeconds()));
        }
        if (target == OrderStatus.CANCELLED) {
            readyOrderQueue.removeOrder(orderId);
        }

        eventPublisher.publish(order, previous, target);
    }
    public void cancelOrder(String orderId) {
        Order order = requireOrder(orderId);
        transitionOrder(orderId, OrderStatus.CANCELLED, when);
        Customer customer = findCustomer(order.getCustomerId()).orElseThrow(() -> new NotFoundException("customer", order.getCustomerId()));
        if (order.getPriceBreakdown() != null) {
            customer.refund(order.getPriceBreakdown().getTotal());
        }
    }
    public Order assignNextReadyOrderToRider(String riderId, LocalDateTime when) {
        Rider rider = findRider(riderId).orElseThrow();
        if (!rider.isAvailable()) {
            throw new RiderUnavailableException(riderId);
        }
        ReadyOrderQueue.Entry next = readyOrderQueue.dequeueNext();
        if (next == null) {
            throw new NotFoundException("ready order", "none waiting");
        }
        Order order = requireOrder(next.orderId());
        rider.assign(order.getId());
        order.assignRider(riderId);
        transitionOrder(order.getId(), OrderStatus.ASSIGNED, when);
        return order;
    }

    private Order requireOrder(String orderId) {
        return findOrder(orderId).orElseThrow();
    }
    public Money customerWalletBalance(String customerId) {
        return findCustomer(customerId).orElseThrow().getWalletBalance();
    }
}
