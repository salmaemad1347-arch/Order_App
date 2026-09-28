public abstract class AbstractPromotion implements Promotion{
    private final String code;
    private final PromotionConditions conditions;

    protected AbstractPromotion(String code, PromotionConditions conditions) {
        if (code == null) {
            throw new IllegalArgumentException("code cannot be null");
        }
        this.code = code.toUpperCase();
        this.conditions = conditions == null ? PromotionConditions.none() : conditions;
    }


    public String getCode() {
        return code;
    }

    @Override
    public final void validate(PromotionContext context) {
        if (conditions.getExpiryDate() != null) {
            if (context.getOrderDate().isAfter(conditions.getExpiryDate())) {
                throw new PromotionException("Promotion '" + code + "' expired on " + conditions.getExpiryDate() + ".");
            }
        }

        if (conditions.getMinSubtotal() != null) {
            if (context.getSubtotal().isLessThan(conditions.getMinSubtotal())) {
                throw new PromotionException("Promotion '" + code + "' requires a minimum subtotal of "
                        + conditions.getMinSubtotal() + ".");
            }
        }
        if (conditions.getRestrictedDistrict() != null) {
            if (!conditions.getRestrictedDistrict().equalsIgnoreCase(context.getDeliveryDistrict())) {
                throw new PromotionException("Promotion '" + code + "' is only valid for orders delivered to "
                        + conditions.getRestrictedDistrict() + ".");
            }
        }
        if (conditions.isFirstTimeCustomersOnly() && !context.isFirstTimeCustomer()) {
            throw new PromotionException("Promotion '" + code + "' is only valid for first-time customers.");
        }
    }
}
