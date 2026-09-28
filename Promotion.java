public interface  Promotion {
    void validate(PromotionContext context);

    Money discountOnSubtotal(PromotionContext context);

    default boolean waivesDeliveryFee() {
        return false;
    }
    String getCode();

}
