public class PercentageOffPromotion extends AbstractPromotion{
    private final double percent;
    private final Money maxDiscount;

    public PercentageOffPromotion(String code, double percent, Money maxDiscount, PromotionConditions conditions) {
        super(code, conditions);
        this.percent = percent;
        this.maxDiscount = maxDiscount;
    }

    @Override
    public Money discountOnSubtotal(PromotionContext context) {
        Money uncapped = context.getSubtotal().percentOf(percent);
        return uncapped.min(maxDiscount);
    }
}
