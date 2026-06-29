import java.util.*;

public class Scheduler {

    private final int D, H, S;
    private final List<String> stadiums;
    private final List<Match> matches;
    private final List<Slot> allSlots = new ArrayList<>();

    private int backtracks = 0;

    public Scheduler(int D, int H, int S, List<String> stadiums, List<Match> matches) {
        this.D = D;
        this.H = H;
        this.S = S;
        this.stadiums = stadiums;
        this.matches = matches;
    }

    public int getBacktracks() {
        return backtracks;
    }

    /**
     * Sets up the initial domains and runs the backtracking search.
     * Returns true if a full assignment was found, false otherwise.
     */
    public boolean solve() {
        initializeDomains();
        return backtrack(0);
    }

    private void initializeDomains() {
        for (int d = 1; d <= D; d++) {
            for (int h = 1; h <= H; h++) {
                for (String std : stadiums) {
                    allSlots.add(new Slot(d, h, std));
                }
            }
        }

        for (Match m : matches) {
            m.domain.addAll(allSlots);
        }
    }

    private boolean backtrack(int matchIndex) {
        int N = matches.size();

        if (matchIndex == N)
            return true; // All matches have been assigned

        Match currentMatch = matches.get(matchIndex);

        if (currentMatch.domain.isEmpty())
            return false;

        // Save the current state of all domains (Snapshot)
        List<Set<Slot>> domainSnapshots = new ArrayList<>();

        for (Match m : matches) {
            domainSnapshots.add(new LinkedHashSet<>(m.domain));
        }

        for (Slot slot : currentMatch.domain) {

            currentMatch.assignedSlot = slot;

            if (forwardCheck(matchIndex, slot)) {
                if (backtrack(matchIndex + 1)) {
                    return true;
                }
            }

            backtracks++;

            currentMatch.assignedSlot = null;

            // Restore domains from the snapshot after backtracking
            for (int i = 0; i < matches.size(); i++) {
                matches.get(i).domain.clear();
                matches.get(i).domain.addAll(domainSnapshots.get(i));
            }
        }

        return false;
    }

    private boolean forwardCheck(int matchIndex, Slot assignedSlot) {

        Match currentMatch = matches.get(matchIndex);
        int N = matches.size();

        for (int i = matchIndex + 1; i < N; i++) {

            Match futureMatch = matches.get(i);

            // 1. Stadium capacity: remove the identical slot
            futureMatch.domain.removeIf(s -> s.equals(assignedSlot));

            // 2. Team rest: if the matches share a team, remove all slots on that day
            boolean hasCommonTeam =
                    futureMatch.involves(currentMatch.team1) ||
                            futureMatch.involves(currentMatch.team2);

            if (hasCommonTeam) {
                futureMatch.domain.removeIf(s -> s.day == assignedSlot.day);
            }

            // 3. Sensitive match constraint: at most one sensitive match per day
            if (currentMatch.isImportant && futureMatch.isImportant) {
                futureMatch.domain.removeIf(s -> s.day == assignedSlot.day);
            }

            // Dead end detected
            if (futureMatch.domain.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}