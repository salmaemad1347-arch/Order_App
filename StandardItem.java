public abstract class StandardItem extends MenuItem {
    private final Money unitPrice;

    public StandardItem(String id, String name, String category, Money unitPrice, int preparationTimeMinutes) throws ValidationException {
        super(id, name, category, preparationTimeMinutes);
        this.unitPrice = unitPrice;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    @Override
    public Money priceFor(double quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero.");
        }  int count = (int) (quantity + 0.5);
        return unitPrice.multiplyByCount(count);
    }

    @Override
    public String toString() {
        return "StandardItem{" +
                "unitPrice=" + unitPrice +
                '}';
    }
}
