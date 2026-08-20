public class RegistryMatchingService {
    private final NationalTransplantRegistry registry;
    private final MatchingEngine matchingEngine;
    private final AllocationService allocationService;

    public RegistryMatchingService(
            NationalTransplantRegistry registry,
            MatchingEngine matchingEngine,
            AllocationService allocationService
    ) {
        this.registry = registry;
        this.matchingEngine = matchingEngine;
        this.allocationService = allocationService;
    }

    public MatchResult findBestMatchForOrgan(String organId) {
        DonorOrgan organ = registry.findOrganById(organId);
        if (organ == null) {
            throw new IllegalArgumentException("No organ found with ID: " + organId);
        }

        MatchResult matchResult = matchingEngine.findBestMatch(
                organ,
                registry.getRegisteredRecipients()
        );

        if (matchResult.hasMatch()) {
            registry.recordMatch(matchResult);
            // Observer Pattern - Rathi: automatically notify all observers
            allocationService.confirmMatch(matchResult);
        }

        return matchResult;
    }
}
