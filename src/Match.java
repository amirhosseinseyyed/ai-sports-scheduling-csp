import java.util.LinkedHashSet;
import java.util.Set;

public class Match {
    public String team1;
    public String team2;
    public boolean isImportant;
    public Slot assignedSlot = null;
    public Set<Slot> domain = new LinkedHashSet<>();

    public Match(String t1, String t2, boolean isImportant) {
        // مرتب‌سازی الفبایی نام تیم‌ها
        if (t1.compareTo(t2) < 0) {
            this.team1 = t1;
            this.team2 = t2;
        } else {
            this.team1 = t2;
            this.team2 = t1;
        }
        this.isImportant = isImportant;
    }

    public boolean involves(String team) {
        return team1.equals(team) || team2.equals(team);
    }

    @Override
    public String toString() {
        return team1 + " " + team2;
    }
}
