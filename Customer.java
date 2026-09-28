import java.time.LocalDateTime;
import java.util.*;

public class Customer {

        private final String id;
        private final String name;
        private  String mobileNumber;
        private final Set<Address> addresses = new LinkedHashSet<>();
        private Money walletBalance;
        private int completedOrderCount;
        private LocalDateTime lastOrderAt;
        private final Deque<String> recentSearches = new ArrayDeque<>();

        public Customer(String id, String name, String mobileNumber) throws ValidationException {
                this.id = id;
                this.name = name;
                setMobileNumber(mobileNumber);
                this.walletBalance = Money.ZERO;
                this.completedOrderCount = 0;
        }

        public void setMobileNumber(String mobileNumber) throws ValidationException {
                validateMobile(mobileNumber);
                this.mobileNumber = mobileNumber;
        }

        public String getId() {
                return id;
        }

        public String getName() {
                return name;
        }

        public void validateMobile(String mobileNumber) throws ValidationException{
                if (mobileNumber == null || !mobileNumber.matches("(010|011|012|015)")) {
                        throw new ValidationException(
                                "Mobile number must be 11 digits start with 010, 011, 012, or 015.");
                }
}
        public String getMobileNumber() {
                return mobileNumber;
        }

        public void addAddress(Address address) {
                addresses.add(address);
        }

        public boolean ownsAddress(Address address) {
                return addresses.contains(address);
        }
        public Set<Address> getAddresses() {
                return Collections.unmodifiableSet(addresses);
        }

        public Money getWalletBalance() {
                return walletBalance;
        }

        public void topUp(Money amount) {
                this.walletBalance = this.walletBalance.add(amount);
        }
        public void charge(Money amount) {
                if (walletBalance.isLessThan(amount)) {
                        throw new  InsufficientBalanceException(amount, walletBalance);
                }
                this.walletBalance = this.walletBalance.subtract(amount);
        }
        public void refund(Money amount) {
                this.walletBalance = this.walletBalance.add(amount);
        }

        public LoyaltyTier getLoyaltyTier() {
                return LoyaltyTier.fromCompletedOrders(completedOrderCount);
        }

        public int getCompletedOrderCount() {
                return completedOrderCount;
        }

        public boolean isFirstTimeCustomer() {
                return completedOrderCount == 0;
        }
        public void recordCompletedOrder(LocalDateTime when) {
                this.completedOrderCount++;
                this.lastOrderAt = when;
        }

        public LocalDateTime getLastOrderAt() {
                return lastOrderAt;
        }

        public void recordSearch(String query) {
                if (query == null || query.isBlank()) return;
                recentSearches.addFirst(query);

        }
        public List<String> getRecentSearches() {
                return Collections.unmodifiableList(new ArrayList<>(recentSearches));
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Customer)) return false;
                return id.equals(((Customer) o).id);
        }

        @Override
        public int hashCode() {
                return id.hashCode();
        }

        @Override
        public String toString() {
                return "Customer{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", mobileNumber='" + mobileNumber + '\'' +
                        ", addresses=" + addresses +
                        ", walletBalance=" + walletBalance +
                        ", completedOrderCount=" + completedOrderCount +
                        ", lastOrderAt=" + lastOrderAt +
                        ", recentSearches=" + recentSearches +
                        '}';
        }

}
