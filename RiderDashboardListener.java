public class RiderDashboardListener implements OrderStatusListener {
    @Override
    public void onStatusChanged(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        if (newStatus == OrderStatus.READY || newStatus == OrderStatus.ASSIGNED
                || newStatus == OrderStatus.OUT_FOR_DELIVERY || newStatus == OrderStatus.DELIVERED) {
            System.out.println(" Order " + order.getId() + "is" + newStatus);
        }
    }
}
