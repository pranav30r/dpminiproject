
public class KidneyTransplantProcess extends TransplantProcess {

    @Override
    protected boolean isCompatibleRecipient(
            DonorOrgan organ, Recipient recipient) {

        System.out.println(
                "Checking recipient for kidney transplant..."
        );

        // Simple demonstration rule only
        return organ.getBloodGroup() != null
                && organ.getBloodGroup().equalsIgnoreCase(
                        recipient.getBloodGroup());
    }
}
