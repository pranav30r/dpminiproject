import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

// Chain of Responsibility Pattern - Hiten
public class BloodTypeMatcher extends AbstractCompatibilityHandler {
    private final MatchingConfig config;

    public BloodTypeMatcher(MatchingConfig config) {
        this.config = config;
    }

    @Override
    public RecipientEvaluation handle(
            DonorOrgan organ,
            Recipient recipient,
            RecipientEvaluation evaluation
    ) {
        String donorBloodGroup = organ.getBloodGroup().toUpperCase(Locale.ROOT);
        String recipientBloodGroup = recipient.getBloodGroup().toUpperCase(Locale.ROOT);

        if (!isCompatible(donorBloodGroup, recipientBloodGroup)) {
            evaluation.reject("Blood group mismatch");
            return evaluation;
        }

        int score = donorBloodGroup.equals(recipientBloodGroup)
                ? config.getExactBloodMatchScore()
                : config.getCompatibleBloodMatchScore();

        evaluation.addScore(score);
        evaluation.addDecision(
                "BloodTypeMatcher passed: donor " + donorBloodGroup
                        + " -> recipient " + recipientBloodGroup
                        + ", score +" + score
        );
        evaluation.markEligible();
        return continueChain(organ, recipient, evaluation);
    }

    private boolean isCompatible(String donorBloodGroup, String recipientBloodGroup) {
        switch (donorBloodGroup) {
            case "O-":
                return true;
            case "O+":
                return inSet(recipientBloodGroup, "O+", "A+", "B+", "AB+");
            case "A-":
                return inSet(recipientBloodGroup, "A-", "A+", "AB-", "AB+");
            case "A+":
                return inSet(recipientBloodGroup, "A+", "AB+");
            case "B-":
                return inSet(recipientBloodGroup, "B-", "B+", "AB-", "AB+");
            case "B+":
                return inSet(recipientBloodGroup, "B+", "AB+");
            case "AB-":
                return inSet(recipientBloodGroup, "AB-", "AB+");
            case "AB+":
                return "AB+".equals(recipientBloodGroup);
            default:
                return false;
        }
    }

    private boolean inSet(String value, String... values) {
        Set<String> allowedValues = new HashSet<>(Arrays.asList(values));
        return allowedValues.contains(value);
    }
}
