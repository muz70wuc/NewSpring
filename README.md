# NewSpring

Eine serverseitig gerenderte Spring-Boot-Webanwendung für ein geschütztes Benutzer- und Kontaktportal. Besucher können öffentliche Portfolio- und Rechtstexte aufrufen, sich registrieren und anmelden. Angemeldete Benutzer erhalten ein Dashboard, können Profildaten verwalten und über ein Kontaktformular eine E-Mail senden.

## Verwendete Technologien

- Java; die Maven-Konfiguration verwendet Java 17.
- Spring Boot 4.1.1 und Maven.
- Spring MVC für HTTP-Anfragen und Controller.
- Thymeleaf für serverseitig gerenderte HTML-Seiten.
- Pico.css 2 als CSS-Framework in `templates/fragments/base.html`.
- Spring Security mit formularbasierter Anmeldung, Logout, BCrypt-Passwort-Hashing und CSRF-Schutz.
- Spring Data JPA / Hibernate für die Persistenz.
- PostgreSQL 16 als Laufzeitdatenbank und H2 für Repository-Tests.
- Spring Mail für SMTP-Kontakt-E-Mails.
- Bucket4j für Rate-Limiting des Registrierungsendpunkts.
- Spring Actuator, DevTools und H2 Console für Monitoring bzw. lokale Entwicklung.
- Docker und Docker Compose für den containerisierten Betrieb.
- JUnit 5, Mockito, AssertJ, Spring MVC Test und ArchUnit für Tests.

## Architektur und Struktur

Das Projekt verwendet eine klassische Layered Architecture innerhalb des Java-Packages `learning.basics`:

```text
src/
├── main/
│   ├── java/learning/basics/
│   │   ├── BasicsApplication.java       # Spring-Boot-Einstiegspunkt
│   │   ├── config/                      # Security und Request-Filter
│   │   ├── controller/                  # Web-Routen und Formularabläufe
│   │   ├── dto/                         # Datenobjekte für Formulare
│   │   ├── mapper/                      # DTO-Entity-Konvertierung
│   │   ├── model/                       # JPA-Entities, derzeit User
│   │   ├── repository/                  # Spring-Data-Zugriff auf die Datenbank
│   │   └── service/                     # Geschäftslogik und externe Dienste
│   └── resources/
│       ├── application.properties.example
│       ├── static/                      # Statische Dateien
│       └── templates/                   # Thymeleaf-Seiten und Layout-Fragmente
└── test/
    ├── java/learning/basics/            # Unit-, Slice-, Security- und Architekturtests
    └── resources/application-test.properties
```

### Schichten und Verantwortlichkeiten

- **Controller:** `PublicController` stellt Startseite, Datenschutz und Nutzungsbedingungen bereit. `AuthController` übernimmt Login-/Registrierungsseiten und Registrierung. `UserHomeController`, `ProfileController` und `ContactController` bilden geschützte Benutzerabläufe ab.
- **Service:** `UserService` bündelt Benutzer-, Profil- und Löschlogik. `CustomUserDetailsService` lädt Benutzer für Spring Security. `EmailService` erstellt und versendet Kontakt-E-Mails. `TimerService` plant die automatische Löschung neu registrierter Accounts nach zwei Jahren.
- **Repository:** `UserRepository` erweitert `JpaRepository<User, Long>` und bietet Abfragen nach Benutzername und E-Mail-Adresse.
- **Model:** `User` ist die persistierte Benutzer-Entity. Passwörter werden vor dem Speichern mit BCrypt verschlüsselt.
- **DTOs und Validierung:** `RegisterDto`, `ProfileDto` und `ContactDto` halten Formulardaten und verwenden Jakarta Bean Validation.
- **Mapper:** `UserMapper` trennt Formulardaten von der persistierten Entity und erzeugt Profil-Datenobjekte.
- **View-Schicht:** Thymeleaf-Seiten liegen unter `templates/`. `fragments/base.html` liefert gemeinsames Layout, Navigation und Footer.
- **Konfiguration:** `SecurityConfig` schützt alle nicht öffentlichen Routen. `RateLimitingFilter` begrenzt Registrierungsversuche und berücksichtigt Proxy-Header wie `CF-Connecting-IP` und `X-Forwarded-For`.

Controller greifen über Services auf Repositories und externe Dienstklassen zu. Die ArchUnit-Prüfung stellt unter anderem sicher, dass Controller nicht direkt auf Repositories zugreifen und Services keine Controller importieren.

## Benutzerabläufe und Routen

| Route | Zugriff | Funktion |
| --- | --- | --- |
| `GET /` | Öffentlich | Startseite und Portfolio-Übersicht |
| `GET /privacy` | Öffentlich | Datenschutzerklärung |
| `GET /terms` | Öffentlich | Nutzungsbedingungen |
| `GET /login` | Öffentlich | Login-Formular |
| `GET /register` | Öffentlich | Registrierungsformular |
| `POST /register` | Öffentlich | Validierung und Anlage eines Benutzers |
| `GET /userHome` | Angemeldet | Dashboard; unvollständige Profile werden zu `/profile` geleitet |
| `GET /profile` | Angemeldet | Profil- und Firmendaten anzeigen |
| `POST /profile` | Angemeldet | Profil aktualisieren |
| `POST /profile/delete` | Angemeldet | Account nach Passwortbestätigung löschen |
| `GET /contact` | Angemeldet | Kontaktformular anzeigen |
| `POST /contact` | Angemeldet | Kontakt-E-Mail versenden |
| `POST /logout` | Angemeldet | Session beenden |

Registrierungen werden zusätzlich durch ein Honeypot-Feld gegen einfache Bots und durch ein Bucket4j-Rate-Limit von maximal drei relevanten Requests geschützt. Geschützte POST-Anfragen benötigen einen CSRF-Token.

## Konfiguration

Die Vorlage liegt in `src/main/resources/application.properties.example`. Für eine lokale Ausführung werden mindestens PostgreSQL-Verbindungsdaten sowie die Mailvariablen `MAIL_USERNAME`, `MAIL_PASSWORD` und `MAIL_TARGET` benötigt.

Für Docker Compose werden die Datenbankparameter im Compose-File gesetzt. Die Mailwerte werden aus einer lokalen `.env`-Datei gelesen. Eine `.env`-Datei mit echten Zugangsdaten sollte nicht versioniert werden.

## Lokal starten

Voraussetzungen: JDK 17, Maven bzw. Maven Wrapper, PostgreSQL und konfigurierte Anwendungseigenschaften.

```powershell
.\mvnw.cmd spring-boot:run
```

Danach ist die Anwendung unter [http://localhost:8080](http://localhost:8080) erreichbar.

## Mit Docker Compose starten

Eine `.env`-Datei mit den Mailvariablen anlegen und anschließend aus dem Projektverzeichnis starten:

```powershell
docker compose up --build
```

Compose startet PostgreSQL 16 und wartet über den Healthcheck, bevor die Webanwendung gestartet wird. Die Anwendung ist anschließend unter [http://localhost:8080](http://localhost:8080) erreichbar.

Das Dockerfile verwendet für Build und Runtime aktuell Eclipse Temurin 21, während `pom.xml` Java 17 als Projektversion definiert. Für einen einheitlichen Build sollten diese Zielversionen künftig aufeinander abgestimmt werden.

## Tests und Testabdeckung

Die Tests liegen unter `src/test/java/learning/basics`. Insgesamt sind aktuell **16 Testklassen mit 70 JUnit-5-`@Test`-Methoden** vorhanden; zusätzlich wird eine ArchUnit-Architekturregel ausgeführt.

| Bereich | Testumfang |
| --- | --- |
| Anwendungskontext | Start des Spring-Kontexts mit dem Testprofil |
| Controller | Öffentliche Seiten, Login/Registrierung, Validierungsfehler, Profil-Update, Account-Löschung, Dashboard-Weiterleitung und Kontaktformular |
| Services | Benutzeranlage, BCrypt-Verhalten, Benutzer-/Profilabfragen, Profilstatus, E-Mail-Prüfung, Löschung und Kontaktverarbeitung mit Mockito |
| E-Mail | Aufbau und Versand von `SimpleMailMessage`, inklusive fehlender Profildaten |
| Timer | Planung und Ausführung der automatischen Account-Löschung nach 730 Tagen |
| Repository | `findByUsername` und `findByEmail` mit einer echten H2-Testdatenbank via `@DataJpaTest` |
| Bean Validation | Register-, Profil- und Kontakt-DTOs inklusive Pflichtfeldern, Längen, E-Mail, Telefon und Zustimmung |
| Security | Authentifizierung, CSRF-Schutz und Zugriff auf geschützte Routen über MockMvc |
| Rate Limiting | Erlaubte und blockierte Requests sowie Cloudflare- und Forwarded-IP-Header |
| Architektur | ArchUnit-Regeln für die Trennung von Controller-, Service-, Repository-, Model- und DTO-Abhängigkeiten |

Tests ausführen:

```powershell
.\mvnw.cmd test
```

Controller-Tests verwenden überwiegend `@WebMvcTest` und MockMvc. Service-Tests isolieren Abhängigkeiten mit Mockito, während Repository-Tests die Persistenz mit H2 prüfen. Eine vollständige End-to-End-Abdeckung mit echtem PostgreSQL, echtem SMTP-Server oder Browserautomatisierung ist derzeit nicht Bestandteil des Test-Sets.

## Maven-Befehle

```powershell
.\mvnw.cmd clean test       # Bauen und alle Tests ausführen
.\mvnw.cmd spring-boot:run # Lokal starten
.\mvnw.cmd clean package   # JAR bauen
```

## Hinweise

- `target/` enthält generierte Build- und Testartefakte und gehört nicht zum Quellcodeaufbau.
- `spring.jpa.hibernate.ddl-auto=update` ist für Entwicklung bequem, sollte produktiv aber durch eine kontrollierte Migrationsstrategie ersetzt werden.
- SMTP-Zugangsdaten gehören ausschließlich in Umgebungsvariablen oder eine lokale, nicht versionierte Konfiguration.
