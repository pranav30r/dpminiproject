import java.util.List;

/**
 * Concrete Strategy: Urgency Priority Matching.
 * Selects the recipient with the highest medical urgency level (CRITICAL > HIGH > MEDIUM > LOW)
 * among all blood-compatible active recipients.
 */
public class UrgencyMatchingStrategy implements MatchingStrategy {

    @Override
    public String getStrategyName() {
        return "Urgency Priority Matching Strategy";
    }

    @Override
    public Recipient match(DonorOrgan organ, List<Recipient> recipients) {
        if (organ == null || recipients == null || recipients.isEmpty()) {
            return null;
        }

        Recipient bestMatch = null;
        int highestUrgencyScore = -1;

        for (Recipient recipient : recipients) {
            // Check status
            if (!"Waiting".equalsIgnoreCase(recipient.getStatus()) && !"Active".equalsIgnoreCase(recipient.getStatus())) {
                continue;
            }

            // Check blood group compatibility
            if (!isBloodCompatible(organ.getBloodGroup(), recipient.getBloodGroup())) {
                continue;
            }

            int score = (recipient.getUrgency() != null)
                    ? recipient.getUrgency().getScoreContribution()
                    : 0;

            if (score > highestUrgencyScore) {
                highestUrgencyScore = score;
                bestMatch = recipient;
            }
        }

        return bestMatch;
    }

    private boolean isBloodCompatible(String donor, String recipient) {
        if (donor == null || recipient == null) return false;
        donor = donor.trim().toUpperCase();
        recipient = recipient.trim().toUpperCase();

        if (donor.equals(recipient)) return true;
        if (donor.equals("O-")) return true;
        if (donor.equals("O+")) return recipient.endsWith("+");
        if (donor.equals("A-")) return recipient.contains("A");
        if (donor.equals("A+")) return recipient.equals("A+") || recipient.equals("AB+");
        if (donor.equals("B-")) return recipient.contains("B");
        if (donor.equals("B+")) return recipient.equals("B+") || recipient.equals("AB+");
        return donor.equals(recipient);
    }
}
