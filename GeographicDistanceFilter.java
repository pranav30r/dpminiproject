// Chain of Responsibility Pattern - Hiten
public class GeographicDistanceFilter extends AbstractCompatibilityHandler {
    private final MatchingConfig config;

    public GeographicDistanceFilter(MatchingConfig config) {
        this.config = config;
    }

    @Override
    public RecipientEvaluation handle(
            DonorOrgan organ,
            Recipient recipient,
            RecipientEvaluation evaluation
    ) {
        double distanceKm = organ.getLocation().distanceTo(recipient.getLocation());
        evaluation.setDistanceKm(distanceKm);

        if (distanceKm > config.getMaximumDistanceKm()) {
            evaluation.reject(
                    "Recipient is outside distance limit (" + round(distanceKm) + " km)"
            );
            return evaluation;
        }

        double ratio = 1.0 - (distanceKm / config.getMaximumDistanceKm());
        int score = (int) Math.round(
                ratio * config.getMaximumDistanceScoreContribution()
        );
        evaluation.addScore(Math.max(score, 0));
        evaluation.addDecision(
                "GeographicDistanceFilter passed: distance "
                        + round(distanceKm) + " km, score +" + Math.max(score, 0)
        );
        return continueChain(organ, recipient, evaluation);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
