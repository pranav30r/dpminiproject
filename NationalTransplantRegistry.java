import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NationalTransplantRegistry {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/organmatch_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "1406";

    private static final String URL_PROPERTY = "transplantiq.db.url";
    private static final String USER_PROPERTY = "transplantiq.db.user";
    private static final String PASSWORD_PROPERTY = "transplantiq.db.password";

    private static final String URL_ENV = "TRANSPLANTIQ_DB_URL";
    private static final String USER_ENV = "TRANSPLANTIQ_DB_USER";
    private static final String PASSWORD_ENV = "TRANSPLANTIQ_DB_PASSWORD";

    private static NationalTransplantRegistry instance;

    private final String url;
    private final String user;
    private final String password;
    private final Connection connection;

    private final List<DonorOrgan> registeredOrgans = new ArrayList<>();
    private final List<Recipient> registeredRecipients = new ArrayList<>();
    private final List<MatchResult> recordedMatches = new ArrayList<>();

    private NationalTransplantRegistry() {
        this.url = resolveSetting(URL_PROPERTY, URL_ENV, DEFAULT_URL);
        this.user = resolveSetting(USER_PROPERTY, USER_ENV, DEFAULT_USER);
        this.password = resolveSetting(PASSWORD_PROPERTY, PASSWORD_ENV, DEFAULT_PASSWORD);
        this.connection = openConnection();
    }

    public static synchronized NationalTransplantRegistry getInstance() {
        if (instance == null) {
            instance = new NationalTransplantRegistry();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isDatabaseConnected() {
        return connection != null;
    }

    public String getDatabaseUrl() {
        return url;
    }

    public synchronized boolean refreshFromDatabase() {
        if (connection == null) {
            return false;
        }

        try {
            Map<String, DonorOrgan> organById = loadOrgansFromDatabase();
            Map<String, Recipient> recipientById = loadRecipientsFromDatabase();
            List<MatchResult> matches = loadMatchesFromDatabase(organById, recipientById);

            registeredOrgans.clear();
            registeredOrgans.addAll(organById.values());

            registeredRecipients.clear();
            registeredRecipients.addAll(recipientById.values());

            recordedMatches.clear();
            recordedMatches.addAll(matches);
            return true;
        } catch (SQLException e) {
            System.out.println("Unable to load TransplantIQ data from MySQL. Falling back to in-memory mode.");
            System.out.println("Reason: " + e.getMessage());
            clearInMemoryData();
            return false;
        }
    }

    // Singleton Pattern - Jogi
    // Factory Method Pattern - Jogi
    public synchronized DonorOrgan registerOrgan(
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
        upsertOrganInMemory(donorOrgan);

        if (connection != null) {
            System.out.println(
                    "MySQL is connected, but persisting a new organ requires an existing donor_id."
            );
            System.out.println(
                    "Use registerOrganForExistingDonor(...) when you want this organ stored in the database."
            );
        }

        return donorOrgan;
    }

    public synchronized DonorOrgan registerOrganForExistingDonor(
            String donorId,
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
        upsertOrganInMemory(donorOrgan);

        if (connection != null) {
            String sql = """
                    INSERT INTO organs
                        (organ_id, organ_type, donor_id, blood_group, hla_profile, city, latitude, longitude, status)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        organ_type = VALUES(organ_type),
                        donor_id = VALUES(donor_id),
                        blood_group = VALUES(blood_group),
                        hla_profile = VALUES(hla_profile),
                        city = VALUES(city),
                        latitude = VALUES(latitude),
                        longitude = VALUES(longitude),
                        status = VALUES(status)
                    """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, organId);
                statement.setString(2, organ.getOrganType());
                statement.setString(3, donorId);
                statement.setString(4, bloodGroup);
                statement.setDouble(5, hlaProfile);
                statement.setString(6, location.getCityName());
                statement.setDouble(7, location.getLatitude());
                statement.setDouble(8, location.getLongitude());
                statement.setString(9, status);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Unable to save organ " + organId + " to MySQL.");
                System.out.println("Reason: " + e.getMessage());
            }
        }

        return donorOrgan;
    }

    public synchronized void registerRecipient(Recipient recipient) {
        upsertRecipientInMemory(recipient);

        if (connection != null) {
            String sql = """
                    INSERT INTO recipients
                        (patient_id, name, age, phone, address, blood_group, hla_profile, city, latitude, longitude, urgency, status)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        name = VALUES(name),
                        age = VALUES(age),
                        phone = VALUES(phone),
                        address = VALUES(address),
                        blood_group = VALUES(blood_group),
                        hla_profile = VALUES(hla_profile),
                        city = VALUES(city),
                        latitude = VALUES(latitude),
                        longitude = VALUES(longitude),
                        urgency = VALUES(urgency),
                        status = VALUES(status)
                    """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, recipient.getPatientId());
                statement.setString(2, recipient.getName());
                statement.setInt(3, recipient.getAge());
                statement.setString(4, recipient.getPhone());
                statement.setString(5, recipient.getAddress());
                statement.setString(6, recipient.getBloodGroup());
                statement.setDouble(7, recipient.getHlaProfile());
                statement.setString(8, recipient.getLocation().getCityName());
                statement.setDouble(9, recipient.getLocation().getLatitude());
                statement.setDouble(10, recipient.getLocation().getLongitude());
                statement.setString(11, recipient.getUrgency().name());
                statement.setString(12, recipient.getStatus());
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Unable to save recipient " + recipient.getPatientId() + " to MySQL.");
                System.out.println("Reason: " + e.getMessage());
            }
        }
    }

    public synchronized DonorOrgan findOrganById(String organId) {
        for (DonorOrgan organ : registeredOrgans) {
            if (organ.getOrganId().equalsIgnoreCase(organId)) {
                return organ;
            }
        }
        return null;
    }

    public synchronized List<DonorOrgan> getRegisteredOrgans() {
        return Collections.unmodifiableList(new ArrayList<>(registeredOrgans));
    }

    public synchronized List<DonorOrgan> getAvailableOrgans() {
        List<DonorOrgan> availableOrgans = new ArrayList<>();
        for (DonorOrgan organ : registeredOrgans) {
            if ("AVAILABLE".equalsIgnoreCase(organ.getStatus())) {
                availableOrgans.add(organ);
            }
        }
        return Collections.unmodifiableList(availableOrgans);
    }

    public synchronized List<Recipient> getRegisteredRecipients() {
        return Collections.unmodifiableList(new ArrayList<>(registeredRecipients));
    }

    public synchronized List<Recipient> getWaitingRecipients() {
        List<Recipient> waitingRecipients = new ArrayList<>();
        for (Recipient recipient : registeredRecipients) {
            if ("WAITING".equalsIgnoreCase(recipient.getStatus())) {
                waitingRecipients.add(recipient);
            }
        }
        return Collections.unmodifiableList(waitingRecipients);
    }

    public synchronized void recordMatch(MatchResult matchResult) {
        if (matchResult == null || !matchResult.hasMatch()) {
            return;
        }

        if (matchResult.getMatchId() == null) {
            persistMatch(matchResult);
        }

        if (connection != null) {
            refreshFromDatabase();
        } else {
            recordedMatches.add(matchResult);
        }
    }

    public synchronized void recordNotification(
            MatchResult matchResult,
            String observerType,
            String message
    ) {
        if (connection == null || matchResult == null || matchResult.getMatchId() == null) {
            return;
        }

        String sql = """
                INSERT INTO notifications (match_id, observer_type, message)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, matchResult.getMatchId());
            statement.setString(2, observerType);
            statement.setString(3, message);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Unable to save notification for match #" + matchResult.getMatchId() + ".");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    public synchronized List<MatchResult> getRecordedMatches() {
        return Collections.unmodifiableList(new ArrayList<>(recordedMatches));
    }

    public synchronized List<String> loadNotificationMessages() {
        if (connection == null) {
            return Collections.emptyList();
        }

        List<String> notifications = new ArrayList<>();
        String sql = """
                SELECT message
                FROM notifications
                ORDER BY created_at, notification_id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                notifications.add(resultSet.getString("message"));
            }
        } catch (SQLException e) {
            System.out.println("Unable to load notification log from MySQL.");
            System.out.println("Reason: " + e.getMessage());
        }

        return Collections.unmodifiableList(notifications);
    }

    public synchronized void clearInMemoryData() {
        registeredOrgans.clear();
        registeredRecipients.clear();
        recordedMatches.clear();
    }

    public synchronized void displayOrgans() {
        for (DonorOrgan organ : registeredOrgans) {
            System.out.println(
                    "ID: " + organ.getOrganId()
                            + ", Organ: " + organ.getOrganType()
                            + ", Status: " + organ.getStatus()
            );
        }
    }

    private Connection openConnection() {
        try {
            Connection dbConnection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL registry at " + url);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println("MySQL connection unavailable. Running in in-memory demo mode.");
            System.out.println("Reason: " + e.getMessage());
            System.out.println(
                    "Tip: add mysql-connector-j to the classpath and pass -D"
                            + PASSWORD_PROPERTY + "=your_mysql_password when running ChainDemo."
            );
            return null;
        }
    }

    private static String resolveSetting(String propertyName, String envName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue.trim();
        }

        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        return defaultValue;
    }

    private Map<String, DonorOrgan> loadOrgansFromDatabase() throws SQLException {
        Map<String, DonorOrgan> organById = new LinkedHashMap<>();
        String sql = """
                SELECT organ_id, organ_type, blood_group, hla_profile, city, latitude, longitude, status
                FROM organs
                ORDER BY organ_id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String organType = resultSet.getString("organ_type");
                DonorOrgan organ = new DonorOrgan(
                        resultSet.getString("organ_id"),
                        OrganFactory.createOrgan(organType),
                        resultSet.getString("blood_group"),
                        resultSet.getDouble("hla_profile"),
                        new Location(
                                resultSet.getString("city"),
                                resultSet.getDouble("latitude"),
                                resultSet.getDouble("longitude")
                        ),
                        resultSet.getString("status")
                );
                organById.put(organ.getOrganId(), organ);
            }
        }

        return organById;
    }

    private Map<String, Recipient> loadRecipientsFromDatabase() throws SQLException {
        Map<String, Recipient> recipientById = new LinkedHashMap<>();
        String sql = """
                SELECT patient_id, name, age, phone, address, blood_group, hla_profile,
                       city, latitude, longitude, urgency, status
                FROM recipients
                ORDER BY patient_id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Recipient recipient = new Recipient(
                        resultSet.getString("patient_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getString("phone"),
                        resultSet.getString("address"),
                        resultSet.getString("blood_group"),
                        resultSet.getDouble("hla_profile"),
                        new Location(
                                resultSet.getString("city"),
                                resultSet.getDouble("latitude"),
                                resultSet.getDouble("longitude")
                        ),
                        parseUrgency(resultSet.getString("urgency")),
                        resultSet.getString("status")
                );
                recipientById.put(recipient.getPatientId(), recipient);
            }
        }

        return recipientById;
    }

    private List<MatchResult> loadMatchesFromDatabase(
            Map<String, DonorOrgan> organById,
            Map<String, Recipient> recipientById
    ) throws SQLException {
        List<MatchResult> matches = new ArrayList<>();
        String sql = """
                SELECT match_id, organ_id, recipient_id, compatibility_score, hla_compatibility, distance_km
                FROM matches
                ORDER BY match_id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                DonorOrgan organ = organById.get(resultSet.getString("organ_id"));
                Recipient recipient = recipientById.get(resultSet.getString("recipient_id"));

                if (organ == null || recipient == null) {
                    continue;
                }

                RecipientEvaluation evaluation = new RecipientEvaluation(recipient);
                evaluation.setHlaCompatibility(resultSet.getDouble("hla_compatibility"));
                evaluation.setDistanceKm(resultSet.getDouble("distance_km"));
                evaluation.addScore(resultSet.getInt("compatibility_score"));
                evaluation.markEligible();
                evaluation.addDecision("Loaded from MySQL match history.");

                MatchResult matchResult = new MatchResult(
                        organ,
                        recipient,
                        evaluation,
                        Collections.singletonList(evaluation)
                );
                matchResult.setMatchId(resultSet.getInt("match_id"));
                matches.add(matchResult);
            }
        }

        return matches;
    }

    private void persistMatch(MatchResult matchResult) {
        if (connection == null) {
            return;
        }

        String sql = """
                INSERT INTO matches
                    (organ_id, recipient_id, compatibility_score, hla_compatibility, distance_km)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, matchResult.getOrgan().getOrganId());
            statement.setString(2, matchResult.getBestRecipient().getPatientId());
            statement.setInt(3, matchResult.getBestEvaluation().getCompatibilityScore());
            statement.setDouble(4, matchResult.getBestEvaluation().getHlaCompatibility());
            statement.setDouble(5, matchResult.getBestEvaluation().getDistanceKm());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    matchResult.setMatchId(generatedKeys.getInt(1));
                }
            }

            updateOrganStatus(matchResult.getOrgan().getOrganId(), "ALLOCATED");
            updateRecipientStatus(matchResult.getBestRecipient().getPatientId(), "MATCHED");
        } catch (SQLException e) {
            System.out.println("Unable to save confirmed match to MySQL.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    private void updateOrganStatus(String organId, String status) throws SQLException {
        String sql = "UPDATE organs SET status = ? WHERE organ_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, organId);
            statement.executeUpdate();
        }
    }

    private void updateRecipientStatus(String patientId, String status) throws SQLException {
        String sql = "UPDATE recipients SET status = ? WHERE patient_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, patientId);
            statement.executeUpdate();
        }
    }

    private UrgencyLevel parseUrgency(String rawUrgency) {
        if (rawUrgency == null || rawUrgency.trim().isEmpty()) {
            return UrgencyLevel.MEDIUM;
        }

        return UrgencyLevel.valueOf(rawUrgency.trim().toUpperCase(Locale.ROOT));
    }

    private void upsertOrganInMemory(DonorOrgan donorOrgan) {
        replaceExistingOrgan(donorOrgan.getOrganId());
        registeredOrgans.add(donorOrgan);
    }

    private void upsertRecipientInMemory(Recipient recipient) {
        replaceExistingRecipient(recipient.getPatientId());
        registeredRecipients.add(recipient);
    }

    private void replaceExistingOrgan(String organId) {
        registeredOrgans.removeIf(organ -> organ.getOrganId().equalsIgnoreCase(organId));
    }

    private void replaceExistingRecipient(String patientId) {
        registeredRecipients.removeIf(recipient -> recipient.getPatientId().equalsIgnoreCase(patientId));
    }
}
