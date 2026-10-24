// Observer Pattern - Rathi
public class TransplantCenterObserver implements MatchObserver {
    @Override
    public void onMatchConfirmed(MatchResult match) {
        String msg = "[Transplant Center] Match Confirmed: " + match.getOrgan().getOrganType().toUpperCase() 
            + " (" + match.getOrgan().getOrganId() + ") allocated to Patient " + match.getBestRecipient().getPatientId() 
            + ". Compatibility Score: " + match.getBestEvaluation().getCompatibilityScore() + "%.";
        
        System.out.println(msg);
        AllocationService.getInstance().logNotification("TransplantCenter", match, msg);
    }
}
