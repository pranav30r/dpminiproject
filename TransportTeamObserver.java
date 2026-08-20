// Observer Pattern - Rathi
public class TransportTeamObserver implements MatchObserver {
    @Override
    public void onMatchConfirmed(MatchResult match) {
        String msg = "[Transport Team] Logistics Alert: Coordinate transport of " + match.getOrgan().getOrganType().toUpperCase() 
            + " (" + match.getOrgan().getOrganId() + ") from Donor location (" + match.getOrgan().getLocation().getCityName() 
            + ") to Patient location (" + match.getBestRecipient().getLocation().getCityName() + ").";
        
        System.out.println(msg);
        AllocationService.getInstance().logNotification(msg);
    }
}
