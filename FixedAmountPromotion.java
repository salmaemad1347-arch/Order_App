public class FixedAmountPromotion extends AbstractPromotion{
    private final Money amountOff;

    public FixedAmountPromotion(String code, Money amountOff, PromotionConditions conditions) {
        super(code, conditions);
        this.amountOff = amountOff;
    }

    @Override
    public Money discountOnSubtotal(PromotionContext context) {
        return amountOff.min(context.getSubtotal());
    }
}
