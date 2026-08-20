public class OrganFactory {
    public static Organ createOrgan(String type) {
        if(type==null) {
            throw new IllegalArgumentException("Organ type cannot be null.");
        }
        switch(type.toLowerCase()) {
            case "kidney":
                return new Kidney();
            case "liver":
                return new Liver();
            case "heart":
                return new Heart();
            case "lung":
                return new Lung();
            default:
                throw new IllegalArgumentException("Invalid organ type: " + type);
        }
    }
}