public class Recipient implements PatientRecord {
    private final String patientId;
    private final String name;
    private final int age;
    private final String phone;
    private final String address;
    private final String bloodGroup;
    private final double hlaProfile;
    private final Location location;
    private final UrgencyLevel urgency;
    private final String status;

    public Recipient(
            String patientId,
            String name,
            int age,
            String phone,
            String address,
            String bloodGroup,
            double hlaProfile,
            Location location,
            UrgencyLevel urgency,
            String status
    ) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.hlaProfile = hlaProfile;
        this.location = location;
        this.urgency = urgency;
        this.status = status;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public double getHlaProfile() {
        return hlaProfile;
    }

    public Location getLocation() {
        return location;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public String getStatus() {
        return status;
    }
}
