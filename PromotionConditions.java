import java.time.LocalDate;

public class PromotionConditions {
    private Money minSubtotal;
    private LocalDate expiryDate;
    private String restrictedDistrict;
    private boolean firstTimeCustomersOnly;

    public static PromotionConditions none() {
        return new PromotionConditions();
    }

    public PromotionConditions minSubtotal(Money minSubtotal) {
        this.minSubtotal = minSubtotal;
        return this;
    }

    public PromotionConditions expiresOn(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public PromotionConditions restrictedToDistrict(String district) {
        this.restrictedDistrict = district;
        return this;
    }

    public PromotionConditions firstTimeCustomersOnly() {
        this.firstTimeCustomersOnly = true;
        return this;
    }

    public Money getMinSubtotal() {
        return minSubtotal;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getRestrictedDistrict() {
        return restrictedDistrict;
    }

    public boolean isFirstTimeCustomersOnly() {
        return firstTimeCustomersOnly;
    }
}
