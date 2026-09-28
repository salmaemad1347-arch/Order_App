import java.util.*;

public class Restaurant {

    private final String id;
    private String name;
    private String district;
    private final Set<String> cuisines = new LinkedHashSet<>();
    private double rating;
    private boolean open;
    private final Map<String, MenuItem> menu = new LinkedHashMap<>();
    private final Map<String, Integer> dailyStock = new LinkedHashMap<>();
    private int completedOrderCount;
    ////////////////////////////////////
    public Restaurant(String id, String name, String district, Set<String> cuisines, double rating) throws ValidationException {
        this.id =id ;
        this.name = name;
        this.district = district;
        if (cuisines == null || cuisines.isEmpty()) {
            throw new ValidationException("A restaurant must offer at least one cuisine category.");
        }
        this.cuisines.addAll(cuisines);
        setRating(rating);
        this.open = true;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDistrict() {
        return district;
    }

    public Set<String> getCuisines() {
        return cuisines;
    }

    public Map<String, Integer> getDailyStock() {
        return dailyStock;
    }

    //////////////////////////////////////////

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) throws ValidationException {
        if (rating < 0.0 || rating > 5.0) {
            throw new ValidationException("Rating must be between 0.0-5.0 .");
        }
        this.rating = rating;
    }
    public boolean isOpen() {
        return open;
    }

    public void open() {
        this.open = true;
    }

    public void close() {
        this.open = false;
    }
    public void addMenuItem(MenuItem item) throws DuplicateIdentifierException{
        if (menu.containsKey(item.getId())) {
            throw new DuplicateIdentifierException("menu item", item.getId());
        }
        menu.put(item.getId(), item);
    }

    public void removeMenuItem(String itemId) {
        menu.remove(itemId);
        dailyStock.remove(itemId);
    }

    public MenuItem getMenuItem(String itemId) {
        return menu.get(itemId);
    }
    public Map<String, MenuItem> getMenu() {
        return Collections.unmodifiableMap(menu);
    }

    public void setDailyStock(String itemId, int units) throws ValidationException {
        if (units < 0) {
            throw new ValidationException("Stock cannot be negative.");
        }
        dailyStock.put(itemId, units);
    }
    public int getRemainingStock(String itemId) {
        return dailyStock.getOrDefault(itemId,0);
    }
    public void reserveStock(String itemId, double quantity) {
        if (!dailyStock.containsKey(itemId)) return;
        int requested = (int)(quantity);
        int available = dailyStock.get(itemId);
        if (requested > available) {
            throw new StockShortageException(itemId, requested, available);
        }
        dailyStock.put(itemId, available - requested);
    }
    public int getCompletedOrderCount() {
        return completedOrderCount;
    }

    public void recordCompletedOrder() {
        this.completedOrderCount++;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant)) return false;
        return id.equals(((Restaurant) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", district='" + district + '\'' +
                ", cuisines=" + cuisines +
                ", rating=" + rating +
                ", open=" + open +
                ", menu=" + menu +
                ", dailyStock=" + dailyStock +
                ", completedOrderCount=" + completedOrderCount +
                '}';
    }
}
