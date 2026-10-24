// Observer Pattern - Rathi
public class DoctorObserver implements MatchObserver {
    @Override
    public void onMatchConfirmed(MatchResult match) {
        String msg = "[Doctor/Medical Team] Surgery Alert: Prepare surgical unit for Patient " 
            + match.getBestRecipient().getPatientId() + ". Organ type: " + match.getOrgan().getOrganType().toUpperCase() 
            + ". Urgency Level: " + match.getBestRecipient().getUrgency() + ".";
        
        System.out.println(msg);
        AllocationService.getInstance().logNotification("Doctor", match, msg);
    }
}
