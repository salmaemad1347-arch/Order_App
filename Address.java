import java.util.Objects;
public class Address {
        private final String district;
    private final String detail;

    public Address(String district, String detail) {
        if (district == null) {
            throw new IllegalArgumentException("district cannot be null");
        }
        if (detail == null) {
            throw new IllegalArgumentException("detail cannot be null");
        }
        this.district = district;
        this.detail = detail;
    }
    public String getDistrict() {
        return district;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public boolean equals(Object o) {
        Address address = (Address) o;
        return district.equalsIgnoreCase(address.district) && detail.equalsIgnoreCase(address.detail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(district.toLowerCase(), detail.toLowerCase());
    }

    @Override
    public String toString() {
        return district + " - " + detail;
    }
}
