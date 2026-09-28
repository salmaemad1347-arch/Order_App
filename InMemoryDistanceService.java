import java.util.HashMap;
import java.util.Map;

public class InMemoryDistanceService implements DistanceService{
    private final Map<String, Double> distances = new HashMap<>();
    private final double defaultDistanceKm;

    public InMemoryDistanceService(double defaultDistanceKm) {
        this.defaultDistanceKm = defaultDistanceKm;
        put("Maadi", "Dokki", 3);
        put("Dokki", "Nasr City", 14.0);
        put("Heliopolis", "Nasr City", 6);
        put("Heliopolis", "Maadi", 18.5);
        put("Faisal", "Dokki", 9.0);
        put("Faisal", "Heliopolis", 25.0);
    }

    private void put(String a, String b, double km) {
        distances.put(key(a, b), km);
    }

    private static String key(String a, String b) {
        String lowerA = a.toLowerCase();
        String lowerB = b.toLowerCase();
        return lowerA.compareTo(lowerB) <= 0 ? lowerA + "|" + lowerB : lowerB + "|" + lowerA;
    }

    @Override
    public double distanceKm(String fromDistrict, String toDistrict) {
        if (fromDistrict.equalsIgnoreCase(toDistrict)) {
            return 0.0;
        }
        return distances.getOrDefault(key(fromDistrict, toDistrict), defaultDistanceKm);
    }
}
