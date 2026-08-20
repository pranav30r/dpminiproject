-- ============================================================
--  TransplantIQ — Complete Database Setup Script
--  Database : organmatch_db
--  Engine   : MySQL 8.x
--
--  HOW TO RUN:
--    1. Open MySQL Workbench (or any MySQL client)
--    2. Run this entire file once:
--         source /path/to/organmatch_db.sql;
--       OR paste and execute in the Query editor.
--
--  JAVA CONNECTION (in NationalTransplantRegistry.java):
--    URL      = "jdbc:mysql://localhost:3306/organmatch_db"
--    USER     = "root"
--    PASSWORD = "your_mysql_password"   ← change this
--
--  JDBC Driver (add to classpath):
--    Download: https://dev.mysql.com/downloads/connector/j/
--    JAR name: mysql-connector-j-8.x.x.jar
--    Compile : javac -cp ".;mysql-connector-j-8.x.x.jar" *.java
--    Run     : java  -cp ".;mysql-connector-j-8.x.x.jar" ChainDemo
-- ============================================================


-- ─────────────────────────────────────────────
--  0. CREATE & SELECT DATABASE
-- ─────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS organmatch_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE organmatch_db;


-- ─────────────────────────────────────────────
--  DROP TABLES (safe re-run)
-- ─────────────────────────────────────────────
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS transplant_centers;
DROP TABLE IF EXISTS organs;
DROP TABLE IF EXISTS recipients;
DROP TABLE IF EXISTS donors;

SET FOREIGN_KEY_CHECKS = 1;


-- ─────────────────────────────────────────────
--  TABLE 1 : donors
-- ─────────────────────────────────────────────
CREATE TABLE donors (
    donor_id    VARCHAR(10)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    age         INT          NOT NULL,
    blood_group VARCHAR(5)   NOT NULL,
    city        VARCHAR(50)  NOT NULL,
    phone       VARCHAR(20)  NOT NULL,
    PRIMARY KEY (donor_id)
);


-- ─────────────────────────────────────────────
--  TABLE 2 : organs
-- ─────────────────────────────────────────────
CREATE TABLE organs (
    organ_id    VARCHAR(10)  NOT NULL,
    organ_type  VARCHAR(20)  NOT NULL,
    donor_id    VARCHAR(10)  NOT NULL,
    blood_group VARCHAR(5)   NOT NULL,
    hla_profile DOUBLE       NOT NULL,
    city        VARCHAR(50)  NOT NULL,
    latitude    DOUBLE       NOT NULL,
    longitude   DOUBLE       NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE',
    PRIMARY KEY (organ_id),
    CONSTRAINT fk_organ_donor FOREIGN KEY (donor_id) REFERENCES donors (donor_id)
);


-- ─────────────────────────────────────────────
--  TABLE 3 : recipients
-- ─────────────────────────────────────────────
CREATE TABLE recipients (
    patient_id  VARCHAR(10)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    age         INT          NOT NULL,
    phone       VARCHAR(20)  NOT NULL,
    address     VARCHAR(200) NOT NULL,
    blood_group VARCHAR(5)   NOT NULL,
    hla_profile DOUBLE       NOT NULL,
    city        VARCHAR(50)  NOT NULL,
    latitude    DOUBLE       NOT NULL,
    longitude   DOUBLE       NOT NULL,
    urgency     VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    status      VARCHAR(20)  NOT NULL DEFAULT 'WAITING',
    PRIMARY KEY (patient_id)
);


-- ─────────────────────────────────────────────
--  TABLE 4 : transplant_centers
-- ─────────────────────────────────────────────
CREATE TABLE transplant_centers (
    center_id      INT          NOT NULL AUTO_INCREMENT,
    center_name    VARCHAR(100) NOT NULL,
    city           VARCHAR(50)  NOT NULL,
    contact_number VARCHAR(20)  NOT NULL,
    PRIMARY KEY (center_id)
);


-- ─────────────────────────────────────────────
--  TABLE 5 : matches
-- ─────────────────────────────────────────────
CREATE TABLE matches (
    match_id            INT         NOT NULL AUTO_INCREMENT,
    organ_id            VARCHAR(10) NOT NULL,
    recipient_id        VARCHAR(10) NOT NULL,
    compatibility_score INT         NOT NULL,
    hla_compatibility   DOUBLE      NOT NULL,
    distance_km         DOUBLE      NOT NULL,
    match_date          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (match_id),
    CONSTRAINT fk_match_organ     FOREIGN KEY (organ_id)     REFERENCES organs     (organ_id),
    CONSTRAINT fk_match_recipient FOREIGN KEY (recipient_id) REFERENCES recipients (patient_id)
);


-- ─────────────────────────────────────────────
--  TABLE 6 : notifications
-- ─────────────────────────────────────────────
CREATE TABLE notifications (
    notification_id INT         NOT NULL AUTO_INCREMENT,
    match_id        INT         NOT NULL,
    observer_type   VARCHAR(50) NOT NULL,
    message         TEXT        NOT NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (notification_id),
    CONSTRAINT fk_notif_match FOREIGN KEY (match_id) REFERENCES matches (match_id)
);


-- ═══════════════════════════════════════════════════
--  SAMPLE DATA
-- ═══════════════════════════════════════════════════


-- ─────────────────────────────────────────────
--  DATA : donors  (8 donors across Maharashtra)
-- ─────────────────────────────────────────────
INSERT INTO donors (donor_id, name, age, blood_group, city, phone) VALUES
('D001', 'Arjun Kadam',    34, 'O+',  'Nagpur',  '9823001001'),
('D002', 'Priya Joshi',    29, 'A-',  'Mumbai',  '9823001002'),
('D003', 'Sanjay Rane',    45, 'B+',  'Pune',    '9823001003'),
('D004', 'Meera Kulkarni', 38, 'AB+', 'Nashik',  '9823001004'),
('D005', 'Rohit Patil',    52, 'O-',  'Aurangabad','9823001005'),
('D006', 'Kavita Desai',   41, 'A+',  'Thane',   '9823001006'),
('D007', 'Suresh Nair',    36, 'B-',  'Nagpur',  '9823001007'),
('D008', 'Anjali More',    27, 'O+',  'Mumbai',  '9823001008');


-- ─────────────────────────────────────────────
--  DATA : organs  (10 organs — mix of types)
-- ─────────────────────────────────────────────
INSERT INTO organs (organ_id, organ_type, donor_id, blood_group, hla_profile, city, latitude, longitude, status) VALUES
('K101', 'Kidney', 'D001', 'O+',  90.0, 'Nagpur',     21.1458, 79.0882, 'ALLOCATED'),
('H201', 'Heart',  'D002', 'A-',  85.0, 'Mumbai',     19.0760, 72.8777, 'ALLOCATED'),
('L301', 'Liver',  'D003', 'B+',  78.0, 'Pune',       18.5204, 73.8567, 'AVAILABLE'),
('LU401','Lung',   'D004', 'AB+', 82.0, 'Nashik',     19.9975, 73.7898, 'AVAILABLE'),
('K102', 'Kidney', 'D005', 'O-',  88.0, 'Aurangabad', 19.8762, 75.3433, 'AVAILABLE'),
('H202', 'Heart',  'D006', 'A+',  91.0, 'Thane',      19.2183, 72.9781, 'AVAILABLE'),
('L302', 'Liver',  'D007', 'B-',  76.0, 'Nagpur',     21.1458, 79.0882, 'AVAILABLE'),
('LU402','Lung',   'D008', 'O+',  87.0, 'Mumbai',     19.0760, 72.8777, 'AVAILABLE'),
('K103', 'Kidney', 'D001', 'O+',  93.0, 'Nagpur',     21.1458, 79.0882, 'AVAILABLE'),
('H203', 'Heart',  'D002', 'A-',  80.0, 'Mumbai',     19.0760, 72.8777, 'AVAILABLE');


-- ─────────────────────────────────────────────
--  DATA : recipients  (10 patients)
-- ─────────────────────────────────────────────
INSERT INTO recipients (patient_id, name, age, phone, address, blood_group, hla_profile, city, latitude, longitude, urgency, status) VALUES
('P101', 'Aarav Mehta',   44, '9800000101', '12 MG Road, Nagpur',         'A+',  84.0, 'Nagpur',     21.1498, 79.0806, 'HIGH',     'MATCHED'),
('P102', 'Rahul Sharma',  42, '9800000102', '45 Sadar, Nagpur',           'O+',  92.0, 'Nagpur',     21.1570, 79.0720, 'CRITICAL', 'MATCHED'),
('P103', 'Neha Verma',    39, '9800000103', '7 FC Road, Pune',            'O+',  55.0, 'Pune',       18.5204, 73.8567, 'HIGH',     'WAITING'),
('P104', 'Kunal Joshi',   50, '9800000104', '22 Linking Rd, Mumbai',      'AB+', 88.0, 'Mumbai',     19.0760, 72.8777, 'MEDIUM',   'WAITING'),
('P105', 'Sneha Patil',   35, '9800000105', '9 Hill Road, Thane',         'A-',  87.0, 'Thane',      19.2183, 72.9781, 'CRITICAL', 'MATCHED'),
('P106', 'Vikram Desai',  48, '9800000106', '3 Station Rd, Nashik',       'A+',  78.0, 'Nashik',     19.9975, 73.7898, 'LOW',      'WAITING'),
('P107', 'Pooja Iyer',    31, '9800000107', '5 Baner Rd, Pune',           'B+',  80.0, 'Pune',       18.5600, 73.7800, 'HIGH',     'WAITING'),
('P108', 'Amit Gupta',    55, '9800000108', '8 Dharampeth, Nagpur',       'O-',  91.0, 'Nagpur',     21.1300, 79.0800, 'CRITICAL', 'WAITING'),
('P109', 'Riya Shah',     28, '9800000109', '14 Colaba, Mumbai',          'A-',  83.0, 'Mumbai',     18.9220, 72.8320, 'MEDIUM',   'WAITING'),
('P110', 'Manish Tiwari', 46, '9800000110', '20 Cidco Colony, Aurangabad','B-',  77.0, 'Aurangabad', 19.8762, 75.3433, 'HIGH',     'WAITING');


-- ─────────────────────────────────────────────
--  DATA : transplant_centers  (5 centers)
-- ─────────────────────────────────────────────
INSERT INTO transplant_centers (center_name, city, contact_number) VALUES
('AIIMS Nagpur Transplant Unit',        'Nagpur',     '0712-2701001'),
('KEM Hospital Organ Centre',           'Mumbai',     '022-24107000'),
('Ruby Hall Transplant Division',       'Pune',       '020-66455100'),
('Wockhardt Hospital - Transplant Dept','Nashik',     '0253-6633500'),
('Bethany Hospital Organ Matching Unit','Aurangabad', '0240-6645000');


-- ─────────────────────────────────────────────
--  DATA : matches  (3 confirmed matches)
-- ─────────────────────────────────────────────
INSERT INTO matches (organ_id, recipient_id, compatibility_score, hla_compatibility, distance_km, match_date) VALUES
('K101', 'P102', 99, 98.0,  2.1,  '2026-08-20 09:15:00'),
('H201', 'P105', 96, 98.0,  19.0, '2026-08-20 09:18:00'),
('H202', 'P101', 83, 94.0,  0.9,  '2026-08-20 10:05:00');


-- ─────────────────────────────────────────────
--  DATA : notifications  (3 observers × 3 matches = 9 rows)
-- ─────────────────────────────────────────────
INSERT INTO notifications (match_id, observer_type, message, created_at) VALUES
-- Match 1 : K101 → P102
(1, 'TransplantCenter',
 '[Transplant Center] Match Confirmed: KIDNEY (K101) allocated to Patient P102. Compatibility Score: 99%.',
 '2026-08-20 09:15:01'),

(1, 'Doctor',
 '[Doctor/Medical Team] Surgery Alert: Prepare surgical unit for Patient P102. Organ type: KIDNEY. Urgency Level: CRITICAL.',
 '2026-08-20 09:15:01'),

(1, 'TransportTeam',
 '[Transport Team] Logistics Alert: Coordinate transport of KIDNEY (K101) from Donor location (Nagpur) to Patient location (Nagpur).',
 '2026-08-20 09:15:01'),

-- Match 2 : H201 → P105
(2, 'TransplantCenter',
 '[Transplant Center] Match Confirmed: HEART (H201) allocated to Patient P105. Compatibility Score: 96%.',
 '2026-08-20 09:18:01'),

(2, 'Doctor',
 '[Doctor/Medical Team] Surgery Alert: Prepare surgical unit for Patient P105. Organ type: HEART. Urgency Level: CRITICAL.',
 '2026-08-20 09:18:01'),

(2, 'TransportTeam',
 '[Transport Team] Logistics Alert: Coordinate transport of HEART (H201) from Donor location (Mumbai) to Patient location (Thane).',
 '2026-08-20 09:18:01'),

-- Match 3 : H202 → P101
(3, 'TransplantCenter',
 '[Transplant Center] Match Confirmed: HEART (H202) allocated to Patient P101. Compatibility Score: 83%.',
 '2026-08-20 10:05:01'),

(3, 'Doctor',
 '[Doctor/Medical Team] Surgery Alert: Prepare surgical unit for Patient P101. Organ type: HEART. Urgency Level: HIGH.',
 '2026-08-20 10:05:01'),

(3, 'TransportTeam',
 '[Transport Team] Logistics Alert: Coordinate transport of HEART (H202) from Donor location (Thane) to Patient location (Nagpur).',
 '2026-08-20 10:05:01');


-- ═══════════════════════════════════════════════════
--  VERIFICATION QUERIES  (optional — run to confirm)
-- ═══════════════════════════════════════════════════
-- SELECT 'donors'            AS tbl, COUNT(*) AS rows FROM donors
-- UNION ALL
-- SELECT 'organs',           COUNT(*) FROM organs
-- UNION ALL
-- SELECT 'recipients',       COUNT(*) FROM recipients
-- UNION ALL
-- SELECT 'transplant_centers', COUNT(*) FROM transplant_centers
-- UNION ALL
-- SELECT 'matches',          COUNT(*) FROM matches
-- UNION ALL
-- SELECT 'notifications',    COUNT(*) FROM notifications;


-- ═══════════════════════════════════════════════════
--  JAVA INTEGRATION NOTES
-- ═══════════════════════════════════════════════════
--
--  1. Download MySQL Connector/J JAR:
--       https://dev.mysql.com/downloads/connector/j/
--       (choose "Platform Independent" → .zip)
--
--  2. Place the JAR in your project folder, e.g.:
--       dpminiproject/mysql-connector-j-8.x.x.jar
--
--  3. Update NationalTransplantRegistry.java:
--       private static final String URL      = "jdbc:mysql://localhost:3306/organmatch_db";
--       private static final String USER     = "root";
--       private static final String PASSWORD = "your_password";   ← set your MySQL root password
--
--  4. Compile with the connector on the classpath:
--       Windows:  javac -cp ".;mysql-connector-j-8.x.x.jar" *.java
--       Mac/Linux: javac -cp ".:mysql-connector-j-8.x.x.jar" *.java
--
--  5. Run with the connector on the classpath:
--       Windows:  java -cp ".;mysql-connector-j-8.x.x.jar" ChainDemo
--       Mac/Linux: java -cp ".:mysql-connector-j-8.x.x.jar" ChainDemo
--
-- ═══════════════════════════════════════════════════
