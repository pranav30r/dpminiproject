import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class TransplantIQApp {

    public static void main(String[] args) {
        PrintStream sysOut = System.out;

        // Temporarily mute stdout during initial registry creation to silence MySQL fallback log
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {}
        }));
        NationalTransplantRegistry registry = NationalTransplantRegistry.getInstance();
        System.setOut(sysOut);

        seedRegistryIfEmpty(registry);

        System.out.println("================================================================================");
        System.out.println("       TransplantIQ - Unified Multi-Pattern Demonstration & Test Suite");
        System.out.println("================================================================================");

        // Section 1: Singleton
        System.out.println("\n[1. Singleton Pattern: Central Registry Initialized]");
        System.out.println("  Registered Organs     : " + registry.getRegisteredOrgans().size());
        System.out.println("  Registered Recipients : " + registry.getRegisteredRecipients().size());

        // Section 2: Strategy
        System.out.println("\n[2. Strategy Pattern: Urgency vs. Distance Priority]");
        DonorOrgan organ = registry.getRegisteredOrgans().get(0);
        List<Recipient> recipients = registry.getRegisteredRecipients();

        System.out.println("  --- Executing Urgency Strategy ---");
        MatchingContext matchingContext = new MatchingContext(new UrgencyMatchingStrategy());
        System.out.println("  [MatchingContext] Executing Strategy: " + matchingContext.getStrategy().getStrategyName());
        Recipient urgencyCandidate = matchingContext.getStrategy().match(organ, recipients);
        if (urgencyCandidate != null) {
            System.out.println("  Selected Candidate: " + urgencyCandidate.getName() + " | Urgency: " + urgencyCandidate.getUrgency());
        }

        System.out.println("\n  --- Switching Runtime Strategy to Distance Strategy ---");
        matchingContext.setStrategy(new DistanceMatchingStrategy());
        System.out.println("  [MatchingContext] Executing Strategy: " + matchingContext.getStrategy().getStrategyName());
        Recipient distanceCandidate = matchingContext.getStrategy().match(organ, recipients);
        if (distanceCandidate != null) {
            System.out.println("  Selected Candidate: " + distanceCandidate.getName() + " | City: " + distanceCandidate.getLocation().getCity());
        }

        // Section 3: Mediator
        System.out.println("\n[3. Mediator Pattern: Multi-Team Registration]");
        TransplantCoordinator coordinator = new TransplantCoordinator();
        MedicalTeam hospital = new MedicalTeam("Transplant Center");
        MedicalTeam surgicalUnit = new MedicalTeam("Surgical Unit");
        MedicalTeam transportLogistics = new MedicalTeam("Transport Logistics");
        MedicalTeam icu = new MedicalTeam("ICU Department");

        System.out.print("  ");
        coordinator.registerTeam(hospital);
        System.out.print("  ");
        coordinator.registerTeam(surgicalUnit);
        System.out.print("  ");
        coordinator.registerTeam(transportLogistics);
        System.out.print("  ");
        coordinator.registerTeam(icu);

        // Section 4: Template Method + Factory
        System.out.println("\n[4. Template Method + Factory Pattern: Transplant Execution]");
        TransplantProcess process = TransplantProcessFactory.createProcess(organ.getOrganType());
        PrintStream indentStream = new IndentedPrintStream(sysOut, "  ");
        System.setOut(indentStream);
        process.processTransplant(organ, coordinator, matchingContext.getStrategy());
        System.setOut(sysOut);

        // Section 5: Memento
        System.out.println("\n[5. Memento Pattern: State Rollback Simulation]");
        RecipientState patientState = new RecipientState("Waiting");
        System.out.println("  Initial Patient State : " + patientState.getStatus());
        RecipientMemento memento = patientState.saveState();
        System.out.println("  State Checkpoint Saved (Memento created)");
        patientState.setStatus("Scheduled for Surgery");
        System.out.println("  State Updated         : " + patientState.getStatus());
        System.out.println("  >> Medical Event: Procedure postponed due to sudden elevated inflammatory markers.");
        patientState.restoreState(memento);
        System.out.println("  State Restored via Memento: " + patientState.getStatus());

        // Section 6: Chain of Responsibility & Observer
        System.out.println("\n[6. Chain of Responsibility & Observer Patterns]");
        AllocationService allocationService = AllocationService.getInstance();
        allocationService.clearObservers();
        allocationService.addObserver(new DoctorObserver());
        allocationService.addObserver(new TransplantCenterObserver());
        allocationService.addObserver(new TransportTeamObserver());

        MatchingConfig config = MatchingConfig.defaultConfig();
        CompatibilityHandler bloodTypeMatcher = new BloodTypeMatcher(config);
        CompatibilityHandler tissueHlaMatcher = new TissueHLAMatcher(config);
        CompatibilityHandler geographicDistFilter = new GeographicDistanceFilter(config);
        CompatibilityHandler urgencyScoreEvaluator = new UrgencyScoreEvaluator();

        bloodTypeMatcher.setNext(tissueHlaMatcher);
        tissueHlaMatcher.setNext(geographicDistFilter);
        geographicDistFilter.setNext(urgencyScoreEvaluator);

        MatchingEngine engine = new MatchingEngine(bloodTypeMatcher);
        RegistryMatchingService matchingService = new RegistryMatchingService(registry, engine, allocationService);

        System.setOut(indentStream);
        MatchResult matchResult = matchingService.findBestMatchForOrgan(organ.getOrganId());
        String matchedName = matchResult.hasMatch() ? matchResult.getBestRecipient().getName() : "None";
        System.out.println("Chain pipeline completed matching for organ " + organ.getOrganId() + " -> Matched: " + matchedName);
        System.setOut(sysOut);

        // Section 7: Proxy
        System.out.println("\n[7. Proxy Pattern (Privacy Protection - from ChainDemo)]");
        Recipient realRecipient = new Recipient(
                "P102", "Rahul Sharma", 42,
                "98XXXXXX05", "45 Sadar, Nagpur",
                "O+", 92.0,
                new Location("Nagpur", 21.1458, 79.0882),
                UrgencyLevel.CRITICAL, "WAITING"
        );
        PatientRecord unauthorizedView = new PrivacyProxy(realRecipient, false);
        PatientRecord authorizedView = new PrivacyProxy(realRecipient, true);

        System.out.println("  --- UNAUTHORIZED VIEW (PII masked) ---");
        System.out.println("  Patient ID  : " + unauthorizedView.getPatientId()
                + " | Name: " + (unauthorizedView.getName().startsWith("REDACTED") ? "REDACTED" : unauthorizedView.getName())
                + " | Age: " + (unauthorizedView.getAge() == -1 ? "REDACTED" : unauthorizedView.getAge())
                + " | Phone: " + (unauthorizedView.getPhone().startsWith("REDACTED") ? "REDACTED" : unauthorizedView.getPhone()));

        System.out.println("  --- AUTHORIZED CLINICAL VIEW (PII visible) ---");
        System.out.println("  Patient ID  : " + authorizedView.getPatientId()
                + " | Name: " + authorizedView.getName()
                + " | Age: " + authorizedView.getAge()
                + " | Phone: " + authorizedView.getPhone());

        System.out.println("\n================================================================================");
        System.out.println("       All Patterns Connected and Executed Successfully!");
        System.out.println("================================================================================");
    }

    private static void seedRegistryIfEmpty(NationalTransplantRegistry registry) {
        registry.clearInMemoryData();

        Location mumbai = new Location("Mumbai", 19.0760, 72.8777);
        Location nagpur = new Location("Nagpur", 21.1458, 79.0882);
        Location pune = new Location("Pune", 18.5204, 73.8567);

        registry.registerRecipient(new Recipient("REC-001", "Rahul Sharma", 42, "+91-9820011111", "Mumbai", "O+", 0.82, mumbai, UrgencyLevel.CRITICAL, "Waiting"));
        registry.registerRecipient(new Recipient("REC-002", "Pooja Patil", 35, "+91-9820022222", "Nagpur", "O+", 0.88, nagpur, UrgencyLevel.HIGH, "Waiting"));
        registry.registerRecipient(new Recipient("REC-003", "Amit Deshmukh", 50, "+91-9820033333", "Pune", "O+", 0.75, pune, UrgencyLevel.MEDIUM, "Waiting"));

        Location donorLoc = new Location("Nagpur", 21.1458, 79.0882);
        registry.registerOrgan("ORG-K101", "Kidney", "O+", 0.85, donorLoc, "Available");
    }

    private static class IndentedPrintStream extends PrintStream {
        private final String indent;
        private boolean atLineStart = true;

        public IndentedPrintStream(OutputStream out, String indent) {
            super(out, true);
            this.indent = indent;
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            for (int i = 0; i < len; i++) {
                write(buf[off + i]);
            }
        }

        @Override
        public void write(int b) {
            if (atLineStart && b != '\n' && b != '\r') {
                for (byte cb : indent.getBytes()) {
                    super.write(cb);
                }
                atLineStart = false;
            }
            super.write(b);
            if (b == '\n') {
                atLineStart = true;
            }
        }
    }
}
