/**
 * Colleague class in the Mediator Pattern.
 * Represents a medical or operational unit participating in an organ transplant.
 * Holds a reference to the TransplantMediator and communicates exclusively through it.
 */
public class MedicalTeam {

    protected String teamName;
    protected TransplantMediator mediator;

    public MedicalTeam(String teamName) {
        this.teamName = teamName;
    }

    public MedicalTeam(String teamName, TransplantMediator mediator) {
        this.teamName = teamName;
        this.mediator = mediator;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setMediator(TransplantMediator mediator) {
        this.mediator = mediator;
    }

    /**
     * Triggers an event through the mediator.
     * The sender does not need direct references to any receiving teams.
     *
     * @param event   the event identifier
     * @param details message or instructions
     */
    public void triggerEvent(String event, String details) {
        if (mediator == null) {
            System.err.println("[" + teamName + "] Cannot trigger event: Mediator is not set.");
            return;
        }
        System.out.println("\n[" + teamName + "] Triggered Event -> " + event);
        mediator.notify(this, event, details);
    }

    /**
     * Invoked by the mediator when an alert or instruction is routed to this team.
     *
     * @param message  instruction or message content
     * @param fromTeam original source team identity
     */
    public void receive(String message, String fromTeam) {
        System.out.println("  [Alert -> " + teamName + "] (via Coordinator from " + fromTeam + "): " + message);
    }
}
