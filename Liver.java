public class Liver implements Organ {
    @Override
    public void displayInfo() {
        System.out.println("Liver organ has been created.");
    }
    @Override
    public String getOrganType() {
        return "Liver";
    }
}