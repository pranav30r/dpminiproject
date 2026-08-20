<div align="center">

# 🏥 TransplantIQ

### Smart Organ-Recipient Matching System

*A Java + MySQL application demonstrating 5 Gang of Four Design Patterns*

---

**Singleton** · **Factory Method** · **Chain of Responsibility** · **Observer** · **Proxy**

</div>

---

## 📌 About the Project

TransplantIQ simulates a **National Organ Transplant Registry** that intelligently matches donated organs to the most compatible recipients. The system evaluates candidates through a multi-step pipeline considering blood type compatibility, tissue/HLA matching, geographic distance, and medical urgency — all backed by a MySQL database.

The project demonstrates how **five GoF design patterns** work together in a real-world healthcare scenario, not as isolated textbook examples but as an **integrated, functioning system**.

---

## 👨‍💻 Team Members & Contributions

| Member     | Pattern(s) Implemented                        | Files Worked On                                                                                                                                                       |
|------------|-----------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Jogi**   | Singleton Pattern + Factory Method Pattern     | `NationalTransplantRegistry.java`, `OrganFactory.java`, `Organ.java`, `Heart.java`, `Kidney.java`, `Liver.java`, `Lung.java`                                          |
| **Hiten**  | Chain of Responsibility Pattern                | `BloodTypeMatcher.java`, `TissueHLAMatcher.java`, `GeographicDistanceFilter.java`, `UrgencyScoreEvaluator.java`, `AbstractCompatibilityHandler.java`, `CompatibilityHandler.java`, `MatchingEngine.java`, `MatchingConfig.java` |
| **Rathi**  | Observer Pattern                               | `MatchObserver.java`, `DoctorObserver.java`, `TransplantCenterObserver.java`, `TransportTeamObserver.java`, `AllocationService.java`, `RegistryMatchingService.java`    |
| **Chetan** | Proxy Pattern                                  | `PrivacyProxy.java`, `PatientRecord.java`                                                                                                                             |

### Shared / Common Files

| File                        | Purpose                                                          |
|-----------------------------|------------------------------------------------------------------|
| `ChainDemo.java`            | Main entry point — runs the demo showcasing all 5 patterns       |
| `DonorOrgan.java`           | Data model for a donated organ                                   |
| `Recipient.java`            | Data model for a transplant recipient                            |
| `RecipientEvaluation.java`  | Stores evaluation results from the matching pipeline             |
| `MatchResult.java`          | Holds the final match result (organ + best recipient)            |
| `Location.java`             | Geographic coordinates and distance calculation                  |
| `UrgencyLevel.java`         | Enum for patient urgency (LOW, MEDIUM, HIGH, CRITICAL)           |
| `organmatch_db.sql`         | MySQL database schema + sample data                              |

---

## 📋 Design Patterns Explained

### 1️⃣ Singleton — `NationalTransplantRegistry` *(Jogi)*

> **Problem**: The system needs exactly one central registry to manage all organs, recipients, and the database connection. Multiple instances would cause data inconsistency.

> **Solution**: `NationalTransplantRegistry.getInstance()` ensures only one instance exists across the entire application. It holds the MySQL connection, organ list, recipient list, and match history.

**Files**: [`NationalTransplantRegistry.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/NationalTransplantRegistry.java)

---

### 2️⃣ Factory Method — `OrganFactory` *(Jogi)*

> **Problem**: The system needs to create different organ objects (Heart, Kidney, Liver, Lung) based on a string type from the database, without hardcoding creation logic everywhere.

> **Solution**: `OrganFactory.createOrgan("Heart")` returns the correct `Organ` subclass. Adding a new organ type (e.g., Pancreas) requires only one new class and one line in the factory — no changes to existing code (Open/Closed Principle).

**Files**: [`OrganFactory.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/OrganFactory.java) · [`Organ.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/Organ.java) · [`Heart.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/Heart.java) · [`Kidney.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/Kidney.java) · [`Liver.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/Liver.java) · [`Lung.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/Lung.java)

---

### 3️⃣ Chain of Responsibility — Matching Pipeline *(Hiten)*

> **Problem**: Organ-recipient matching involves multiple sequential checks. Hardcoding all checks in one place creates a monolithic, rigid system.

> **Solution**: Each matching criterion is an independent handler in a chain:
>
> `BloodTypeMatcher → TissueHLAMatcher → GeographicDistanceFilter → UrgencyScoreEvaluator`
>
> Each handler evaluates the recipient, adds to their score, and either passes them to the next handler or rejects them. New criteria can be inserted without modifying existing handlers.

**Files**: [`BloodTypeMatcher.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/BloodTypeMatcher.java) · [`TissueHLAMatcher.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/TissueHLAMatcher.java) · [`GeographicDistanceFilter.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/GeographicDistanceFilter.java) · [`UrgencyScoreEvaluator.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/UrgencyScoreEvaluator.java) · [`AbstractCompatibilityHandler.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/AbstractCompatibilityHandler.java) · [`CompatibilityHandler.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/CompatibilityHandler.java)

---

### 4️⃣ Observer — Notification System *(Rathi)*

> **Problem**: When an organ is matched to a recipient, multiple stakeholders (doctors, transplant centers, transport teams) must be notified simultaneously.

> **Solution**: The `AllocationService` maintains a list of observers. When a match is confirmed, all registered observers are automatically notified. New observers can be added without changing the matching engine (loose coupling).

**Files**: [`MatchObserver.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/MatchObserver.java) · [`DoctorObserver.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/DoctorObserver.java) · [`TransplantCenterObserver.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/TransplantCenterObserver.java) · [`TransportTeamObserver.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/TransportTeamObserver.java) · [`AllocationService.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/AllocationService.java)

---

### 5️⃣ Proxy — Privacy Protection *(Chetan)*

> **Problem**: Patient data (name, phone, address) is sensitive health information. Not everyone should see it.

> **Solution**: `PrivacyProxy` wraps a `Recipient` object and implements the `PatientRecord` interface. Unauthorized users see masked data (`REDACTED`), while authorized clinical staff see the real information — all through the same interface, transparently.

**Files**: [`PrivacyProxy.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/PrivacyProxy.java) · [`PatientRecord.java`](file:///c:/Users/ASUS/Downloads/dpminiproject/PatientRecord.java)

---

## ⚙️ Setup & Installation

### Step 1: Install Java JDK (17 or higher)

1. Download from: https://www.oracle.com/java/technologies/downloads/
2. Run the installer with default options.
3. Add Java to your system PATH:
   - Press `Win + S` → search **"Environment Variables"** → click **"Edit the system environment variables"**
   - Click **"Environment Variables"** → find **Path** under System variables → click **Edit**
   - Click **New** → add:
     ```
     C:\Program Files\Java\jdk-17\bin
     ```
4. Verify in a new terminal:
   ```
   java -version
   javac -version
   ```

### Step 2: Install MySQL Server (8.0 or higher)

1. Download from: https://dev.mysql.com/downloads/installer/
2. Run the installer → choose **"MySQL Server"** (Server only setup type).
3. During setup:
   - Keep default port: **3306**
   - Set a **root password** (remember it!)
   - Enable **"Start MySQL Server at System Startup"**
4. Add MySQL to PATH:
   - Same as above, add:
     ```
     C:\Program Files\MySQL\MySQL Server 8.0\bin
     ```

### Step 3: Download MySQL JDBC Connector

1. Download from: https://dev.mysql.com/downloads/connector/j/
2. Select **"Platform Independent"** → download the `.zip`.
3. Extract and copy the `mysql-connector-j-X.X.X.jar` file into the project folder.

### Step 4: Load the Database

Open **MySQL 8.0 Command Line Client** (from Start Menu), enter your root password, then run:

```sql
source C:/path/to/dpminiproject/organmatch_db.sql
```
```sql
source c:/temp/organmatch_db.sql
```
> ⚠️ **Use forward slashes `/` only!** Backslashes cause errors because MySQL interprets `\d`, `\n`, etc. as special commands.

You should see multiple `Query OK` messages — the database, tables, and sample data are now ready.

### Step 5: Configure the Password

Open `NationalTransplantRegistry.java` and update line 17:

```java
private static final String DEFAULT_PASSWORD = "YOUR_MYSQL_ROOT_PASSWORD";
```

Or skip this and pass the password at runtime (see Step 7).

### Step 6: Compile

Open a terminal in the project folder:

**Windows:**
```powershell
javac -cp ".;mysql-connector-j-X.X.X.jar" *.java
```

**Mac / Linux:**
```bash
javac -cp ".:mysql-connector-j-X.X.X.jar" *.java
```

### Step 7: Run

**Windows:**
```powershell
java -cp ".;mysql-connector-j-X.X.X.jar" ChainDemo
```

**Mac / Linux:**
```bash
java -cp ".:mysql-connector-j-X.X.X.jar" ChainDemo
```

**Or pass the password at runtime (without editing code):**
```powershell
java -cp ".;mysql-connector-j-X.X.X.jar" -Dtransplantiq.db.password=YOUR_PASSWORD ChainDemo
```

> Replace `X.X.X` with your actual connector version (e.g., `26.7.0`).

---

## 🗃️ Database Schema

The `organmatch_db.sql` script creates the following tables:

```
┌──────────────┐     ┌──────────────┐     ┌───────────────────┐
│   donors     │────▶│   organs     │────▶│     matches       │
│              │     │              │     │                   │
│ donor_id (PK)│     │ organ_id (PK)│     │ match_id (PK)     │
│ name         │     │ organ_type   │     │ organ_id (FK)     │
│ age          │     │ donor_id (FK)│     │ recipient_id (FK) │
│ blood_group  │     │ blood_group  │     │ compatibility_score│
│ city         │     │ hla_profile  │     │ hla_compatibility │
│ phone        │     │ city/lat/lng │     │ distance_km       │
└──────────────┘     │ status       │     │ match_date        │
                     └──────────────┘     └────────┬──────────┘
                                                   │
┌──────────────────┐                     ┌─────────▼──────────┐
│   recipients     │────────────────────▶│  notifications     │
│                  │                     │                    │
│ patient_id (PK)  │                     │ notification_id(PK)│
│ name, age, phone │                     │ match_id (FK)      │
│ blood_group      │                     │ observer_type      │
│ hla_profile      │                     │ message            │
│ city/lat/lng     │                     │ created_at         │
│ urgency, status  │                     └────────────────────┘
└──────────────────┘

┌──────────────────────┐
│  transplant_centers  │
│                      │
│ center_id (PK)       │
│ center_name          │
│ city                 │
│ contact_number       │
└──────────────────────┘
```

**Sample data included**: 8 donors, 10 organs, 10 recipients, 5 transplant centers, 3 pre-loaded matches, and 9 notifications.

---

## 🔧 Troubleshooting

| Error | Fix |
|-------|-----|
| `mysql is not recognized` | Add MySQL `bin` folder to system PATH and restart terminal |
| `java is not recognized` | Add JDK `bin` folder to system PATH and restart terminal |
| `Failed to open file 'c:\...\d'` | Use **forward slashes** `/` in the `source` path, not backslashes |
| `MySQL connection unavailable` | Ensure MySQL service is running (`Win+R` → `services.msc` → MySQL → Start) |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Make sure the `.jar` connector is in the classpath with `-cp` |
| `Access denied for user 'root'` | Wrong password — update in code or pass with `-Dtransplantiq.db.password=...` |

> 💡 **Note**: If MySQL is not available, the program still runs in **in-memory demo mode** — no crash, just no database persistence.

---

## 📂 Complete File List

```
dpminiproject/
│
│── ChainDemo.java                    ← Main entry point (all patterns demo)
│
│── NationalTransplantRegistry.java   ← [Jogi]   Singleton + DB connection
│── OrganFactory.java                 ← [Jogi]   Factory Method
│── Organ.java                        ← [Jogi]   Organ interface
│── Heart.java                        ← [Jogi]   Concrete organ
│── Kidney.java                       ← [Jogi]   Concrete organ
│── Liver.java                        ← [Jogi]   Concrete organ
│── Lung.java                         ← [Jogi]   Concrete organ
│
│── CompatibilityHandler.java         ← [Hiten]  Handler interface
│── AbstractCompatibilityHandler.java ← [Hiten]  Base handler (chaining logic)
│── BloodTypeMatcher.java             ← [Hiten]  Chain handler #1
│── TissueHLAMatcher.java             ← [Hiten]  Chain handler #2
│── GeographicDistanceFilter.java     ← [Hiten]  Chain handler #3
│── UrgencyScoreEvaluator.java        ← [Hiten]  Chain handler #4
│── MatchingEngine.java               ← [Hiten]  Runs the chain pipeline
│── MatchingConfig.java               ← [Hiten]  Configurable thresholds
│
│── MatchObserver.java                ← [Rathi]  Observer interface
│── DoctorObserver.java               ← [Rathi]  Concrete observer
│── TransplantCenterObserver.java     ← [Rathi]  Concrete observer
│── TransportTeamObserver.java        ← [Rathi]  Concrete observer
│── AllocationService.java            ← [Rathi]  Subject (notifies observers)
│── RegistryMatchingService.java      ← [Rathi]  Orchestrates matching + notify
│
│── PatientRecord.java                ← [Chetan] Subject interface for Proxy
│── PrivacyProxy.java                 ← [Chetan] Proxy (masks PII)
│
│── DonorOrgan.java                   ← Shared   Organ data model
│── Recipient.java                    ← Shared   Recipient data model
│── RecipientEvaluation.java          ← Shared   Evaluation result model
│── MatchResult.java                  ← Shared   Final match result model
│── Location.java                     ← Shared   GPS coordinates + distance
│── UrgencyLevel.java                 ← Shared   Urgency enum
│
│── organmatch_db.sql                 ← Database schema + sample data
└── README.md                         ← This file
```

---

<div align="center">

**TransplantIQ** — Design Patterns Mini Project

</div>
