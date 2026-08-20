// Chain of Responsibility Pattern - Hiten
public class UrgencyScoreEvaluator extends AbstractCompatibilityHandler {
    @Override
    public RecipientEvaluation handle(
            DonorOrgan organ,
            Recipient recipient,
            RecipientEvaluation evaluation
    ) {
        int score = recipient.getUrgency().getScoreContribution();
        evaluation.addScore(score);
        evaluation.addDecision(
                "UrgencyScoreEvaluator passed: urgency "
                        + recipient.getUrgency() + ", score +" + score
        );
        return continueChain(organ, recipient, evaluation);
    }
}
