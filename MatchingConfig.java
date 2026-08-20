public class MatchingConfig {
    private final double minimumHlaCompatibility;
    private final double maximumDistanceKm;
    private final int exactBloodMatchScore;
    private final int compatibleBloodMatchScore;
    private final int maximumHlaScoreContribution;
    private final int maximumDistanceScoreContribution;

    public MatchingConfig(
            double minimumHlaCompatibility,
            double maximumDistanceKm,
            int exactBloodMatchScore,
            int compatibleBloodMatchScore,
            int maximumHlaScoreContribution,
            int maximumDistanceScoreContribution
    ) {
        this.minimumHlaCompatibility = minimumHlaCompatibility;
        this.maximumDistanceKm = maximumDistanceKm;
        this.exactBloodMatchScore = exactBloodMatchScore;
        this.compatibleBloodMatchScore = compatibleBloodMatchScore;
        this.maximumHlaScoreContribution = maximumHlaScoreContribution;
        this.maximumDistanceScoreContribution = maximumDistanceScoreContribution;
    }

    public static MatchingConfig defaultConfig() {
        return new MatchingConfig(70.0, 100.0, 20, 15, 40, 15);
    }

    public double getMinimumHlaCompatibility() {
        return minimumHlaCompatibility;
    }

    public double getMaximumDistanceKm() {
        return maximumDistanceKm;
    }

    public int getExactBloodMatchScore() {
        return exactBloodMatchScore;
    }

    public int getCompatibleBloodMatchScore() {
        return compatibleBloodMatchScore;
    }

    public int getMaximumHlaScoreContribution() {
        return maximumHlaScoreContribution;
    }

    public int getMaximumDistanceScoreContribution() {
        return maximumDistanceScoreContribution;
    }
}
