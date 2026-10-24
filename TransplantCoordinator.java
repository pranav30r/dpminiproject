import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Mediator: Central Coordinator for Organ Transplant Operations.
 * Encapsulates the interaction logic among Hospital, Surgical Unit, Transport, and ICU.
 */
public class TransplantCoordinator implements TransplantMediator {

    private final Map<String, MedicalTeam> teams = new HashMap<>();

    @Override
    public void registerTeam(MedicalTeam team) {
        if (team != null) {
            teams.put(team.getTeamName().toLowerCase(), team);
            team.setMediator(this);
            System.out.println("[Coordinator] Registered team: " + team.getTeamName());
        }
    }

    public MedicalTeam getTeam(String teamName) {
        return teams.get(teamName.toLowerCase());
    }

    @Override
    public void notify(MedicalTeam sender, String event, String details) {
        System.out.println(">>> [Coordinator Processing Event] '" + event + "' from " + sender.getTeamName());
        System.out.println("    Details: " + details);

        switch (event.toUpperCase()) {
            case "ORGAN_MATCH_CONFIRMED":
                // Notify Logistics to dispatch transport and Surgery to prep OR
                relayTo("Transport Logistics", "Dispatch emergency organ transport for retrieval.", sender);
                relayTo("Surgical Unit", "Recipient identified. Prepare operating room and surgical staff.", sender);
                relayTo("ICU Department", "Move recipient patient to pre-op holding area.", sender);
                break;

            case "ORGAN_IN_TRANSIT":
                // Notify Surgery of live transit status and ETA
                relayTo("Surgical Unit", "Organ in transit: " + details + ". Begin anesthesia preparation.", sender);
                relayTo("Transplant Center", "Update national registry: organ transit underway.", sender);
                break;

            case "OR_READY":
                // Surgical unit is ready
                relayTo("Transplant Center", "Operating theatre sterilized and team standing by.", sender);
                break;

            case "ORGAN_ARRIVED":
                // Organ arrived at receiving hospital
                relayTo("Surgical Unit", "Organ safely received at facility. Immediate transfer to OR.", sender);
                relayTo("Transport Logistics", "Delivery confirmed. Transport mission completed.", sender);
                break;

            case "TRANSPLANT_COMPLETED":
                // Surgery finished successfully
                relayTo("Transplant Center", "Transplant procedure successful. Update recipient status to 'Post-Op'.", sender);
                relayTo("ICU Department", "Prepare intensive care recovery bed for post-transplant monitoring.", sender);
                break;

            default:
                // Broadcast to all other registered teams
                for (MedicalTeam team : teams.values()) {
                    if (team != sender) {
                        team.receive(details, sender.getTeamName());
                    }
                }
                break;
        }
    }

    private void relayTo(String targetTeamName, String message, MedicalTeam sender) {
        MedicalTeam target = teams.get(targetTeamName.toLowerCase());
        if (target != null) {
            target.receive(message, sender.getTeamName());
        }
    }
}
