import java.util.ArrayList;
import java.util.List;

/**
 * Context class in the Strategy Pattern.
 * Maintains a reference to a MatchingStrategy and delegates matching algorithm execution
 * to the currently configured strategy. Allows dynamic runtime switching between strategies.
 */
public class MatchingContext {

    private MatchingStrategy strategy;

    public MatchingContext(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    public MatchingStrategy getStrategy() {
        return this.strategy;
    }

    public Recipient executeStrategy(DonorOrgan organ, List<Recipient> recipients) {
        if (this.strategy == null) {
            throw new IllegalStateException("Matching strategy is not set in context.");
        }
        System.out.println("\n[MatchingContext] Executing Strategy: " + strategy.getStrategyName());
        return this.strategy.match(organ, recipients);
    }

    /**
     * Standalone demonstration of the Strategy Pattern:
     * Shows runtime switching between Urgency-priority matching and Proximity-priority matching.
     */
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   TransplantIQ - Strategy Pattern Demonstration ");
        System.out.println("======================================================================");

        // 1. Setup Donor Organ (Located in Nagpur, Blood Group O+)
        Location donorLocation = new Location("Nagpur", 21.1458, 79.0882);
        DonorOrgan donorOrgan = new DonorOrgan(
                "ORG-K101",
                new Kidney(),
                "O+",
                0.85,
                donorLocation,
                "Available"
        );

        System.out.println("\n[Available Donor Organ]");
        System.out.println("- Organ ID    : " + donorOrgan.getOrganId());
        System.out.println("- Organ Type  : " + donorOrgan.getOrganType());
        System.out.println("- Blood Group : " + donorOrgan.getBloodGroup());
        System.out.println("- City/Location: " + donorOrgan.getLocation().getCityName());

        // 2. Setup candidate recipients with different cities and urgency levels
        List<Recipient> candidateList = new ArrayList<>();

        // Patient 1: Located far away (Mumbai ~685 km), but in CRITICAL condition
        candidateList.add(new Recipient(
                "REC-001", "Rahul Sharma", 45, "9823011111", "Andheri, Mumbai",
                "O+", 0.82, new Location("Mumbai", 19.0760, 72.8777),
                UrgencyLevel.CRITICAL, "Waiting"
        ));

        // Patient 2: Located locally (Nagpur ~0 km), but HIGH condition (not critical)
        candidateList.add(new Recipient(
                "REC-002", "Priya Verma", 32, "9823022222", "Dharampeth, Nagpur",
                "O+", 0.88, new Location("Nagpur", 21.1458, 79.0882),
                UrgencyLevel.HIGH, "Waiting"
        ));

        // Patient 3: Located in Pune (~620 km), MEDIUM condition
        candidateList.add(new Recipient(
                "REC-003", "Amit Patel", 58, "9823033333", "Kothrud, Pune",
                "O+", 0.75, new Location("Pune", 18.5204, 73.8567),
                UrgencyLevel.MEDIUM, "Waiting"
        ));

        // Patient 4: Incompatible blood group (B+) in Nagpur
        candidateList.add(new Recipient(
                "REC-004", "Neha Singh", 28, "9823044444", "Sitabuldi, Nagpur",
                "B+", 0.90, new Location("Nagpur", 21.1458, 79.0882),
                UrgencyLevel.CRITICAL, "Waiting"
        ));

        System.out.println("\n[Candidate Recipients Registered in Pool]");
        for (Recipient r : candidateList) {
            double dist = donorOrgan.getLocation().distanceTo(r.getLocation());
            System.out.printf("  * ID: %-7s | Name: %-13s | Blood: %-2s | Urgency: %-8s | City: %-7s (Dist: %.1f km)\n",
                    r.getPatientId(), r.getName(), r.getBloodGroup(), r.getUrgency(), r.getLocation().getCityName(), dist);
        }

        // 3. Initialize Context with Strategy 1: Urgency Priority Matching
        MatchingContext context = new MatchingContext(new UrgencyMatchingStrategy());
        Recipient selectedByUrgency = context.executeStrategy(donorOrgan, candidateList);

        if (selectedByUrgency != null) {
            System.out.println(">> Result Selected : " + selectedByUrgency.getPatientId()
                    + " (" + selectedByUrgency.getName() + ")");
            System.out.println("   Reason          : Prioritized highest medical urgency ("
                    + selectedByUrgency.getUrgency() + ") despite being in "
                    + selectedByUrgency.getLocation().getCityName());
        }

        // 4. Switch Strategy dynamically at runtime to Strategy 2: Distance Priority Matching
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println(">> Dynamically switching strategy to: Distance / Proximity Priority...");
        context.setStrategy(new DistanceMatchingStrategy());
        Recipient selectedByDistance = context.executeStrategy(donorOrgan, candidateList);

        if (selectedByDistance != null) {
            double dist = donorOrgan.getLocation().distanceTo(selectedByDistance.getLocation());
            System.out.println(">> Result Selected : " + selectedByDistance.getPatientId()
                    + " (" + selectedByDistance.getName() + ")");
            System.out.println("   Reason          : Prioritized shortest transit distance ("
                    + String.format("%.2f km", dist) + ") to minimize organ cold ischemia time.");
        }

        System.out.println("\n======================================================================");
        System.out.println("Strategy pattern successfully decoupled algorithm selection at runtime!");
        System.out.println("======================================================================");
    }
}
