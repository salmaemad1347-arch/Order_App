public class Money {
    public static final Money ZERO = new Money(0);

    private final long piastres;
    private Money(long piastres) {
        this.piastres = piastres;
    }
    private static long roundToWholeNumber(double value) {
        return (long) (value + 0.5);
    }
    public static Money fromEgp(double egpAmount) {
        long piastres = roundToWholeNumber(egpAmount * 100.0);
        return new Money(piastres);
    }
    public static Money fromPiastres(long piastres) {
        return new Money(piastres);
    }

    public Money add(Money other) {
        return new Money(this.piastres + other.piastres);
    }

    public Money subtract(Money other) {
        return new Money(this.piastres - other.piastres);
    }
    public Money multiplyByCount(int count) {
        return new Money(this.piastres * count);
    }
    public Money multiplyByAmount(double amount) {
        return new Money(roundToWholeNumber(this.piastres * amount));
    }
    public Money percentOf(double percent) {
        return new Money(roundToWholeNumber(this.piastres * percent / 100.0));
    }

    public Money min(Money other) {
        if (this.piastres <= other.piastres) {
            return this;
        }
        return other;
    }
    public Money max(Money other) {
        if (this.piastres >=other.piastres) {
            return this;
        }
        return other;
    }
    public Money nonNegative() {
        if (piastres < 0) {
            return ZERO;
        }
        return this;
    }

    public boolean isNegative() {
        return piastres < 0;
    }

    public boolean isPositive() {
        return piastres > 0;
    }

    public boolean isZero() {
        return piastres == 0;
    }
    public boolean isGreaterThanOrEqualTo(Money other) {
        return this.piastres >= other.piastres;
    }

    public boolean isLessThan(Money other) {
        return this.piastres < other.piastres;
    }
    public double toEgp() {
        return piastres / 100.0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Money)) {
            return false;
        }
        Money otherMoney = (Money) other;
        return this.piastres == otherMoney.piastres;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(piastres);
    }

    @Override
    public String toString() {
        return "Money{" +
                "piastres=" + piastres +
                '}';
    }
}
