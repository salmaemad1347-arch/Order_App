public class Rider {
    private String id;
    private String name;
    private  RiderBehavior behavior;
    private String currentDistrict;
    private boolean onDuty;
    private String activeOrderId;
    private int completedDeliveries;
    private long totalDeliverySeconds;
    public Rider(String id, String name, VehicleType vehicleType, String currentDistrict) {
        this.id = id;
        this.name = name;
        this.behavior = RiderBehaviorFactory.forVehicleType(vehicleType);
        this.currentDistrict = currentDistrict;
        this.onDuty = false;
        this.completedDeliveries = 0;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RiderBehavior getBehavior() {
        return behavior;
    }

    public VehicleType getVehicleType() {
        return behavior.vehicleType();
    }

    public String getCurrentDistrict() {
        return currentDistrict;
    }

    public void setCurrentDistrict(String district) {
        this.currentDistrict = district;
    }

    public boolean isOnDuty() {
        return onDuty;
    }

    public void goOnDuty() {
        this.onDuty = true;
    }

    public void goOffDuty() {
        if (isBusy()) {
            throw new RiderUnavailableException(id);
        }
        this.onDuty = false;
    }

    public boolean isBusy() {
        return activeOrderId != null;
    }

    public boolean isAvailable() {
        return onDuty && !isBusy();
    }

    public String getActiveOrderId() {
        return activeOrderId;
    }
    public void assign(String orderId) {
        if (!isAvailable()) {
            throw new RiderUnavailableException(id);
        }
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
        this.activeOrderId = orderId;
    }
    public void completeDelivery(long deliverySeconds) {
        if (activeOrderId == null) {
            throw new RiderUnavailableException(id);
        }
        this.activeOrderId = null;
        this.completedDeliveries++;
        this.totalDeliverySeconds += deliverySeconds;
    }

    public int getCompletedDeliveries() {
        return completedDeliveries;
    }

    public double getAverageDeliveryDurationSeconds() {
        return completedDeliveries == 0 ? 0.0 : (double) totalDeliverySeconds / completedDeliveries;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rider)) return false;
        return id.equals(((Rider) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Rider{" + id + ", " + name + ", " + behavior.vehicleType()
                + ", " + (isBusy() ? "busy" : (onDuty ? "available" : "off-duty")) + "}";
    }
}


