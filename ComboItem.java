import java.util.ArrayList;
import java.util.List;

public class ComboItem extends MenuItem{
    private final List<MenuItem> components;
    private final Money bundlePrice;
    public ComboItem(String id, String name, String category, List<MenuItem> components,
                     Money bundlePrice, int preparationTimeMinutes) throws ValidationException {
        super(id, name, category, preparationTimeMinutes);
        if (components == null) {
            throw new IllegalArgumentException("components cannot be null");
        }
        if (components.isEmpty()) {
            throw new ValidationException("A combo must bundle at least one item.");
        }
        this.components = new ArrayList<>(components);
        this.bundlePrice = bundlePrice;
    }

    public List<MenuItem> getComponents() {
        return new ArrayList<>(components);
    }

    public Money getBundlePrice() {
        return bundlePrice;
    }

    @Override
    public Money priceFor(double quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero.");
        }

        int count = (int) (quantity + 0.5);
        return bundlePrice.multiplyByCount(count);
    }

    @Override
    public String quantityUnit() {
        return "each";
    }
}
