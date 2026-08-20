// Proxy Pattern - Chetan
public class PrivacyProxy implements PatientRecord {
    private final PatientRecord realRecord;
    private final boolean authorized;

    public PrivacyProxy(PatientRecord realRecord) {
        this(realRecord, false);
    }

    public PrivacyProxy(PatientRecord realRecord, boolean authorized) {
        this.realRecord = realRecord;
        this.authorized = authorized;
    }

    @Override
    public String getPatientId() {
        return realRecord.getPatientId();
    }

    @Override
    public String getName() {
        if (authorized) {
            return realRecord.getName();
        }
        return "REDACTED (PII Protected)";
    }

    @Override
    public int getAge() {
        if (authorized) {
            return realRecord.getAge();
        }
        return -1; // -1 represents redacted age
    }

    @Override
    public String getPhone() {
        if (authorized) {
            return realRecord.getPhone();
        }
        return "REDACTED (PII Protected)";
    }

    @Override
    public String getAddress() {
        if (authorized) {
            return realRecord.getAddress();
        }
        return "REDACTED (PII Protected)";
    }

    @Override
    public String getBloodGroup() {
        return realRecord.getBloodGroup();
    }

    @Override
    public double getHlaProfile() {
        return realRecord.getHlaProfile();
    }

    @Override
    public Location getLocation() {
        return realRecord.getLocation();
    }

    @Override
    public UrgencyLevel getUrgency() {
        return realRecord.getUrgency();
    }

    @Override
    public String getStatus() {
        return realRecord.getStatus();
    }
}
