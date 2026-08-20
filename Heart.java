public class Heart implements Organ {
    @Override
    public void displayInfo() {
        System.out.println("Heart organ has been created.");
    }
    @Override
    public String getOrganType() {
        return "Heart";
    }
}