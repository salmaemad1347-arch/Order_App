import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

private Scanner in = new Scanner(System.in);

public  class Main {
    public   Masrdeliveryplatform platform;
public Rider rider;
public Main(Rider rider) {
    this.rider=rider;
}
    public Main(Masrdeliveryplatform platform) {
        this.platform = platform;
    }

    public  void main(String[] args) throws ValidationException, DuplicateIdentifierException {
        AppConfig config = AppConfig.getInstance();
        Masrdeliveryplatform platform = new Masrdeliveryplatform(new InMemoryDistanceService(10.0), config);
       Data.load(platform);
        Main app = new Main(platform);
        app.run();    }

    public void run() {
        System.out.println("Welcome to Masr Delivery. (Seed data loaded: customers C1/C2, restaurants R1/R2/R3, riders RD1/RD2)");
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readMenuChoice(0, 4);
            switch (choice) {
                case 1 -> customerArea();
                case 2 -> restaurantArea(platform,);
                case 3 -> riderArea(rider, platform);
                case 4 -> adminArea(platform);
                case 0 -> running = false;
            }
        }
        System.out.println("Goodbye.");
    }
    private void printMainMenu() {
        System.out.println();
        System.out.println("============================================");
        System.out.println(" MASR DELIVERY_Main Menu");
        System.out.println("============================================");
        System.out.println(" 1. Customer");
        System.out.println(" 2. Restaurant");
        System.out.println(" 3. Rider");
        System.out.println(" 4. Admin & Reports");
        System.out.println(" 0. Exit");
        System.out.println("============================================");
    }
    private void customerArea() {
        String customerId = readNonBlank("Enter your customer id (e.g. C1): ");
        Optional<Customer> found = platform.findCustomer(customerId);
        if (found.isEmpty()) {
            System.out.println("No customer found with id '" + customerId + "'.");
            return;
        }
        Customer customer = found.get();
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Customer: " + customer.getName() + " (" + customer.getLoyaltyTier() + ") ---");
            System.out.println(" 1. Browse restaurants");
            System.out.println(" 2. Search");
            System.out.println(" 3. View menu");
            System.out.println(" 4. Place order");
            System.out.println(" 5. Pay from wallet");
            System.out.println(" 6. Track order");
            System.out.println(" 7. Cancel order");
            System.out.println(" 8. Order history");
            System.out.println(" 0. Back");
            int choice = readMenuChoice(0, 8);
            try {
                switch (choice) {
                    case 1 -> browseRestaurants();
                    case 2 -> searchRestaurants(customer);
                    case 3 -> viewMenu();
                    case 4 -> placeOrder(customer,new DistanceService,new AppConfig);
                    case 5 -> payFromWallet();
                    case 6 -> trackOrder(orderId);
                    case 7 -> platform.cancelOrder(orderId);
                    case 8 -> orderHistory(customer,platform);
                    case 0 -> back = true;
                }
            } catch (PlatformException e) {
                System.out.println("Could not complete that: " + e.getMessage());
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<Restaurant> allRestaurantsSortedByRating() {
        List<Restaurant> list = new ArrayList<>(platform.allRestaurants());
        list.sort(Comparator.comparingDouble(Restaurant::getRating).reversed().thenComparing(Restaurant::getName));
        return list;
    }
    private void browseRestaurants() {
        System.out.print("Filter by district (blank for none): ");
        String district = in.nextLine().trim();
        System.out.print("Filter by cuisine (blank for none): ");
        String cuisine = in.nextLine().trim();
        System.out.print("Filter by minimum rating (blank for none): ");
        String minRatingStr = in.nextLine().trim();
        List<Predicate<Restaurant>> criteria = new ArrayList<>();
        criteria.add(RestaurantSearch.isOpen());
        if (!district.isBlank()) criteria.add(RestaurantSearch.byDistrict(district));
        if (!cuisine.isBlank()) criteria.add(RestaurantSearch.byCuisine(cuisine));
        if (!minRatingStr.isBlank()) criteria.add(RestaurantSearch.byMinRating(Double.parseDouble(minRatingStr)));
////////////////////////////////////////////////////////////////////////////////////////////////
        @SuppressWarnings("unchecked")
        List<Restaurant> results = RestaurantSearch.execute(platform.allRestaurants(),
                criteria.toArray(new Predicate[0]));
        results.sort(Comparator.comparingDouble(Restaurant::getRating).reversed().thenComparing(Restaurant::getName));
        printRestaurantList(results);
    }
    private void searchRestaurants(Customer customer) {
        String text = readNonBlank("Search (name or cuisine): ");
        customer.recordSearch(text);
        List<Restaurant> results = RestaurantSearch.execute(platform.allRestaurants(),
                RestaurantSearch.byNameOrCuisineContains(text));
        if (results.isEmpty()) {
            System.out.println("Nothing found for " + text );
        } else {
            printRestaurantList(results);
        }
    }

    private void printRestaurantList(List<Restaurant> results) {
        if (results.isEmpty()) {
            System.out.println("No restaurants match.");
            return;
        }
        int i = 1;
        for (Restaurant r : results) {
            System.out.printf(r.getId(), r.getName(), r.getDistrict(), r.getCuisines(), r.getRating());
        }
    }
    private void viewMenu() throws ValidationException {
        Restaurant restaurant = selectRestaurant();
        if (restaurant == null) return;
        System.out.println("Menu for " + restaurant.getName() + ":");
        for (MenuItem item : restaurant.getMenu().values()) {
            String flag = item.isAvailable() ? "" : "  [UNAVAILABLE]";
            System.out.printf(item.getId(), item.getName(), priceLabel(item), item.quantityUnit(), flag);
        }
    }
    private String priceLabel(MenuItem item) throws ValidationException {
        if (item instanceof StandardItem s) return s.getUnitPrice().toString();
        if (item instanceof WeightedItem w) return w.getPricePerKg() + "/kg";
        if (item instanceof ComboItem c) return c.getBundlePrice().toString();
        return item.priceFor(1).toString();
    }

    public Restaurant selectRestaurant() {
        String id = readNonBlank("Restaurant id (e.g. R1): ");
        return platform.findRestaurant(id).orElseGet(() -> {
            System.out.println("No restaurant found with id '" + id + "'.");
            return null;
        });
    }
    public void placeOrder(Customer customer,DistanceService distanceService,AppConfig config) throws ValidationException {
        Restaurant restaurant = selectRestaurant();
        if (restaurant == null) return;

        if (customer.getAddresses().isEmpty()) {
            System.out.println("You have no saved addresses. Please add one first (see admin/profile setup).");
            return;
        }
        List<Address> addresses = new ArrayList<>(customer.getAddresses());
        for (int i = 0; i < addresses.size(); i++) {
            System.out.println((i + 1) + ". " + addresses.get(i));
        }
        int addrChoice = readMenuChoice(1, addresses.size()) - 1;
        Address address = addresses.get(addrChoice);
        List<MenuItem> items = new ArrayList<>();
        boolean addingItems = true;
        while (addingItems) {
            System.out.print("Menu item id to add (blank to finish): ");
            String itemId = in.nextLine().trim();
            if (itemId.isBlank()) {
                addingItems = false;
                continue;
            }
            System.out.print("Quantity (count, or kg for weighted items): ");
            double qty = Double.parseDouble(in.nextLine().trim());
            boolean add = items.add(new Masrdeliveryplatform(distanceService, config));
        }
        if (items.isEmpty()) {
            System.out.println("No items selected; order cancelled.");
            return;
        }
        System.out.print("Promotion code (blank for none): ");
        String promo = in.nextLine().trim();
        System.out.print("Delivery notes (blank for none): ");
        String notes = in.nextLine().trim();

        Order order = platform.placeOrder(customer.getId(), restaurant.getId(), address,
                items, promo.isBlank() ? null : promo, notes.isBlank() ? null : notes);
        System.out.println("Order placed: " + order.getId());
        System.out.println(order.getPriceBreakdown());
    }

    public void payFromWallet() {
        String orderId = readNonBlank("Order reference: ");
        platform.payFromWallet(orderId);
        Order order = platform.findOrder(orderId).orElseThrow();
        Customer customer = platform.findCustomer(order.getCustomerId()).orElseThrow();
        System.out.println("Payment confirmed. New wallet balance: " + customer.getWalletBalance());
    }

    public void trackOrder(String orderId) {
         orderId = readNonBlank("Order reference: ");
        Order order = platform.findOrder(orderId).orElse(null);
        if (order == null) {
            System.out.println("No order found with reference '" + orderId);
            return;
        }
        Duration elapsed = Duration.between(order.getPlacedAt(), LocalDateTime.now());
        System.out.println("Status: " + order.getStatus() + " | Elapsed since placement: "
                + elapsed.toHours() + "h " + (elapsed.toMinutes() % 60) + "m");
    }
    String orderId = readNonBlank("Order reference: ");
        platform.cancelOrder(orderId);
        System.out.println("Order " + orderId + " cancelled and refunded to wallet (if it had been paid).");
}

private void orderHistory(Customer customer,Masrdeliveryplatform p) {
    ReportService reportService = buildReportService(p);
    List<Order> history = reportService.customerOrderHistory(customer.getId());
    if (history.isEmpty()) {
        System.out.println("No orders yet.");
        return;
    }
    for (Order o : history) {
        System.out.println("  " + o.getId() + " | " + o.getPlacedAt() + " | " + o.getStatus()
                + " | " + (o.getPriceBreakdown() != null ? o.getPriceBreakdown().getTotal() : "n/a"));
    }
    System.out.println("Lifetime spend (delivered orders): " + reportService.customerLifetimeSpend(customer.getId()));
}


private void restaurantArea(Masrdeliveryplatform platform,Restaurant restaurant) {
     restaurant = selectRestaurant();
    if (restaurant == null) return;
    boolean back = false;
    while (!back) {
        System.out.println();
        System.out.println("--- Restaurant: " + restaurant.getName() + " ---");
        System.out.println(" 1. Accept a pending (PLACED) order");
        System.out.println(" 2. Mark an order PREPARING");
        System.out.println(" 3. Mark an order READY");
        System.out.println(" 4. Toggle item availability");
        System.out.println(" 5. Add a menu item");
        System.out.println(" 6. Remove a menu item");
        System.out.println(" 7. Adjust daily stock");
        System.out.println(" 8. View today's orders and revenue");
        System.out.println(" 0. Back");
        int choice = readMenuChoice(0, 8);
        try {
            switch (choice) {
                case 1 -> transition(readNonBlank("Order reference: "), OrderStatus.ACCEPTED,platform);
                case 2 -> transition(readNonBlank("Order reference: "), OrderStatus.PREPARING,platform);
                case 3 -> transition(readNonBlank("Order reference: "), OrderStatus.READY, platform);
                case 4 -> toggleAvailability(restaurant);
                case 5 -> addMenuItem(restaurant);
                case 6 -> {
                    String id = readNonBlank("Item id to remove: ");
                    restaurant.removeMenuItem(id);
                    System.out.println("Removed (if it existed).");
                }
                case 7 -> adjustStock(restaurant);
                case 8 -> todaysOrdersAndRevenue(restaurant,platform);
                case 0 -> back = true;
            }
        } catch (PlatformException e) {
            System.out.println("Could not complete that: " + e.getMessage());
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        } catch (DuplicateIdentifierException e) {
            throw new RuntimeException(e);
        }
    }
}

private void transition(String orderId, OrderStatus target,Masrdeliveryplatform platform) {
    platform.transitionOrder(orderId, target, LocalDateTime.now());
    System.out.println("Order " + orderId + " -> " + target);
}

private void toggleAvailability(Restaurant restaurant) {
    String id = readNonBlank("Item id: ");
    MenuItem item = restaurant.getMenuItem(id);
    if (item == null) {
        System.out.println("No such item.");
        return;
    }
    item.setAvailable(!item.isAvailable());
    System.out.println(item.getName() + " is now " + (item.isAvailable() ? "available" : "unavailable") + ".");
}

private void addMenuItem(Restaurant restaurant) throws ValidationException, DuplicateIdentifierException {
    String type = readNonBlank("Type (STANDARD / COMBO / WEIGHTED): ");
    String id = readNonBlank("New item id (unique within this restaurant): ");
    String name = readNonBlank("Name: ");
    String category = readNonBlank("Category: ");
    int prepTime = Integer.parseInt(readNonBlank("Preparation time (minutes): "));
    Money price = Money.fromEgp(Double.parseDouble(readNonBlank("Price (unit price / bundle price / price per kg): ")));
    List<String> componentIds = List.of();
    if (type.equalsIgnoreCase("COMBO")) {
        String raw = readNonBlank("Component item ids, comma separated: ");
        componentIds = Arrays.stream(raw.split(",")).map(String::trim).toList();
    }
    MenuItemFactory.Spec spec = new MenuItemFactory.Spec(type, id, name, category, prepTime, price, componentIds);
    MenuItem item = MenuItemFactory.create(spec, restaurant.getMenu());
    restaurant.addMenuItem(item);
    System.out.println("Added " + item.getName() + ".");
}

private void adjustStock(Restaurant restaurant) throws ValidationException {
    String id = readNonBlank("Item id: ");
    int units = Integer.parseInt(readNonBlank("New stock level: "));
    restaurant.setDailyStock(id, units);
    System.out.println("Stock for " + id + " set to " + units + ".");
}

private void todaysOrdersAndRevenue(Restaurant restaurant,Masrdeliveryplatform platform) {
    LocalDate today = LocalDate.now();
    List<Order> todaysOrders = platform.allOrders().stream()
            .filter(o -> o.getRestaurantId().equals(restaurant.getId()))
            .filter(o -> o.getPlacedAt().toLocalDate().equals(today))
            .toList();
    Money revenue = todaysOrders.stream()
            .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
            .map(o -> o.getPriceBreakdown().getTotal())
            .reduce(Money.ZERO, Money::add);
    System.out.println("Today's orders: " + todaysOrders.size() + " | Revenue (delivered): " + revenue);
    for (Order o : todaysOrders) {
        System.out.println("  " + o.getId() + " | " + o.getStatus());
    }
}



private void riderArea(Rider rider,Masrdeliveryplatform platform) {
    String riderId = readNonBlank("Enter your rider id (e.g. RD1): ");
    rider = platform.findRider(riderId).orElse(null);
    if (rider == null) {
        System.out.println("No rider found with id '" + riderId + "'.");
        return;
    }
    boolean back = false;
    while (!back) {
        System.out.println();
        System.out.println("--- Rider: " + rider.getName() + " (" + rider.getVehicleType() + ") ---");
        System.out.println(" 1. Go on duty");
        System.out.println(" 2. Go off duty");
        System.out.println(" 3. View currently assigned order");
        System.out.println(" 4. Mark assigned order picked up (out for delivery)");
        System.out.println(" 5. Mark assigned order delivered");
        System.out.println(" 6. View personal delivery statistics");
        System.out.println(" 0. Back");
        int choice = readMenuChoice(0, 6);
        try {
            switch (choice) {
                case 1 -> { rider.goOnDuty(); System.out.println("You are now on duty."); }
                case 2 -> { rider.goOffDuty(); System.out.println("You are now off duty."); }
                case 3 -> viewAssignedOrder(rider,platform);
                case 4 -> {
                    if (rider.getActiveOrderId() == null) { System.out.println("No active order."); break; }
                    transition(rider.getActiveOrderId(), OrderStatus.OUT_FOR_DELIVERY,platform);
                }
                case 5 -> {
                    if (rider.getActiveOrderId() == null) { System.out.println("No active order."); break; }
                    transition(rider.getActiveOrderId(), OrderStatus.DELIVERED,platform);
                }
                case 6 -> System.out.println("Completed deliveries: " + rider.getCompletedDeliveries()
                        + " | Average duration: " + String.format("%.0f", rider.getAverageDeliveryDurationSeconds()) + "s");
                case 0 -> back = true;
            }
        } catch (PlatformException e) {
            System.out.println("Could not complete that: " + e.getMessage());
        }
    }
}

private void viewAssignedOrder(Rider rider,Masrdeliveryplatform platform) {
    if (rider.getActiveOrderId() == null) {
        System.out.println("No order currently assigned.");
        return;
    }
    Order order = platform.findOrder(rider.getActiveOrderId()).orElse(null);
    System.out.println(order);
}




private void adminArea(Masrdeliveryplatform platform) {
    boolean back = false;
    while (!back) {
        System.out.println();
        System.out.println("--- Admin & Reports ---");
        System.out.println(" 1. Add a restaurant");
        System.out.println(" 2. Remove a restaurant");
        System.out.println(" 3. Create a promotion");
        System.out.println(" 4. Assign next ready order to a rider");
        System.out.println(" 5. Run a report");
        System.out.println(" 6. Platform-wide statistics");
        System.out.println(" 0. Back");
        int choice = readMenuChoice(0, 6);
        try {
            switch (choice) {
                case 1 -> addRestaurant( platform);
                case 2 -> { platform.removeRestaurant(readNonBlank("Restaurant id: ")); System.out.println("Removed (if it existed)."); }
                case 3 -> System.out.println("Use the seeded promotions (NILE20, FREEDEL), or extend SeedData/Admin API for custom ones.");
                case 4 -> {
                    String riderId = readNonBlank("Rider id: ");
                    Order o = platform.assignNextReadyOrderToRider(riderId, LocalDateTime.now());
                    System.out.println("Assigned order " + o.getId() + " to rider " + riderId + ".");
                }
                case 5 -> runReport(platform);
                case 6 -> platformStats( platform);
                case 0 -> back = true;
            }
        } catch (PlatformException e) {
            System.out.println("Could not complete that: " + e.getMessage());
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        } catch (DuplicateIdentifierException e) {
            throw new RuntimeException(e);
        }
    }
}

private void addRestaurant(Masrdeliveryplatform platform) throws ValidationException, DuplicateIdentifierException {
    String id = readNonBlank("Restaurant id: ");
    String name = readNonBlank("Name: ");
    String district = readNonBlank("District: ");
    String cuisinesRaw = readNonBlank("Cuisines, comma separated: ");
    double rating = Double.parseDouble(readNonBlank("Initial rating (0.0-5.0): "));
    Set<String> cuisines = new LinkedHashSet<>(Arrays.stream(cuisinesRaw.split(",")).map(String::trim).toList());
    platform.registerRestaurant(new Restaurant(id, name, district, cuisines, rating));
    System.out.println("Restaurant added.");
}

private ReportService buildReportService(Masrdeliveryplatform platform) {
    return new ReportService(
            platform.allOrders(),
            platform.allRestaurants(),
            platform.allCustomers(),
            platform.allRiders());
}

private void runReport(Masrdeliveryplatform platform) {
    System.out.println("Reports:");
    System.out.println(" 1. Total revenue for a date range");
    System.out.println(" 2. Top five restaurants by revenue for a month");
    System.out.println(" 3. Average order value per district");
    System.out.println(" 4. Restaurants with rating > 4.5 and >= 20 completed orders");
    System.out.println(" 5. Order counts by status");
    System.out.println(" 6. Rider performance");
    System.out.println(" 7. Most frequently ordered item");
    System.out.println(" 8. Peak ordering hour");
    System.out.println(" 9. Customers inactive for 30+ days");
    int choice = readMenuChoice(1, 9);
    ReportService rs = buildReportService( platform);
    switch (choice) {
        case 1 -> {
            LocalDate from = LocalDate.parse(readNonBlank("From (YYYY-MM-DD): "));
            LocalDate to = LocalDate.parse(readNonBlank("To (YYYY-MM-DD): "));
            System.out.println("Total revenue: " + rs.totalRevenue(from, to));
        }
        case 2 -> {
            YearMonth month = YearMonth.parse(readNonBlank("Month (YYYY-MM): "));
            for (ReportDtos.RestaurantRevenue rr : rs.topFiveRestaurantsByRevenue(month)) {
                System.out.println("  " + rr.restaurantName() + ": " + rr.revenue());
            }
        }
        case 3 -> {
            for (ReportDtos.DistrictAverage da : rs.averageOrderValuePerDistrict()) {
                System.out.println("  " + da.district() + ": " + da.averageOrderValue());
            }
        }
        case 4 -> rs.topRatedHighVolumeRestaurants().forEach(r -> System.out.println("  " + r));
        case 5 -> rs.ordersByStatus().forEach(sc -> System.out.println("  " + sc.status() + ": " + sc.count()));
        case 6 -> rs.riderPerformance().forEach(rp -> System.out.println("  " + rp.riderName() + ": "
                + rp.completedDeliveries() + " deliveries, avg " + String.format("%.0f", rp.averageDurationSeconds()) + "s"));
        case 7 -> System.out.println(rs.mostFrequentlyOrderedItem()
                .map(f -> f.itemName() + " (" + f.orderCount() + " orders)")
                .orElse("No orders have been placed yet."));
        case 8 -> System.out.println(rs.peakOrderingHour()
                .map(h -> h + ":00")
                .orElse("No orders have been placed yet."));
        case 9 -> rs.customersInactiveFor30Days(LocalDateTime.now()).forEach(c -> System.out.println("  " + c));
    }
}

private void platformStats(Masrdeliveryplatform platform) {
    System.out.println("Restaurants: " + platform.allRestaurants().size());
    System.out.println("Customers: " + platform.allCustomers().size());
    System.out.println("Riders: " + platform.allRiders().size());
    System.out.println("Orders: " + platform.allOrders().size());
    System.out.println("Distinct cuisines offered: " + distinctCuisines(platform));
}

private Set<String> distinctCuisines(Masrdeliveryplatform platform) {
    Set<String> set = new LinkedHashSet<>();
    platform.allRestaurants().forEach(r -> set.addAll(r.getCuisines()));
    return set;
}



private int readMenuChoice(int min, int max) {
    while (true) {
        System.out.print("Choose an option: ");
        String line = in.nextLine().trim();
        try {
            int value = Integer.parseInt(line);
            if (value >= min && value <= max) {
                return value;
            }
        } catch (NumberFormatException ignored) {
        }
        System.out.println("Please enter a number between " + min + " and " + max + ".");
    }
}

private String readNonBlank(String prompt) {
    while (true) {
        System.out.print(prompt);
        String line = in.nextLine().trim();
        if (!line.isBlank()) return line;
        System.out.println("This field cannot be blank.");
    }
}
}

