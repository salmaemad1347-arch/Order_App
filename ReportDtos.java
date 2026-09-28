public class ReportDtos {
    private ReportDtos() {}

    public record RestaurantRevenue(String restaurantId, String restaurantName, Money revenue) {}

    public record DistrictAverage(String district, Money averageOrderValue) {}

    public record RiderPerformance(String riderId, String riderName, int completedDeliveries, double averageDurationSeconds) {}

    public record MenuItemFrequency(String itemId, String itemName, long orderCount) {}

    public record StatusCount(String status, long count) {}
}
