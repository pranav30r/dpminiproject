import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TransplantIQ - Smart Organ-Recipient Matching System
 *
 * Console demo showcasing all 5 Design Patterns:
 *   1. Factory Method  (Jogi)
 *   2. Singleton       (Jogi)
 *   3. Chain of Responsibility (Hiten)
 *   4. Observer        (Rathi)
 *   5. Proxy           (Chetan)
 */
public class ChainDemo {

    public static void main(String[] args) {
        printBanner();

        NationalTransplantRegistry registry = NationalTransplantRegistry.getInstance();
        AllocationService allocationService = prepareAllocationService();

        boolean databaseLoaded = registry.refreshFromDatabase();
        if (databaseLoaded) {
            allocationService.replaceNotificationsLog(registry.loadNotificationMessages());
        } else {
            registry.clearInMemoryData();
        }

        printRegistryMode(registry);

        System.out.println("=== [Jogi] Pattern 2: Singleton ===");
        System.out.println("Registry instance #1 : " + registry.hashCode());
        NationalTransplantRegistry registryAgain = NationalTransplantRegistry.getInstance();
        System.out.println("Registry instance #2 : " + registryAgain.hashCode());
        System.out.println("Same instance?       : " + (registry == registryAgain));
        System.out.println();

        System.out.println("=== [Jogi] Pattern 1: Factory Method ===");
        if (databaseLoaded) {
            if (registry.getRegisteredOrgans().isEmpty() || registry.getRegisteredRecipients().isEmpty()) {
                System.out.println("Database connection is working, but no seed data was found.");
                System.out.println("Run organmatch_db.sql in MySQL, then rerun ChainDemo.");
                return;
            }

            System.out.println("Loaded " + registry.getRegisteredOrgans().size() + " organs from MySQL.");
            System.out.println("Each organ row was recreated through OrganFactory.createOrgan(...).");
            printOrganPreview(registry.getAvailableOrgans());
            System.out.println("Loaded " + registry.getRegisteredRecipients().size() + " recipients from MySQL.");
        } else {
            seedRegistry(registry);
        }
        System.out.println();

        MatchingConfig config = MatchingConfig.defaultConfig();
        CompatibilityHandler bloodTypeMatcher = new BloodTypeMatcher(config);
        CompatibilityHandler tissueHlaMatcher = new TissueHLAMatcher(config);
        CompatibilityHandler geographicDistFilter = new GeographicDistanceFilter(config);
        CompatibilityHandler urgencyScoreEvaluator = new UrgencyScoreEvaluator();

        bloodTypeMatcher.setNext(tissueHlaMatcher);
        tissueHlaMatcher.setNext(geographicDistFilter);
        geographicDistFilter.setNext(urgencyScoreEvaluator);

        MatchingEngine engine = new MatchingEngine(bloodTypeMatcher);
        RegistryMatchingService matchingService = new RegistryMatchingService(
                registry, engine, allocationService
        );

        List<String> organIdsToMatch = selectOrganIdsForDemo(registry);
        MatchResult proxyPreviewMatch = null;

        System.out.println("=== [Hiten] Pattern 3: Chain of Responsibility ===");
        for (String organId : organIdsToMatch) {
            DonorOrgan organ = registry.findOrganById(organId);
            if (organ == null) {
                continue;
            }

            System.out.println("Running matching pipeline for Organ ID: " + organ.getOrganId()
                    + " (" + organ.getOrganType() + ", " + organ.getBloodGroup() + ")");
            System.out.println();

            MatchResult result = matchingService.findBestMatchForOrgan(organ.getOrganId());
            printEvaluationDetails(result);

            System.out.println("--- Best Match for " + organ.getOrganId() + " ---");
            printBestMatch(result);
            System.out.println();

            if (proxyPreviewMatch == null && result.hasMatch()) {
                proxyPreviewMatch = result;
            }
        }

        System.out.println("=== [Rathi] Pattern 4: Observer - Notification Log ===");
        List<String> log = allocationService.getNotificationsLog();
        if (log.isEmpty()) {
            System.out.println("  (no notifications)");
        } else {
            for (int i = 0; i < log.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + log.get(i));
            }
        }
        System.out.println();

        System.out.println("=== [Chetan] Pattern 5: Proxy (Privacy Protection) ===");
        if (proxyPreviewMatch != null && proxyPreviewMatch.hasMatch()) {
            PatientRecord realRecord = proxyPreviewMatch.getBestRecipient();
            PatientRecord unauthorizedView = new PrivacyProxy(realRecord, false);
            PatientRecord authorizedView = new PrivacyProxy(realRecord, true);

            System.out.println();
            System.out.println("--- UNAUTHORIZED VIEW (PII masked) ---");
            printPatientRecord(unauthorizedView);

            System.out.println();
            System.out.println("--- AUTHORIZED CLINICAL VIEW (PII visible) ---");
            printPatientRecord(authorizedView);
        } else {
            System.out.println("  No successful match was available for proxy preview.");
        }
        System.out.println();

        System.out.println("=== SYSTEM SUMMARY ===");
        System.out.println("Registered Organs     : " + registry.getRegisteredOrgans().size());
        System.out.println("Available Organs      : " + registry.getAvailableOrgans().size());
        System.out.println("Registered Recipients : " + registry.getRegisteredRecipients().size());
        System.out.println("Waiting Recipients    : " + registry.getWaitingRecipients().size());
        System.out.println("Successful Matches    : " + registry.getRecordedMatches().size());
        System.out.println("Observer Alerts Sent  : " + allocationService.getNotificationsLog().size());
    }

    private static AllocationService prepareAllocationService() {
        AllocationService allocationService = AllocationService.getInstance();
        allocationService.clearObservers();
        allocationService.clearNotifications();
        allocationService.addObserver(new TransplantCenterObserver());
        allocationService.addObserver(new DoctorObserver());
        allocationService.addObserver(new TransportTeamObserver());
        return allocationService;
    }

    private static void printBanner() {
        System.out.println("==============================================================");
        System.out.println("TRANSPLANTIQ - Smart Organ Matching System");
        System.out.println("Design Patterns Lab Mini Project");
        System.out.println("==============================================================");
        System.out.println();
    }

    private static void printRegistryMode(NationalTransplantRegistry registry) {
        if (registry.isDatabaseConnected()) {
            System.out.println("Data mode: MySQL-backed registry");
            System.out.println("Database: " + registry.getDatabaseUrl());
        } else {
            System.out.println("Data mode: In-memory demo fallback");
            System.out.println("Tip: add mysql-connector-j to the classpath and set the DB password to enable MySQL mode.");
        }
        System.out.println();
    }

    private static void printOrganPreview(List<DonorOrgan> organs) {
        if (organs.isEmpty()) {
            System.out.println("No available organs are currently loaded.");
            return;
        }

        int previewCount = Math.min(organs.size(), 3);
        for (int i = 0; i < previewCount; i++) {
            DonorOrgan organ = organs.get(i);
            System.out.println("  Hydrated via Factory: " + organ.getOrganType()
                    + " (ID: " + organ.getOrganId() + ", Status: " + organ.getStatus() + ")");
            organ.getOrgan().displayInfo();
        }
    }

    private static List<String> selectOrganIdsForDemo(NationalTransplantRegistry registry) {
        List<String> organIds = new ArrayList<>();
        for (DonorOrgan organ : registry.getAvailableOrgans()) {
            organIds.add(organ.getOrganId());
            if (organIds.size() == 2) {
                break;
            }
        }

        if (organIds.isEmpty() && registry.findOrganById("K101") != null) {
            organIds.add("K101");
        }

        return organIds;
    }

    private static void seedRegistry(NationalTransplantRegistry registry) {
        DonorOrgan kidney = registry.registerOrgan(
                "K101", "kidney", "O+", 90,
                new Location("Nagpur", 21.1458, 79.0882), "AVAILABLE"
        );
        System.out.println("  Created via Factory: " + kidney.getOrganType()
                + " (ID: " + kidney.getOrganId() + ")");
        kidney.getOrgan().displayInfo();

        DonorOrgan heart = registry.registerOrgan(
                "H201", "heart", "A-", 85,
                new Location("Mumbai", 19.0760, 72.8777), "AVAILABLE"
        );
        System.out.println("  Created via Factory: " + heart.getOrganType()
                + " (ID: " + heart.getOrganId() + ")");
        heart.getOrgan().displayInfo();

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
        if (result.getMatchId() != null) {
            System.out.println("  Match ID     : " + result.getMatchId());
        }
    }

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
}
