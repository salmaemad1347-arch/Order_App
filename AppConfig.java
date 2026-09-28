import java.time.LocalDate;
public class AppConfig {

        private static AppConfig instance;

    private final Money baseDeliveryFee = Money.fromEgp(15.00);
    private final double freeDeliveryRadiusKm = 3;
    private final Money perKmFeeBeyondRadius = Money.fromEgp(3.00);
    private final double serviceFeePercent = 10;
    private final int threadPoolSize = 4;
    private final LocalDate dataFilePath = LocalDate.now();

    private AppConfig() {
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public Money getBaseDeliveryFee() {
        return baseDeliveryFee;
    }

    public double getFreeDeliveryRadiusKm() {
        return freeDeliveryRadiusKm;
    }

    public Money getPerKmFeeBeyondRadius() {
        return perKmFeeBeyondRadius;
    }

    public double getServiceFeePercent() {
        return serviceFeePercent;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public LocalDate getDataFilePath() {
        return dataFilePath;
    }
}
