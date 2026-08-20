import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {
    private final CompatibilityHandler firstHandler;

    public MatchingEngine(CompatibilityHandler firstHandler) {
        this.firstHandler = firstHandler;
    }

    public MatchResult findBestMatch(DonorOrgan organ, List<Recipient> recipients) {
        List<RecipientEvaluation> allEvaluations = new ArrayList<>();
        RecipientEvaluation bestEvaluation = null;

        for (Recipient recipient : recipients) {
            RecipientEvaluation evaluation = new RecipientEvaluation(recipient);
            firstHandler.handle(organ, recipient, evaluation);
            allEvaluations.add(evaluation);

            if (evaluation.isEligible()) {
                if (bestEvaluation == null
                        || evaluation.getCompatibilityScore() > bestEvaluation.getCompatibilityScore()) {
                    bestEvaluation = evaluation;
                }
            }
        }

        Recipient bestRecipient = bestEvaluation == null ? null : bestEvaluation.getRecipient();
        return new MatchResult(organ, bestRecipient, bestEvaluation, allEvaluations);
    }
}
