/**
 * Standalone demonstration runner for the Mediator Pattern in TransplantIQ.
 * Demonstrates loose coupling: Transplant Center, Transport Logistics, Surgical Unit, and ICU
 * coordinate a complete organ transplant operation through a central TransplantCoordinator
 * without any direct references between individual teams.
 */
public class MediatorDemo {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("       TransplantIQ - Mediator Design Pattern Demonstration");
        System.out.println("======================================================================");

        // 1. Instantiate the Central Mediator (TransplantCoordinator)
        TransplantCoordinator coordinator = new TransplantCoordinator();
        System.out.println("[Init] Central Transplant Coordination Hub initialized.\n");

        // 2. Instantiate Colleagues (Teams)
        MedicalTeam transplantCenter = new MedicalTeam("Transplant Center");
        MedicalTeam surgicalUnit = new MedicalTeam("Surgical Unit");
        MedicalTeam transportLogistics = new MedicalTeam("Transport Logistics");
        MedicalTeam icuDepartment = new MedicalTeam("ICU Department");

        // 3. Register Colleagues with the Mediator
        coordinator.registerTeam(transplantCenter);
        coordinator.registerTeam(surgicalUnit);
        coordinator.registerTeam(transportLogistics);
        coordinator.registerTeam(icuDepartment);

        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("Workflow Simulation: Multi-Team Coordination via Mediator");
        System.out.println("----------------------------------------------------------------------");

        // Step 1: Transplant Center confirms organ match
        transplantCenter.triggerEvent(
                "ORGAN_MATCH_CONFIRMED",
                "Donor Kidney ORG-K101 allocated to Recipient REC-001 (Rahul Sharma)"
        );

        // Step 2: Transport logistics departs with organ
        transportLogistics.triggerEvent(
                "ORGAN_IN_TRANSIT",
                "Helicopter air transport en route from Nagpur. ETA: 40 mins"
        );

        // Step 3: Surgical team prepares the operating room
        surgicalUnit.triggerEvent(
                "OR_READY",
                "Operating Suite 2 ready; surgical team scrubbed in"
        );

        // Step 4: Transport arrives with organ
        transportLogistics.triggerEvent(
                "ORGAN_ARRIVED",
                "Cold-storage perfusion box delivered to Surgical Unit"
        );

        // Step 5: Surgery successfully completed
        surgicalUnit.triggerEvent(
                "TRANSPLANT_COMPLETED",
                "Kidney transplant procedure completed with positive organ perfusion"
        );

        System.out.println("\n======================================================================");
        System.out.println("Mediator Pattern Summary:");
        System.out.println("- No team references any other team directly (0% inter-team coupling).");
        System.out.println("- All event routing & coordination is centralized in TransplantCoordinator.");
        System.out.println("======================================================================");
    }
}
