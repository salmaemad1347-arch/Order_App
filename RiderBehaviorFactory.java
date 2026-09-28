public class RiderBehaviorFactory {
    private RiderBehaviorFactory() {}

    public static RiderBehavior forVehicleType(VehicleType type) {
        switch (type) {
            case MOTORCYCLE: return new MotorcycleBehavior();
            case BICYCLE: return new BicycleBehavior();
            case CAR: return new CarBehavior();
            default: throw new IllegalArgumentException("Unknown vehicle type" );
        }
    }
}
