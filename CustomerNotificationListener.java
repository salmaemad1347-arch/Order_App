public class CustomerNotificationListener implements OrderStatusListener{
    @Override
    public void onStatusChanged(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        System.out.println("customer " + order.getCustomerId() + "Order " + order.getId()
                + " is " + newStatus );
    }
}
