public class PriceBreakdown {
    private final Money subtotal;
    private final Money deliveryFee;
    private final Money serviceFee;
    private final Money promotionDiscount;
    private final Money total;
    private final String appliedPromotionCode;

    public PriceBreakdown(Money subtotal, Money deliveryFee, Money serviceFee, Money promotionDiscount, Money total, String appliedPromotionCode) {
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.serviceFee = serviceFee;
        this.promotionDiscount = promotionDiscount;
        this.total = total;
        this.appliedPromotionCode = appliedPromotionCode;
    }
    public Money getSubtotal() {
        return subtotal;
    }

    public Money getDeliveryFee() {
        return deliveryFee;
    }

    public Money getServiceFee() {
        return serviceFee;
    }

    public Money getPromotionDiscount() {
        return promotionDiscount;
    }

    public Money getTotal() {
        return total;
    }
    public String getAppliedPromotionCode() {
        return appliedPromotionCode;
    }

    @Override
    public String toString() {
        return "PriceBreakdown{" +
                "subtotal=" + subtotal +
                ", deliveryFee=" + deliveryFee +
                ", serviceFee=" + serviceFee +
                ", promotionDiscount=" + promotionDiscount +
                ", total=" + total +
                ", appliedPromotionCode='" + appliedPromotionCode + '\'' +
                '}';
    }
}
