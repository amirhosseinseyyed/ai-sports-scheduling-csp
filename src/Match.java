import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Match {
    public String team1;
    public String team2;
    public boolean isSensitive;
    public Set<Slot> domain;       // Domain of valid values for this match
    public Slot assignedSlot;      // Value assigned to this match during the search

    public Match(String t1, String t2, boolean isSensitive, List<Slot> allPossibleSlots) {
        // Sort team names for consistency (TeamA TeamB is equivalent to TeamB TeamA)
        if (t1.compareTo(t2) > 0) {
            this.team1 = t2;
            this.team2 = t1;
        } else {
            this.team1 = t1;
            this.team2 = t2;
        }
        this.isSensitive = isSensitive;
        // Copy all possible slots into the initial domain of this match
        this.domain = new LinkedHashSet<>(allPossibleSlots);
        this.assignedSlot = null;
    }

    // Check whether this match shares a team with another match
    public boolean sharesTeam(Match other) {
        return this.team1.equals(other.team1) || this.team1.equals(other.team2) ||
                this.team2.equals(other.team1) || this.team2.equals(other.team2);
    }
}