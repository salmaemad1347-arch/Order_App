import java.time.LocalDate;

public class PromotionContext {
        private final Money subtotal;
        private final String deliveryDistrict;
        private final boolean firstTimeCustomer;
        private final LocalDate orderDate;

        public PromotionContext(Money subtotal, String deliveryDistrict, boolean firstTimeCustomer, LocalDate orderDate) {
            this.subtotal = subtotal;
            this.deliveryDistrict = deliveryDistrict;
            this.firstTimeCustomer = firstTimeCustomer;
            this.orderDate = orderDate;
        }

        public Money getSubtotal() {
            return subtotal;
        }

        public String getDeliveryDistrict() {
            return deliveryDistrict;
        }

        public boolean isFirstTimeCustomer() {
            return firstTimeCustomer;
        }

        public LocalDate getOrderDate() {
            return orderDate;
        }
}
