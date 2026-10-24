import java.util.List;

/**
 * Concrete Strategy: Distance / Proximity Priority Matching.
 * Selects the recipient located closest to the donor organ's hospital location to
 * minimize cold ischemia time (organ preservation window during transit).
 */
public class DistanceMatchingStrategy implements MatchingStrategy {

    @Override
    public String getStrategyName() {
        return "Distance / Proximity Priority Matching Strategy";
    }

    @Override
    public Recipient match(DonorOrgan organ, List<Recipient> recipients) {
        if (organ == null || recipients == null || recipients.isEmpty() || organ.getLocation() == null) {
            return null;
        }

        Recipient closestRecipient = null;
        double minDistanceKm = Double.MAX_VALUE;

        for (Recipient recipient : recipients) {
            // Check status
            if (!"Waiting".equalsIgnoreCase(recipient.getStatus()) && !"Active".equalsIgnoreCase(recipient.getStatus())) {
                continue;
            }

            // Check blood group compatibility
            if (!isBloodCompatible(organ.getBloodGroup(), recipient.getBloodGroup())) {
                continue;
            }

            if (recipient.getLocation() == null) {
                continue;
            }

            double distance = organ.getLocation().distanceTo(recipient.getLocation());
            if (distance < minDistanceKm) {
                minDistanceKm = distance;
                closestRecipient = recipient;
            }
        }

        return closestRecipient;
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
