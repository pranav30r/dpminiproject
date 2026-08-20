import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipientEvaluation {
    private final Recipient recipient;
    private final List<String> decisionTrail = new ArrayList<>();
    private boolean eligible = true;
    private String rejectionReason = "Not evaluated";
    private double hlaCompatibility;
    private double distanceKm;
    private int compatibilityScore;

    public RecipientEvaluation(Recipient recipient) {
        this.recipient = recipient;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public boolean isEligible() {
        return eligible;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public double getHlaCompatibility() {
        return hlaCompatibility;
    }

    public void setHlaCompatibility(double hlaCompatibility) {
        this.hlaCompatibility = hlaCompatibility;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getCompatibilityScore() {
        return compatibilityScore;
    }

    public void addScore(int scoreContribution) {
        compatibilityScore += scoreContribution;
    }

    public void addDecision(String decision) {
        decisionTrail.add(decision);
    }

    public void markEligible() {
        eligible = true;
        rejectionReason = "Qualified";
    }

    public void reject(String reason) {
        eligible = false;
        rejectionReason = reason;
        decisionTrail.add("Rejected: " + reason);
    }

    public List<String> getDecisionTrail() {
        return Collections.unmodifiableList(decisionTrail);
    }
}
