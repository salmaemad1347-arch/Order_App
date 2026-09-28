import java.time.LocalDate;

public class PriceCalculator {
    private final DistanceService distanceService;
    private final AppConfig config;

    public PriceCalculator(DistanceService distanceService, AppConfig config) {
        this.distanceService = distanceService;
        this.config = config;
    }

    public PriceBreakdown calculate(Order order, Restaurant restaurant, Customer customer, Promotion promotion) throws ValidationException {
        Money subtotal = computeSubtotal(order);
        Money deliveryFee = computeDeliveryFee(restaurant, order, customer.getLoyaltyTier(), promotion);
        Money serviceFee = computeServiceFee(subtotal);

        Money promotionDiscount = Money.ZERO;
        String appliedCode = null;
        if (promotion != null) {
            PromotionContext context = new PromotionContext(
                    subtotal, order.getDeliveryAddress().getDistrict(), customer.isFirstTimeCustomer(), LocalDate.now());
            promotion.validate(context);
            promotionDiscount = promotion.discountOnSubtotal(context);
            appliedCode = promotion.getCode();
        }

        Money total = subtotal.add(deliveryFee).add(serviceFee).subtract(promotionDiscount).nonNegative();

        return new PriceBreakdown(subtotal, deliveryFee, serviceFee, promotionDiscount, total, appliedCode);
    }

    private Money computeSubtotal(Order order) throws ValidationException {
        Money subtotal = Money.ZERO;
        for (LineItem lineItem : order.getLineItems()) {
            subtotal = subtotal.add(lineItem.lineTotal());
        }
        return subtotal;
    }

    private Money computeDeliveryFee(Restaurant restaurant, Order order, LoyaltyTier tier, Promotion promotion) {
        if (promotion != null && promotion.waivesDeliveryFee()) {
            return Money.ZERO;
        }
        double distanceKm = distanceService.distanceKm(restaurant.getDistrict(), order.getDeliveryAddress().getDistrict());
        double extraKm = distanceKm - config.getFreeDeliveryRadiusKm();
        if (extraKm < 0) {
            extraKm = 0;
        }
        Money distanceFee = config.getPerKmFeeBeyondRadius().multiplyByAmount(extraKm);
        Money baseFee = config.getBaseDeliveryFee().add(distanceFee);

        double discountPercent = tier.deliveryFeeDiscountPercent();
        Money discount = baseFee.percentOf(discountPercent);
        return baseFee.subtract(discount).nonNegative();
    }

    private Money computeServiceFee(Money subtotal) {
        return subtotal.percentOf(config.getServiceFeePercent());
    }
}
