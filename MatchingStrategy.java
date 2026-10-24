import java.util.List;

/**
 * Strategy interface for organ-recipient matching algorithms in TransplantIQ.
 * Allows different matching criteria (e.g. urgency, distance/proximity) to be
 * interchanged dynamically at runtime.
 */
public interface MatchingStrategy {

    /**
     * Returns the human-readable name of the strategy.
     */
    String getStrategyName();

    /**
     * Matches and selects the most optimal recipient for the given donor organ
     * from a list of candidate recipients according to this strategy's algorithm.
     *
     * @param organ      the available donor organ
     * @param recipients list of registered candidate recipients
     * @return the selected best-match Recipient, or null if no compatible recipient is found
     */
    Recipient match(DonorOrgan organ, List<Recipient> recipients);
}
