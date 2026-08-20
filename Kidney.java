public class Kidney implements Organ {
    @Override
    public void displayInfo() {
        System.out.println("Kidney organ has been created.");
    }
    @Override
    public String getOrganType() {
        return "Kidney";
    }
}