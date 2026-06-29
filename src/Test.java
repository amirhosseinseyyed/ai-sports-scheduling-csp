import java.util.*;

public class Test {
    static int S, D, H, N, K;
    static List<String> stadiums = new ArrayList<>();
    static List<Match> matches = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // First line: number of stadiums (S)
        S = scanner.nextInt();

        // Second line: stadium names
        for (int i = 0; i < S; i++) {
            stadiums.add(scanner.next());
        }

        // Third line: number of days (D)
        D = scanner.nextInt();

        // Fourth line: number of time slots (H)
        H = scanner.nextInt();

        // Fifth line: total number of matches (N)
        N = scanner.nextInt();

        // Next N lines: matches (initially assume all are non-sensitive)
        for (int i = 0; i < N; i++) {
            String t1 = scanner.next();
            String t2 = scanner.next();
            matches.add(new Match(t1, t2, false));
        }

        // Next line: number of sensitive matches (K)
        K = scanner.nextInt();

        // Next K lines: sensitive matches
        for (int i = 0; i < K; i++) {
            String t1 = scanner.next();
            String t2 = scanner.next();

            // Find the corresponding match in the list and mark it as sensitive
            for (Match m : matches) {
                if (m.involves(t1) && m.involves(t2)) {
                    m.isImportant = true;
                    break;
                }
            }
        }

        // Initial feasibility checks (Pre-processing)
        if (N > D * H * S || K > D) {
            System.out.println("No Solution");
            return;
        }

        // All CSP work (domain setup, backtracking, forward checking) lives in Scheduler now
        Scheduler scheduler = new Scheduler(D, H, S, stadiums, matches);

        // Start timer (nanoseconds)
        long startTime = System.nanoTime();

        boolean success = scheduler.solve();

        // End timer (nanoseconds)
        long endTime = System.nanoTime();

        if (success) {
            // Print the assignment list
            for (Match m : matches) {
                System.out.println(m.team1 + " " + m.team2 + " " + m.assignedSlot);
            }

            // Print the number of backtracks
            System.out.println("Backtracks: " + scheduler.getBacktracks());

            // Calculate and print execution time in seconds
            double timeInSeconds = (endTime - startTime) / 1_000_000_000.0;
            System.out.printf("Time: %.6fs%n", timeInSeconds);

        } else {
            System.out.println("No Solution");
        }
    }
}