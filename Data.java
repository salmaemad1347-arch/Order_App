    import java.util.Set;
    public class Data {
            private Data() {}

            static void load(Masrdeliveryplatform platform) throws ValidationException, DuplicateIdentifierException {

                Restaurant Burger = new Restaurant("R1", "Burger House", "Maadi", Set.of("Egyptian"), 4.7);
                Burger.addMenuItem(new StandardItem("I1", "Koshary (regular)", "Mains", Money.fromEgp(35.00), 10) {
                    @Override
                    public String quantityUnit() {
                        return "";
                    }
                });
                Burger.addMenuItem(new StandardItem("I2", "Koshary (large)", "Mains", Money.fromEgp(55.00), 10) {
                    @Override
                    public String quantityUnit() {
                        return "";
                    }
                });
                Burger.addMenuItem(new WeightedItem("I3", "Grilled Kofta", "Grills", Money.fromEgp(320.00), 20) {
                    @Override
                    public String quantityUnit() {
                        return "";
                    }
                });
                Burger.setDailyStock("I3", 15);
                platform.registerRestaurant(Burger);

                Restaurant grillHouse = new Restaurant("R2", "Cairo Grill House", "Faisal", Set.of("Egyptian", "Grill"), 4.6);
                grillHouse.addMenuItem(new WeightedItem("I4", "Mixed Grill", "Grills", Money.fromEgp(450.00), 25) {
                    @Override
                    public String quantityUnit() {
                        return "";
                    }
                });
                grillHouse.addMenuItem(new StandardItem("I5", "Rice", "Sides", Money.fromEgp(20.00), 5) {
                    @Override
                    public String quantityUnit() {
                        return "";
                    }
                });
                grillHouse.addMenuItem(new ComboItem("I6", "Grill Combo for Two",
                        "Combos", java.util.List.of(grillHouse.getMenuItem("I5")), Money.fromEgp(470.00), 25));
                grillHouse.setDailyStock("I4", 30);
                platform.registerRestaurant(grillHouse);

                Restaurant pizzaPlace = new Restaurant("R3", "Bella Pizza", "Dokki", Set.of("Italian", "Fast Food"), 4.2);
                pizzaPlace.addMenuItem(new StandardItem("I7", "Margherita Pizza", "Pizza", Money.fromEgp(120.00), 15) {
                    @Override
                    public String quantityUnit() {
                        return "";
                    }
                });
                pizzaPlace.addMenuItem(new StandardItem("I8", "Pepperoni Pizza", "Pizza", Money.fromEgp(140.00), 15) {
                    @Override
                    public String quantityUnit() {
                        return "";
                    }
                });
                pizzaPlace.close();
                platform.registerRestaurant(pizzaPlace);


                Customer sara = new Customer("C1", "Sara Adel", "01012345678");
                sara.addAddress(new Address("Faisal", "12 Nile St, Apt 4"));
                sara.topUp(Money.fromEgp(1000.00));
                platform.registerCustomer(sara);

                Customer omar = new Customer("C2", "Omar Hassan", "01198765432");
                omar.addAddress(new Address("Maadi", "5 Road 9"));
                omar.topUp(Money.fromEgp(500.00));
                platform.registerCustomer(omar);

                Rider mostafa = new Rider("RD1", "Mostafa Kamal", VehicleType.MOTORCYCLE, "Maadi");
                mostafa.goOnDuty();
                platform.registerRider(mostafa);

                Rider hany = new Rider("RD2", "Hany Fathy", VehicleType.BICYCLE, "Dokki");
                hany.goOnDuty();
                platform.registerRider(hany);

                platform.registerPromotion(new PercentageOffPromotion("NILE20", 20.0, Money.fromEgp(50.00),
                        PromotionConditions.none()));
                platform.registerPromotion(new Freedeliverypromotion("FREEDEL", PromotionConditions.none()
                        .minSubtotal(Money.fromEgp(150.00))));
            }
}
