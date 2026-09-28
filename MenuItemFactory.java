import java.util.List;
import java.util.Map;

public class MenuItemFactory {
    private MenuItemFactory() {}
    public static final class Spec {
        public final String type;
        public final String id;
        public final String name;
        public final String category;
        public final int preparationTimeMinutes;
        public final Money price;
        public final List<String> componentItemIds;

        public Spec(String type, String id, String name, String category, int preparationTimeMinutes,
                    Money price, List<String> componentItemIds) {
            if (type == null) {
                throw new IllegalArgumentException("type cannot be null");
            }
            this.type = type.toUpperCase();
            this.id = id;
            this.name = name;
            this.category = category;
            this.preparationTimeMinutes = preparationTimeMinutes;
            this.price = price;
            this.componentItemIds = componentItemIds;
        }
    }

    public static MenuItem create(Spec spec, Map<String, MenuItem> existingMenuItems) throws ValidationException {
        if (spec.type.equals("STANDARD")) {
            return new StandardItem(spec.id, spec.name, spec.category, spec.price, spec.preparationTimeMinutes) {
                @Override
                public String quantityUnit() {
                    return "";
                }
            };
        }
        if (spec.type.equals("WEIGHTED")) {
            return new WeightedItem(spec.id, spec.name, spec.category, spec.price, spec.preparationTimeMinutes) {
                @Override
                public String quantityUnit() {
                    return "";
                }
            };
        }
        if (spec.type.equals("COMBO")) {
            List<MenuItem> components = spec.componentItemIds.stream()
                    .map(componentId -> {
                        MenuItem component = existingMenuItems.get(componentId);
                        if (component == null) {
                            try {
                                throw new ValidationException("Combo references unknown component item id '" + componentId + "'.");
                            } catch (ValidationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return component;
                    })
                    .toList();
            return new ComboItem(spec.id, spec.name, spec.category, components, spec.price, spec.preparationTimeMinutes);
        }
        throw new ValidationException("Unknown menu item type '" + spec.type + "'.");
    }
}
