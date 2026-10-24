/**
 * Mediator interface for coordinating communication and actions between various
 * transplant teams (Transplant Center, Surgical Unit, Logistics/Transport, ICU)
 * without direct coupling between them.
 */
public interface TransplantMediator {

    /**
     * Registers a medical team colleague with the mediator.
     *
     * @param team the medical team to register
     */
    void registerTeam(MedicalTeam team);

    /**
     * Notifies the mediator of an event triggered by a colleague team.
     * The mediator coordinates which other teams need to take action.
     *
     * @param sender  the team that triggered the event
     * @param event   the event name / type
     * @param details descriptive context or operational instructions
     */
    void notify(MedicalTeam sender, String event, String details);
}
