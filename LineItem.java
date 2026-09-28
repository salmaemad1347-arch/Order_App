public class LineItem {
    private final MenuItem menuItem;
    private final double quantity;
    public LineItem(MenuItem menuItem, double quantity) {
        if (menuItem == null) {
            throw new IllegalArgumentException("menuItem cannot be null");
        }
        this.menuItem = menuItem;
        this.quantity = quantity;
    }
    public MenuItem getMenuItem() {
        return menuItem;
    }
    public double getQuantity() {
        return quantity;
    }
    public Money lineTotal() throws ValidationException {
        return menuItem.priceFor(quantity);
    }

    @Override
    public String toString() {
        return "LineItem{" +
                "menuItem=" + menuItem +
                ", quantity=" + quantity +
                '}';
    }
}
