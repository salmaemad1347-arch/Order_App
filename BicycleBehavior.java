public class BicycleBehavior implements RiderBehavior{
    @Override
     public double maxRangeKm() { return 6.0; }
    @Override
    public double averageSpeedKmh() { return 15.0; }
    @Override
 public int maxOrderSize() { return 6; }
    @Override
  public VehicleType vehicleType() { return VehicleType.BICYCLE; }
}




