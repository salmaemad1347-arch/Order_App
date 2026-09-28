public class MotorcycleBehavior implements RiderBehavior{

    @Override
    public double maxRangeKm() { return 25.0; }
    @Override
   public double averageSpeedKmh() { return 40.0; }
    @Override
 public int maxOrderSize() { return 20; }
    @Override
 public VehicleType vehicleType() { return VehicleType.MOTORCYCLE;}
    }

