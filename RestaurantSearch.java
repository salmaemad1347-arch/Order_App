import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RestaurantSearch {
    public static Predicate<Restaurant> byDistrict(String district) {
        return r -> r.getDistrict().equalsIgnoreCase(district);
    }

    public static Predicate<Restaurant> byCuisine(String cuisine) {
        return r -> r.getCuisines().stream().anyMatch(c -> c.equalsIgnoreCase(cuisine));
    }

    public static Predicate<Restaurant> byMinRating(double minRating) {
        return r -> r.getRating() >= minRating;
    }

    public static Predicate<Restaurant> byPriceCeiling(double ceiling) {
        return r -> r.getMenu().values().stream()
                .filter(MenuItem::isAvailable)
                .anyMatch(item -> {
                    try {
                        return priceOf(item) <= ceiling;
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public static Predicate<Restaurant> isOpen() {
        return Restaurant::isOpen;
    }

    public static Predicate<Restaurant> byNameOrCuisineContains(String text) {
        String needle = text.toLowerCase();
        return r -> r.getName().toLowerCase().contains(needle)
                || r.getCuisines().stream().anyMatch(c -> c.toLowerCase().contains(needle));
    }

    @SafeVarargs
    public static List<Restaurant> execute(Collection<Restaurant> restaurants, Predicate<Restaurant>... criteria) {
        Predicate<Restaurant> combined = r -> true;
        for (Predicate<Restaurant> criterion : criteria) {
            combined = combined.and(criterion);
        }
        return restaurants.stream().filter(combined).collect(Collectors.toCollection(ArrayList::new));
    }

    private static double priceOf(MenuItem item) throws ValidationException {
        return item.priceFor(1).toEgp();
    }

    private RestaurantSearch() {}
}


