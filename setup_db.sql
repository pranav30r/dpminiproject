-- ============================================================
--  TransplantIQ — Full Database Setup Script
--  Run once:  mysql -u root -p < setup_db.sql
--         OR open in MySQL Workbench and execute all
-- ============================================================

CREATE DATABASE IF NOT EXISTS organmatch_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE organmatch_db;

-- ──────────────────────────────────────────────
--  1. DONOR ORGANS TABLE
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS organs (
    organ_id    INT          AUTO_INCREMENT PRIMARY KEY,
    organ_code  VARCHAR(20)  NOT NULL UNIQUE COMMENT 'e.g. K101, H201',
    organ_type  VARCHAR(50)  NOT NULL COMMENT 'kidney | heart | liver | lung',
    blood_group VARCHAR(5)   NOT NULL,
    hla_profile DOUBLE       NOT NULL COMMENT 'HLA compatibility score 1-100',
    city        VARCHAR(100) NOT NULL,
    latitude    DOUBLE       NOT NULL DEFAULT 0.0,
    longitude   DOUBLE       NOT NULL DEFAULT 0.0,
    status      VARCHAR(30)  NOT NULL DEFAULT 'AVAILABLE',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ──────────────────────────────────────────────
--  2. RECIPIENTS TABLE
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS recipients (
    recipient_id  VARCHAR(20)  NOT NULL PRIMARY KEY COMMENT 'e.g. P001',
    name          VARCHAR(100) NOT NULL,
    age           INT          NOT NULL,
    phone         VARCHAR(20),
    address       TEXT,
    blood_group   VARCHAR(5)   NOT NULL,
    hla_profile   DOUBLE       NOT NULL COMMENT 'HLA score 1-100',
    city          VARCHAR(100) NOT NULL,
    latitude      DOUBLE       NOT NULL DEFAULT 0.0,
    longitude     DOUBLE       NOT NULL DEFAULT 0.0,
    urgency       VARCHAR(20)  NOT NULL COMMENT 'LOW | MEDIUM | HIGH | CRITICAL',
    status        VARCHAR(30)  NOT NULL DEFAULT 'WAITING',
    registered_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ──────────────────────────────────────────────
--  3. MATCH RESULTS TABLE
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS match_results (
    match_id         INT         AUTO_INCREMENT PRIMARY KEY,
    organ_code       VARCHAR(20) NOT NULL COMMENT 'ref organs.organ_code',
    recipient_id     VARCHAR(20) NOT NULL COMMENT 'ref recipients.recipient_id',
    composite_score  INT         NOT NULL COMMENT 'total compatibility score',
    hla_score        DOUBLE      NOT NULL DEFAULT 0.0,
    distance_km      DOUBLE      NOT NULL DEFAULT 0.0,
    matched_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organ_code)   REFERENCES organs(organ_code),
    FOREIGN KEY (recipient_id) REFERENCES recipients(recipient_id)
);

-- ──────────────────────────────────────────────
--  Seed: sample donor organs (matches ChainDemo hardcoded data)
-- ──────────────────────────────────────────────
INSERT IGNORE INTO organs
    (organ_code, organ_type, blood_group, hla_profile, city, latitude, longitude, status)
VALUES
    ('K101', 'kidney', 'O+',  88.0, 'Nagpur', 21.1458, 79.0882, 'AVAILABLE'),
    ('H201', 'heart',  'A-',  72.0, 'Mumbai', 19.0760, 72.8777, 'AVAILABLE'),
    ('L301', 'liver',  'B+',  65.0, 'Pune',   18.5204, 73.8567, 'AVAILABLE');

-- ──────────────────────────────────────────────
--  Seed: sample recipients (matches ChainDemo hardcoded data)
-- ──────────────────────────────────────────────
INSERT IGNORE INTO recipients
    (recipient_id, name, age, phone, address,
     blood_group, hla_profile, city, latitude, longitude, urgency, status)
VALUES
    ('P001', 'Aarav Sharma', 34, '9876500001', 'Nagpur, MH',
     'O+', 90.0, 'Nagpur', 21.1458, 79.0882, 'CRITICAL', 'WAITING'),
    ('P002', 'Meena Patel',  52, '9876500002', 'Pune, MH',
     'A-', 70.0, 'Pune',   18.5204, 73.8567, 'HIGH',     'WAITING'),
    ('P003', 'Rajan Gupta',  45, '9876500003', 'Mumbai, MH',
     'O+', 60.0, 'Mumbai', 19.0760, 72.8777, 'MEDIUM',   'WAITING'),
    ('P004', 'Sunita Desai', 29, '9876500004', 'Thane, MH',
     'AB+', 55.0, 'Thane', 19.2183, 72.9781, 'HIGH',     'WAITING'),
    ('P005', 'Vikram Joshi', 61, '9876500005', 'Nashik, MH',
     'O-', 82.0, 'Nashik', 19.9975, 73.7898, 'LOW',      'WAITING');

-- ──────────────────────────────────────────────
--  Verify
-- ──────────────────────────────────────────────
SELECT 'organmatch_db setup complete!' AS status;
SHOW TABLES;
SELECT COUNT(*) AS seed_organs     FROM organs;
SELECT COUNT(*) AS seed_recipients FROM recipients;
