public class TransplantProcessFactory {

    public static TransplantProcess createProcess(String organType) {
        if (organType == null) {
            return new KidneyTransplantProcess();
        }
        switch (organType.trim().toLowerCase()) {
            case "heart":
                return new HeartTransplantProcess();
            case "liver":
                return new LiverTransplantProcess();
            case "lung":
                return new LungTransplantProcess();
            case "kidney":
            default:
                return new KidneyTransplantProcess();
        }
    }
}
