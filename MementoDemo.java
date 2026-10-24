
public class MementoDemo {

    public static void main(String[] args) {

        RecipientState recipient =
                new RecipientState("Waiting");

        System.out.println(
                "Original status: " + recipient.getStatus()
        );

        // Save the original state
        RecipientMemento savedState =
                recipient.saveState();

        // Change the status
        recipient.setStatus("Under Review");

        System.out.println(
                "Updated status: " + recipient.getStatus()
        );

        // Restore the saved state
        recipient.restoreState(savedState);

        System.out.println(
                "Restored status: " + recipient.getStatus()
        );
    }
}
