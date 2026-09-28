public enum LoyaltyTier {
    BRONZE(0, 9, 0),
    SILVER(10, 30, 10),
    GOLD(30, 500, 100);

    private final int minOrders;
    private final int maxOrders;
    private final double deliveryFeeDiscountPercent;

    LoyaltyTier(int minOrders, int maxOrders, double deliveryFeeDiscountPercent) {
        this.minOrders = minOrders;
        this.maxOrders = maxOrders;
        this.deliveryFeeDiscountPercent = deliveryFeeDiscountPercent;
    }

    public static LoyaltyTier fromCompletedOrders(int completedOrders) {
        if (completedOrders >= GOLD.minOrders) {
            return GOLD;
        }
        if (completedOrders >= SILVER.minOrders) {
            return SILVER;
        }
        return BRONZE;
    }

    public double deliveryFeeDiscountPercent() {
        return deliveryFeeDiscountPercent;
    }

    public boolean isDeliveryWaived() {
        return this == GOLD;
    }
}
