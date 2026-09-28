public enum OrderStatus {
    PLACED, ACCEPTED, PREPARING, READY, ASSIGNED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED;

    public boolean canTransitionTo(OrderStatus target) {
        switch (this) {
            case PLACED:
                return target == ACCEPTED || target == CANCELLED;
            case ACCEPTED:
                return target == PREPARING || target == CANCELLED;
            case PREPARING:
                return target == READY || target == CANCELLED;
            case READY:
                return target == ASSIGNED || target == CANCELLED;
            case ASSIGNED:
                return target == OUT_FOR_DELIVERY || target == CANCELLED;
            case OUT_FOR_DELIVERY:
                return target == DELIVERED;
            case DELIVERED:
                return false;
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }
}
