
    public abstract class MenuItem {
    private final String id;
        public String menuItemId;
        public double quantity;
        private String name;
    private String category;
    private int preparationTimeMinutes;
    private boolean available;

    public MenuItem(String id, String name, String category, int preparationTimeMinutes) throws ValidationException {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.id = id;
        this.name = name;
        this.category = category;
        if (preparationTimeMinutes <= 0) {
            throw new  ValidationException("Preparation time must be greater than zero.");
        }
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.available = true;
    }
    public abstract Money priceFor(double quantity) throws ValidationException;
    public abstract String quantityUnit();

    public static void requirePositive(double value, String quantity) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(quantity + " must be greater than zero.");
        }
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setPreparationTimeMinutes(int preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public int getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
        public boolean isAvailable() {
            return available;
        }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MenuItem)) {
            return false;
        }
        return this.id.equals(((MenuItem) other).id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", preparationTimeMinutes=" + preparationTimeMinutes +
                ", available=" + available +
                '}';
    }
}
