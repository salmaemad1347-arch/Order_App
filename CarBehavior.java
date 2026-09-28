public class CarBehavior implements RiderBehavior {
    @Override
 public double maxRangeKm() { return 40.0; }
    @Override
 public double averageSpeedKmh() { return 35.0; }
    @Override
 public int maxOrderSize() { return 40; }
    @Override
 public VehicleType vehicleType() { return VehicleType.CAR; }
}
