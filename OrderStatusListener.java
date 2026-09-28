public interface OrderStatusListener {
    public  void onStatusChanged(Order order, OrderStatus previousStatus, OrderStatus newStatus);
}
