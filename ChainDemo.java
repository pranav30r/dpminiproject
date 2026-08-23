import java.util.Arrays;
import java.util.List;

/**
 * TransplantIQ - Smart Organ-Recipient Matching System
 *
 * Console demo showcasing all 5 Design Patterns:
 *   1. Factory Method          - OrganFactory creates organ instances
 *   2. Singleton               - NationalTransplantRegistry single instance
 *   3. Chain of Responsibility - Compatibility handler pipeline
 *   4. Observer                - AllocationService notifies observers on match
 *   5. Proxy                   - PrivacyProxy masks PII for unauthorized users
 */
public class ChainDemo {

    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("      TRANSPLANTIQ - Smart Organ Matching System            ");
        System.out.println("           Design Patterns Lab Mini Project                 ");
        System.out.println("============================================================");
        System.out.println();

        // ------------------------------------------------------------
        // PATTERN 2: Singleton
        // NationalTransplantRegistry.getInstance() always returns the
        // same object.  Private constructor prevents external creation.
        // ------------------------------------------------------------
        NationalTransplantRegistry registry = NationalTransplantRegistry.getInstance();
        registry.clearInMemoryData();

        System.out.println("=== Singleton: NationalTransplantRegistry ===");
        System.out.println("  Instance #1: " + registry.hashCode());
        NationalTransplantRegistry registryAgain = NationalTransplantRegistry.getInstance();
        System.out.println("  Instance #2: " + registryAgain.hashCode());
        System.out.println("Same instance: " + (registry == registryAgain));
        System.out.println();

        // ------------------------------------------------------------
        // PATTERN 1: Factory Method
        // OrganFactory.createOrgan(type) returns the correct subclass
        // (Kidney, Heart, Liver, Lung) without the caller knowing
        // which concrete class was instantiated.
        // ------------------------------------------------------------
        System.out.println("=== Factory Method: OrganFactory ===");
        seedRegistry(registry);
        System.out.println();

        // ------------------------------------------------------------
        // PATTERN 4: Observer
        // AllocationService is the Subject.  Three concrete observers
        // are registered.  When confirmMatch() is called they each
        // receive the MatchResult automatically.
        // ------------------------------------------------------------
        AllocationService allocationService = AllocationService.getInstance();
        allocationService.clearNotifications();
        allocationService.addObserver(new TransplantCenterObserver());
        allocationService.addObserver(new DoctorObserver());
        allocationService.addObserver(new TransportTeamObserver());

        // ------------------------------------------------------------
        // PATTERN 3: Chain of Responsibility
        // Four handlers are chained together.  Each one either rejects
        // the candidate or passes it to the next handler.
        // ------------------------------------------------------------
        MatchingConfig config = MatchingConfig.defaultConfig();
        CompatibilityHandler bloodTypeMatcher      = new BloodTypeMatcher(config);
        CompatibilityHandler tissueHlaMatcher      = new TissueHLAMatcher(config);
        CompatibilityHandler geographicDistFilter  = new GeographicDistanceFilter(config);
        CompatibilityHandler urgencyScoreEvaluator = new UrgencyScoreEvaluator();

        bloodTypeMatcher.setNext(tissueHlaMatcher);
        tissueHlaMatcher.setNext(geographicDistFilter);
        geographicDistFilter.setNext(urgencyScoreEvaluator);

        MatchingEngine engine = new MatchingEngine(bloodTypeMatcher);

        // RegistryMatchingService ties Registry, Engine and AllocationService together
        RegistryMatchingService matchingService = new RegistryMatchingService(
                registry, engine, allocationService
        );

        // ----------------------------------------------------------
        // RUN MATCHING FOR KIDNEY K101
        // ----------------------------------------------------------
        System.out.println("=== Chain of Responsibility: Compatibility Evaluation ===");
        System.out.println("Running matching pipeline for Organ ID: K101 (Kidney, O+)");
        System.out.println();

        MatchResult kidneyResult = matchingService.findBestMatchForOrgan("K101");
        printEvaluationDetails(kidneyResult);

        System.out.println("--- Best Match for K101 ---");
        printBestMatch(kidneyResult);
        System.out.println();

        // ----------------------------------------------------------
        // RUN MATCHING FOR HEART H201
        // ----------------------------------------------------------
        System.out.println("Running matching pipeline for Organ ID: H201 (Heart, A-)");
        System.out.println();

        MatchResult heartResult = matchingService.findBestMatchForOrgan("H201");
        printEvaluationDetails(heartResult);

        System.out.println("--- Best Match for H201 ---");
        printBestMatch(heartResult);
        System.out.println();

        // ----------------------------------------------------------
        // OBSERVER NOTIFICATIONS LOG
        // ----------------------------------------------------------
        System.out.println("=== Observer: AllocationService ===");
        List<String> log = allocationService.getNotificationsLog();
        if (log.isEmpty()) {
            System.out.println("  (no notifications)");
        } else {
            for (int i = 0; i < log.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + log.get(i));
            }
        }
        System.out.println();

        // ------------------------------------------------------------
        // PATTERN 5: Proxy
        // PrivacyProxy wraps a real Recipient (which implements
        // PatientRecord).  PII fields are masked for unauthorized users
        // while medical fields pass through untouched.
        // ------------------------------------------------------------
        System.out.println("=== Protection Proxy: PrivacyProxy ===");
        if (kidneyResult.hasMatch()) {
            PatientRecord realRecord       = kidneyResult.getBestRecipient();
            PatientRecord unauthorizedView = new PrivacyProxy(realRecord, false);
            PatientRecord authorizedView   = new PrivacyProxy(realRecord, true);

            System.out.println();
            System.out.println("  PUBLIC (PII Redacted):");
            printPatientRecord(unauthorizedView);

            System.out.println();
            System.out.println("  AUTHORIZED (PII Visible):");
            printPatientRecord(authorizedView);
        }
        System.out.println();

        // ----------------------------------------------------------
        // SUMMARY
        // ----------------------------------------------------------
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("|                       SYSTEM SUMMARY                         |");
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("|  Registered Organs     : " + pad(registry.getRegisteredOrgans().size())          + "|");
        System.out.println("|  Registered Recipients : " + pad(registry.getRegisteredRecipients().size())      + "|");
        System.out.println("|  Successful Matches    : " + pad(registry.getRecordedMatches().size())           + "|");
        System.out.println("|  Observer Alerts Sent  : " + pad(allocationService.getNotificationsLog().size()) + "|");
        System.out.println("+---------------------------------------------------------------+");
    }

    // ------------------------------------------------------------
    // HELPER : Seed sample data into the registry
    // Uses Factory Method inside registerOrgan()
    // ------------------------------------------------------------
    private static void seedRegistry(NationalTransplantRegistry registry) {
        // Kidney donor in Nagpur
        DonorOrgan kidney = registry.registerOrgan(
                "K101", "kidney", "O+", 90,
                new Location("Nagpur", 21.1458, 79.0882), "AVAILABLE"
        );
        System.out.println("  Created via Factory: " + kidney.getOrganType()
                + " (ID: " + kidney.getOrganId() + ")");
        kidney.getOrgan().displayInfo();

        // Heart donor in Mumbai
        DonorOrgan heart = registry.registerOrgan(
                "H201", "heart", "A-", 85,
                new Location("Mumbai", 19.0760, 72.8777), "AVAILABLE"
        );
        System.out.println("  Created via Factory: " + heart.getOrganType()
                + " (ID: " + heart.getOrganId() + ")");
        heart.getOrgan().displayInfo();

        // Register recipients
        Arrays.asList(
                new Recipient("P101", "Aarav Mehta", 44,
                        "98XXXXXX01", "12 MG Road, Nagpur",
                        "A+", 84,
                        new Location("Nagpur", 21.1498, 79.0806),
                        UrgencyLevel.HIGH, "WAITING"),

                new Recipient("P102", "Rahul Sharma", 42,
                        "98XXXXXX05", "45 Sadar, Nagpur",
                        "O+", 92,
                        new Location("Nagpur", 21.1570, 79.0720),
                        UrgencyLevel.CRITICAL, "WAITING"),

                new Recipient("P103", "Neha Verma", 39,
                        "98XXXXXX09", "7 FC Road, Pune",
                        "O+", 55,
                        new Location("Pune", 18.5204, 73.8567),
                        UrgencyLevel.HIGH, "WAITING"),

                new Recipient("P104", "Kunal Joshi", 50,
                        "98XXXXXX11", "22 Linking Rd, Mumbai",
                        "AB+", 88,
                        new Location("Mumbai", 19.0760, 72.8777),
                        UrgencyLevel.MEDIUM, "WAITING"),

                new Recipient("P105", "Sneha Patil", 35,
                        "98XXXXXX22", "9 Hill Road, Thane",
                        "A-", 87,
                        new Location("Thane", 19.2183, 72.9781),
                        UrgencyLevel.CRITICAL, "WAITING"),

                new Recipient("P106", "Vikram Desai", 48,
                        "98XXXXXX33", "3 Station Rd, Nashik",
                        "A+", 78,
                        new Location("Nashik", 19.9975, 73.7898),
                        UrgencyLevel.LOW, "WAITING")
        ).forEach(registry::registerRecipient);

        System.out.println("  Registered " + registry.getRegisteredRecipients().size() + " recipients.");
    }

    // ------------------------------------------------------------
    // HELPER : Print detailed evaluation for every candidate
    // ------------------------------------------------------------
    private static void printEvaluationDetails(MatchResult result) {
        for (RecipientEvaluation eval : result.getAllEvaluations()) {
            System.out.println("  Recipient : " + eval.getRecipient().getPatientId()
                    + " (" + eval.getRecipient().getName() + ")");
            System.out.println("  Eligible  : " + eval.isEligible());
            System.out.println("  Score     : " + eval.getCompatibilityScore());
            System.out.println("  HLA Match : " + round(eval.getHlaCompatibility()) + "%");
            System.out.println("  Distance  : " + round(eval.getDistanceKm()) + " km");
            System.out.println("  Status    : " + eval.getRejectionReason());
            for (String step : eval.getDecisionTrail()) {
                System.out.println("    -> " + step);
            }
            System.out.println();
        }
    }

    // ------------------------------------------------------------
    // HELPER : Print the best-match summary
    // ------------------------------------------------------------
    private static void printBestMatch(MatchResult result) {
        if (!result.hasMatch()) {
            System.out.println("  No suitable recipient was found.");
            return;
        }
        Recipient best = result.getBestRecipient();
        RecipientEvaluation eval = result.getBestEvaluation();

        System.out.println("  Organ ID     : " + result.getOrgan().getOrganId());
        System.out.println("  Organ Type   : " + result.getOrgan().getOrganType());
        System.out.println("  Recipient ID : " + best.getPatientId());
        System.out.println("  Name         : " + best.getName());
        System.out.println("  Blood Group  : " + best.getBloodGroup());
        System.out.println("  Urgency      : " + best.getUrgency());
        System.out.println("  Final Score  : " + eval.getCompatibilityScore());
        System.out.println("  Distance     : " + round(eval.getDistanceKm()) + " km");
    }

    // ------------------------------------------------------------
    // HELPER : Print a PatientRecord (works with Proxy or Real)
    // ------------------------------------------------------------
    private static void printPatientRecord(PatientRecord record) {
        System.out.println("  Patient ID  : " + record.getPatientId());
        System.out.println("  Name        : " + record.getName());
        System.out.println("  Age         : " + (record.getAge() == -1 ? "REDACTED" : record.getAge()));
        System.out.println("  Phone       : " + record.getPhone());
        System.out.println("  Address     : " + record.getAddress());
        System.out.println("  Blood Group : " + record.getBloodGroup());
        System.out.println("  HLA Profile : " + record.getHlaProfile() + "%");
        System.out.println("  Urgency     : " + record.getUrgency());
        System.out.println("  Location    : " + record.getLocation());
    }

    private static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static String pad(int number) {
        return String.format("%-37d", number);
    }
}
