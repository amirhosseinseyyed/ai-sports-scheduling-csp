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

    /**
     * MRV Heuristic Helper:
     * Selects the unassigned Match with the minimum remaining values (domain size).
     */
    private int selectMRVMatch() {
        int bestIndex = -1;
        int minDomainSize = Integer.MAX_VALUE;

        for (int i = 0; i < matches.size(); i++) {
            Match m = matches.get(i);
            // Ignore already assigned matches
            if (m.assignedSlot == null) {
                int domainSize = m.domain.size();
                // Strictly less than ensures we preserve the original ordering on ties
                if (domainSize < minDomainSize) {
                    minDomainSize = domainSize;
                    bestIndex = i;
                }
            }
        }
        return bestIndex;
    }

    private boolean backtrack(int assignedCount) {
        int N = matches.size();

        if (assignedCount == N)
            return true; // All matches have been assigned

        // MRV: Select the unassigned Match with the smallest remaining domain
        int matchIndex = selectMRVMatch();
        if (matchIndex == -1) return false;

        Match currentMatch = matches.get(matchIndex);

        if (currentMatch.domain.isEmpty())
            return false;

        // Save the current state of all domains (Snapshot)
        List<Set<Slot>> domainSnapshots = new ArrayList<>();

        for (Match m : matches) {
            domainSnapshots.add(new LinkedHashSet<>(m.domain));
        }

        // LCV: Order candidate slots from least to most constraining
        List<Slot> orderedSlots = orderByLCV(matchIndex);

        for (Slot slot : orderedSlots) {

            currentMatch.assignedSlot = slot;

            if (forwardCheck(matchIndex, slot)) {
                if (backtrack(assignedCount + 1)) {
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

    /**
     * LCV Heuristic Helper:
     * Returns the candidate slots of the given match sorted by their LCV score
     * (ascending: least constraining first). Ties preserve the original domain order.
     */
    private List<Slot> orderByLCV(int matchIndex) {
        List<Slot> candidates = new ArrayList<>(matches.get(matchIndex).domain);
        // Stable sort: equal-score slots keep their original LinkedHashSet order
        candidates.sort(Comparator.comparingInt(slot -> computeLCVScore(matchIndex, slot)));
        return candidates;
    }

    /**
     * LCV Score Computation:
     * Counts how many values would be removed from all unassigned future match domains
     * if the given slot were assigned to the match at matchIndex.
     * The computation is purely read-only; no domain is actually modified.
     */
    private int computeLCVScore(int matchIndex, Slot candidateSlot) {
        Match currentMatch = matches.get(matchIndex);
        int eliminations = 0;

        for (int i = 0; i < matches.size(); i++) {
            Match futureMatch = matches.get(i);

            // Only consider unassigned matches other than the current one
            if (i == matchIndex || futureMatch.assignedSlot != null) {
                continue;
            }

            boolean hasCommonTeam =
                    futureMatch.involves(currentMatch.team1) ||
                            futureMatch.involves(currentMatch.team2);

            boolean bothImportant = currentMatch.isImportant && futureMatch.isImportant;

            for (Slot s : futureMatch.domain) {
                // Simulate constraint 1: stadium capacity (same slot)
                if (s.equals(candidateSlot)) {
                    eliminations++;
                    continue; // already eliminated; skip further constraint checks
                }
                // Simulate constraint 2: team rest (same day, shared team)
                if (hasCommonTeam && s.day == candidateSlot.day) {
                    eliminations++;
                    continue;
                }
                // Simulate constraint 3: at most one sensitive match per day
                if (bothImportant && s.day == candidateSlot.day) {
                    eliminations++;
                }
            }
        }

        return eliminations;
    }

    private boolean forwardCheck(int matchIndex, Slot assignedSlot) {

        Match currentMatch = matches.get(matchIndex);
        int N = matches.size();

        for (int i = 0; i < N; i++) {

            Match futureMatch = matches.get(i);

            // Forward checking only applies to unassigned matches
            if (i == matchIndex || futureMatch.assignedSlot != null) {
                continue;
            }

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
