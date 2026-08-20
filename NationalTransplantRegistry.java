import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NationalTransplantRegistry {
    // ==== Update these to match your teammate's actual DB config ====
    private static final String URL = "jdbc:mysql://localhost:3306/organmatch_db";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";
    // ===================================================================

    private static NationalTransplantRegistry instance;
    private Connection connection;
    private final List<DonorOrgan> registeredOrgans = new ArrayList<>();
    private final List<Recipient> registeredRecipients = new ArrayList<>();
    private final List<MatchResult> recordedMatches = new ArrayList<>();

    // Private constructor - creates its own connection internally,
    // so the Singleton fully owns/controls its state (no external
    // parameter can be silently ignored on later calls).
    private NationalTransplantRegistry() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch(SQLException e) {
            this.connection = null;
            System.out.println("Database connection unavailable. Running registry in in-memory demo mode.");
        }
    }

    // No-argument getInstance(): every caller gets the same instance,
    // and there's no parameter that could be mistakenly ignored.
    public static synchronized NationalTransplantRegistry getInstance() {
        if(instance==null) {
            instance=new NationalTransplantRegistry();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // Singleton Pattern - Jogi
    // Factory Method Pattern - Jogi
    public DonorOrgan registerOrgan(
            String organId,
            String organType,
            String bloodGroup,
            double hlaProfile,
            Location location,
            String status
    ) {
        Organ organ = OrganFactory.createOrgan(organType);
        DonorOrgan donorOrgan = new DonorOrgan(
                organId,
                organ,
                bloodGroup,
                hlaProfile,
                location,
                status
        );
        registeredOrgans.add(donorOrgan);

        if (connection != null) {
            addOrgan(organ.getOrganType());
        }

        return donorOrgan;
    }

    public void registerRecipient(Recipient recipient) {
        registeredRecipients.add(recipient);
    }

    public DonorOrgan findOrganById(String organId) {
        for (DonorOrgan organ : registeredOrgans) {
            if (organ.getOrganId().equalsIgnoreCase(organId)) {
                return organ;
            }
        }
        return null;
    }

    public List<DonorOrgan> getRegisteredOrgans() {
        return Collections.unmodifiableList(registeredOrgans);
    }

    public List<Recipient> getRegisteredRecipients() {
        return Collections.unmodifiableList(registeredRecipients);
    }

    public void recordMatch(MatchResult matchResult) {
        recordedMatches.add(matchResult);
    }

    public List<MatchResult> getRecordedMatches() {
        return Collections.unmodifiableList(recordedMatches);
    }

    public void clearInMemoryData() {
        registeredOrgans.clear();
        registeredRecipients.clear();
        recordedMatches.clear();
    }

    public void addOrgan(String organType) {
        if (connection == null) {
            return;
        }

        String sql="INSERT INTO organs (organ_type) VALUES (?)";
        try(PreparedStatement statement=connection.prepareStatement(sql)) {
            statement.setString(1, organType);
            statement.executeUpdate();
            System.out.println(organType + " added to registry.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayOrgans() {
        if (connection == null) {
            for (DonorOrgan organ : registeredOrgans) {
                System.out.println("ID: " + organ.getOrganId() + ", Organ: "
                        + organ.getOrganType());
            }
            return;
        }

        String sql="SELECT * FROM organs";
        try(PreparedStatement statement=connection.prepareStatement(sql);
             ResultSet resultSet=statement.executeQuery()) {
            while(resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("organ_id") + ", Organ: "
                        + resultSet.getString("organ_type"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
