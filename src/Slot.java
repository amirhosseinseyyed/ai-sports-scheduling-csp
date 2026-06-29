import java.util.Objects;

public class Slot {
    public int day;
    public int hour;
    public String stadium;

    public Slot(int day, int hour, String stadium) {
        this.day = day;
        this.hour = hour;
        this.stadium = stadium;
    }

    // Required for use in Set and for removing from the domain
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return day == slot.day && hour == slot.hour && stadium.equals(slot.stadium);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, hour, stadium);
    }

    @Override
    public String toString() {
        return day + " " + hour + " " + stadium;
    }
}