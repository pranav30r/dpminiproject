
import java.util.List;

public abstract class TransplantProcess {

    protected final NationalTransplantRegistry registry =
            NationalTransplantRegistry.getInstance();

    // Template Method: the fixed sequence of steps
    public final void processTransplant(DonorOrgan organ) {

        System.out.println("\n--- Transplant Process Started ---");

        if (!validateOrgan(organ)) {
            System.out.println("Organ is not available for processing.");
            return;
        }

        System.out.println("Step 1: Organ validated.");

        List<Recipient> recipients =
                registry.getRegisteredRecipients();

        System.out.println("Step 2: Recipients retrieved.");

        Recipient selectedRecipient = null;

        for (Recipient recipient : recipients) {

            if (isCompatibleRecipient(organ, recipient)) {
                selectedRecipient = recipient;
                break;
            }
        }

        System.out.println("Step 3: Compatibility check completed.");

        if (selectedRecipient == null) {
            System.out.println("No suitable recipient found.");
            return;
        }

        System.out.println("Step 4: Recipient selected.");
        System.out.println(
                "Selected Patient ID: "
                + selectedRecipient.getPatientId()
        );

        System.out.println("Organ Type: " + organ.getOrganType());
        System.out.println("Step 5: Processing completed.");
    }

    // Common validation logic
    private boolean validateOrgan(DonorOrgan organ) {
        return organ != null
                && "Available".equalsIgnoreCase(organ.getStatus());
    }

    // Subclasses provide their own compatibility check
    protected abstract boolean isCompatibleRecipient(
            DonorOrgan organ, Recipient recipient);
}
