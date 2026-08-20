public class Lung implements Organ {
    @Override
    public void displayInfo() {
        System.out.println("Lung organ has been created.");
    }
    @Override
    public String getOrganType() {
        return "Lung";
    }
}