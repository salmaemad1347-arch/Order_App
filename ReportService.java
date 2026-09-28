import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportService {
    private final Iterable<Order> orders;
    private final Iterable<Restaurant> restaurants;
    private final Iterable<Customer> customers;
    private final Iterable<Rider> riders;

    public ReportService(Iterable<Order> orders, Iterable<Restaurant> restaurants,
                         Iterable<Customer> customers, Iterable<Rider> riders) {
        this.orders = orders;
        this.restaurants = restaurants;
        this.customers = customers;
        this.riders = riders;
    }
    private List<Order> ordersList() {
        return java.util.stream.StreamSupport.stream(orders.spliterator(), false).collect(Collectors.toList());
    }

    private List<Restaurant> restaurantsList() {
        return java.util.stream.StreamSupport.stream(restaurants.spliterator(), false).collect(Collectors.toList());
    }

    private List<Customer> customersList() {
        return java.util.stream.StreamSupport.stream(customers.spliterator(), false).collect(Collectors.toList());
    }

    private List<Rider> ridersList() {
        return java.util.stream.StreamSupport.stream(riders.spliterator(), false).collect(Collectors.toList());
    }

    private static boolean isDelivered(Order o) {
        return o.getStatus() == OrderStatus.DELIVERED;
    }

    private static Money totalOf(Order o) {
        return o.getPriceBreakdown() == null ? Money.ZERO : o.getPriceBreakdown().getTotal();
    }

    public Money totalRevenue(LocalDate from, LocalDate to) {
        return ordersList().stream()
                .filter(ReportService::isDelivered)
                .filter(o -> withinRange(o.getPlacedAt().toLocalDate(), from, to))
                .map(ReportService::totalOf)
                .reduce(Money.ZERO, Money::add);
    }

    public List<ReportDtos.RestaurantRevenue> topFiveRestaurantsByRevenue(YearMonth month) {
        Map<String, Restaurant> byId = restaurantsList().stream()
                .collect(Collectors.toMap(Restaurant::getId, Function.identity()));

        Map<String, Money> revenueByRestaurant = ordersList().stream()
                .filter(ReportService::isDelivered)
                .filter(o -> YearMonth.from(o.getPlacedAt()).equals(month))
                .collect(Collectors.groupingBy(Order::getRestaurantId,
                        Collectors.reducing(Money.ZERO, ReportService::totalOf, Money::add)));

        return revenueByRestaurant.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue().toEgp(), a.getValue().toEgp())) // highest revenue first
                .limit(5)
                .map(e -> new ReportDtos.RestaurantRevenue(e.getKey(),
                        byId.containsKey(e.getKey()) ? byId.get(e.getKey()).getName() : e.getKey(),
                        e.getValue()))
                .collect(Collectors.toList());
    }

    public List<ReportDtos.DistrictAverage> averageOrderValuePerDistrict() {
        Map<String, List<Order>> byDistrict = ordersList().stream()
                .filter(ReportService::isDelivered)
                .collect(Collectors.groupingBy(o -> o.getDeliveryAddress().getDistrict()));

        return byDistrict.entrySet().stream()
                .map(e -> {
                    Money sum = e.getValue().stream().map(ReportService::totalOf).reduce(Money.ZERO, Money::add);
                    Money avg = Money.fromEgp(sum.toEgp() / e.getValue().size());
                    return new ReportDtos.DistrictAverage(e.getKey(), avg);
                })
                .sorted(Comparator.comparing(ReportDtos.DistrictAverage::district))
                .collect(Collectors.toList());
    }

    public List<Restaurant> topRatedHighVolumeRestaurants() {
        return restaurantsList().stream()
                .filter(r -> r.getRating() > 4.5)
                .filter(r -> r.getCompletedOrderCount() >= 20)
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed()
                        .thenComparing(Restaurant::getName))
                .collect(Collectors.toList());
    }

    public List<ReportDtos.StatusCount> ordersByStatus() {
        Map<OrderStatus, Long> counts = ordersList().stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        return counts.entrySet().stream()
                .map(e -> new ReportDtos.StatusCount(e.getKey().name(), e.getValue()))
                .sorted(Comparator.comparing(ReportDtos.StatusCount::status))
                .collect(Collectors.toList());
    }

    public List<ReportDtos.RiderPerformance> riderPerformance() {
        return ridersList().stream()
                .map(r -> new ReportDtos.RiderPerformance(r.getId(), r.getName(), r.getCompletedDeliveries(),
                        r.getAverageDeliveryDurationSeconds()))
                .sorted(Comparator.comparingInt(ReportDtos.RiderPerformance::completedDeliveries).reversed())
                .collect(Collectors.toList());
    }


    public Optional<ReportDtos.MenuItemFrequency> mostFrequentlyOrderedItem() {
        Map<String, Long> countsByItemId = ordersList().stream()
                .flatMap(o -> o.getLineItems().stream())
                .collect(Collectors.groupingBy(li -> li.getMenuItem().getId(), Collectors.counting()));

        Map<String, String> nameByItemId = ordersList().stream()
                .flatMap(o -> o.getLineItems().stream())
                .map(LineItem::getMenuItem)
                .collect(Collectors.toMap(MenuItem::getId, MenuItem::getName, (a, b) -> a));

        return countsByItemId.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new ReportDtos.MenuItemFrequency(e.getKey(), nameByItemId.getOrDefault(e.getKey(), e.getKey()), e.getValue()));
    }

    public List<Order> customerOrderHistory(String customerId) {
        return ordersList().stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .sorted(Comparator.comparing(Order::getPlacedAt).reversed())
                .collect(Collectors.toList());
    }

    public Money customerLifetimeSpend(String customerId) {
        return ordersList().stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .filter(ReportService::isDelivered)
                .map(ReportService::totalOf)
                .reduce(Money.ZERO, Money::add);
    }

    public Optional<Integer> peakOrderingHour() {
        return ordersList().stream()
                .map(o -> o.getPlacedAt().getHour())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public List<Customer> customersInactiveFor30Days(LocalDateTime now) {
        LocalDateTime cutoff = now.minusDays(30);
        return customersList().stream()
                .filter(c -> c.getLastOrderAt() == null || c.getLastOrderAt().isBefore(cutoff))
                .collect(Collectors.toList());
    }

    private static boolean withinRange(LocalDate date, LocalDate from, LocalDate to) {
        return !date.isBefore(from) && !date.isAfter(to);
    }
}
