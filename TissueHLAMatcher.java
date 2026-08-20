// Chain of Responsibility Pattern - Hiten
public class TissueHLAMatcher extends AbstractCompatibilityHandler {
    private final MatchingConfig config;

    public TissueHLAMatcher(MatchingConfig config) {
        this.config = config;
    }

    @Override
    public RecipientEvaluation handle(
            DonorOrgan organ,
            Recipient recipient,
            RecipientEvaluation evaluation
    ) {
        double compatibility = 100.0 - Math.abs(organ.getHlaProfile() - recipient.getHlaProfile());
        compatibility = Math.max(0.0, compatibility);
        evaluation.setHlaCompatibility(compatibility);

        if (compatibility < config.getMinimumHlaCompatibility()) {
            evaluation.reject(
                    "HLA compatibility below threshold (" + round(compatibility) + "%)"
            );
            return evaluation;
        }

        int score = (int) Math.round(
                (compatibility / 100.0) * config.getMaximumHlaScoreContribution()
        );
        evaluation.addScore(score);
        evaluation.addDecision(
                "TissueHLAMatcher passed: HLA compatibility "
                        + round(compatibility) + "%, score +" + score
        );
        return continueChain(organ, recipient, evaluation);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
