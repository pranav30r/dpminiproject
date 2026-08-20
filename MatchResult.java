import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchResult {
    private final DonorOrgan organ;
    private final Recipient bestRecipient;
    private final RecipientEvaluation bestEvaluation;
    private final List<RecipientEvaluation> allEvaluations;

    public MatchResult(
            DonorOrgan organ,
            Recipient bestRecipient,
            RecipientEvaluation bestEvaluation,
            List<RecipientEvaluation> allEvaluations
    ) {
        this.organ = organ;
        this.bestRecipient = bestRecipient;
        this.bestEvaluation = bestEvaluation;
        this.allEvaluations = new ArrayList<>(allEvaluations);
    }

    public DonorOrgan getOrgan() {
        return organ;
    }

    public boolean hasMatch() {
        return bestRecipient != null;
    }

    public Recipient getBestRecipient() {
        return bestRecipient;
    }

    public RecipientEvaluation getBestEvaluation() {
        return bestEvaluation;
    }

    public List<RecipientEvaluation> getAllEvaluations() {
        return Collections.unmodifiableList(allEvaluations);
    }
}
