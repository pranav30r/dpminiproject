
public class RecipientState {

    private String status;

    public RecipientState(String status) {
        this.status = status;
    }

    public RecipientState(String patientId, String status) {
        this(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // Save the current state
    public RecipientMemento saveState() {
        return new RecipientMemento(status);
    }

    // Restore a previously saved state
    public void restoreState(RecipientMemento memento) {
        this.status = memento.getStatus();
    }
}
