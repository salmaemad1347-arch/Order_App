

public class Freedeliverypromotion extends AbstractPromotion{
        public Freedeliverypromotion(String code, PromotionConditions conditions) {
        super(code, conditions);
    }

    @Override
    public Money discountOnSubtotal(PromotionContext context) {
        return Money.ZERO;
    }

    @Override
    public boolean waivesDeliveryFee() {
        return true;

}}
