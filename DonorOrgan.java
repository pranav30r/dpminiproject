public class DonorOrgan {
    private final String organId;
    private final Organ organ;
    private final String bloodGroup;
    private final double hlaProfile;
    private final Location location;
    private final String status;

    public DonorOrgan(
            String organId,
            Organ organ,
            String bloodGroup,
            double hlaProfile,
            Location location,
            String status
    ) {
        this.organId = organId;
        this.organ = organ;
        this.bloodGroup = bloodGroup;
        this.hlaProfile = hlaProfile;
        this.location = location;
        this.status = status;
    }

    public String getOrganId() {
        return organId;
    }

    public Organ getOrgan() {
        return organ;
    }

    public String getOrganType() {
        return organ.getOrganType();
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

    public String getStatus() {
        return status;
    }
}
