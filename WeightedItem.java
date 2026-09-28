public abstract class WeightedItem  extends MenuItem {

        private final Money pricePerKg;

        public WeightedItem(String id, String name, String category, Money pricePerKg, int preparationTimeMinutes) throws ValidationException {
            super(id, name, category, preparationTimeMinutes);
            this.pricePerKg = pricePerKg;
        }

        public Money getPricePerKg() {
            return pricePerKg;
        }

        @Override
        public Money priceFor(double kilograms) throws ValidationException {
            if (kilograms <= 0) {
                throw new ValidationException("Weight in kilograms must be greater than zero.");
            }
            return pricePerKg.multiplyByAmount(kilograms);
        }

    @Override
    public String toString() {
        return "WeightedItem{" +
                "pricePerKg=" + pricePerKg +
                '}';
    }
}
