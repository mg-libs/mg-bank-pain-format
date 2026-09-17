# mg-bank-pain-format-spring-boot-starter

Spring Boot Starter pour la génération de fichiers **ISO 20022 Payment Initiation (pain.\*)**.

Ajoute une seule dépendance et génère des fichiers de virement SEPA conformes aux standards bancaires internationaux. Le starter ne stocke rien, n'initie aucun paiement, et n'a aucun couplage au domaine métier du consommateur.

---

## Formats supportés

| Bean Spring | Format ISO 20022 | Usage |
|-------------|-----------------|-------|
| `pain001V03Generator` | pain.001.001.03 | Virement SEPA Credit Transfer (version actuelle) |
| `pain001V09Generator` | pain.001.001.09 | Virement SEPA Credit Transfer Instant (SCT Inst) |
| `pain008V02CoreGenerator` | pain.008.001.02 | Prélèvement SEPA Direct Debit — SDD Core |
| `pain008V02B2bGenerator` | pain.008.001.02 | Prélèvement SEPA Direct Debit — SDD B2B |

---

## Installation

### Prérequis

- Java 21+
- Spring Boot 3.x

### Option A — Après publication Maven Central

```xml
<dependency>
    <groupId>io.github.mg-libs</groupId>
    <artifactId>mg-bank-pain-format-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option B — Installation locale (développement)

```bash
cd mg-bank-pain-format
mvn clean install -DskipTests
```

Puis dans le projet consommateur :

```xml
<dependency>
    <groupId>io.github.mg-libs</groupId>
    <artifactId>mg-bank-pain-format-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Configuration

Dans `application.yml` du projet consommateur :

```yaml
mg-bank:
  pain:
    enabled: true          # true par défaut — mettre false pour désactiver complètement

    # Pour pain.001 (virements) — compte émetteur
    debtor:
      name: "MA SOCIÉTÉ SAS"
      iban: "FR7630004000031234567890143"
      bic:  "BNPAFRPPXXX"

    # Pour pain.008 (prélèvements) — compte collecteur
    creditor:
      name: "MA SOCIÉTÉ SAS"
      iban: "FR7630004000031234567890143"
      bic:  "BNPAFRPPXXX"
      creditor-id: "FR72ZZZ123456"   # Identifiant Créancier SEPA (ICS)

    output:
      charset: "UTF-8"     # UTF-8 par défaut — certaines banques exigent ISO-8859-1
```

---

## Utilisation

## Utilisation — pain.008 (prélèvement SEPA)

### Injection

```java
@Service
public class PrelevementService {

    private final PainGenerator<SepaDirectDebitRequest> generator;

    // "pain008V02CoreGenerator" ou "pain008V02B2bGenerator"
    public PrelevementService(
            @Qualifier("pain008V02CoreGenerator") PainGenerator<SepaDirectDebitRequest> generator) {
        this.generator = generator;
    }

    public byte[] genererPrelevement(List<MonPrelevement> prelevements, LocalDate dateExecution) {
        List<SepaDirectDebitRequest> requests = prelevements.stream()
                .map(p -> new SepaDirectDebitRequest(
                        p.getReference(),           // endToEndId
                        p.getMontant(),             // amount
                        "EUR",
                        p.getNomDebiteur(),         // debtorName
                        p.getIbanDebiteur(),        // debtorIban
                        p.getBicDebiteur(),         // debtorBic
                        p.getLibelle(),             // remittanceInfo
                        p.getReferencMandat(),      // mandateId
                        p.getDateSignatureMandat(), // mandateDate (LocalDate)
                        dateExecution,              // collectionDate (même pour tout le lot)
                        "RCUR"                      // sequenceType : FRST, RCUR, OOFF, FNAL
                ))
                .toList();

        String messageId = "SDD-" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8);
        return generator.generate(requests, messageId).getContent();
    }
}
```

### Types de séquence (`sequenceType`)

| Code | Signification |
|------|--------------|
| `FRST` | Premier prélèvement sur ce mandat |
| `RCUR` | Prélèvement récurrent |
| `OOFF` | Prélèvement ponctuel (mandat à usage unique) |
| `FNAL` | Dernier prélèvement sur ce mandat |

> **Important :** toutes les requêtes d'un même appel à `generate()` doivent avoir le même `collectionDate` et le même `sequenceType` — ce sont des champs de niveau lot (`PmtInf`) dans le schéma SEPA.

---

## Utilisation — pain.001 (virement SEPA)

### Injection

Deux beans sont disponibles simultanément. Choisissez la version via `@Qualifier` :

```java
@Service
public class VirementService {

    private final PainGenerator<SepaTransferRequest> generator;

    // Injecter la version souhaitée : "pain001V03Generator" ou "pain001V09Generator"
    public VirementService(
            @Qualifier("pain001V03Generator") PainGenerator<SepaTransferRequest> generator) {
        this.generator = generator;
    }
}
```

### Construire les requêtes

```java
List<SepaTransferRequest> requests = List.of(
    new SepaTransferRequest(
        "REF-2026-001",          // endToEndId — identifiant bout-en-bout unique (max 35 cars)
        new BigDecimal("150.00"), // montant
        "EUR",                   // devise
        "DUPONT JEAN",           // nom du bénéficiaire (max 70 cars)
        "FR7630006000011234567890189", // IBAN bénéficiaire
        "AGRIFRPP882",           // BIC bénéficiaire
        "Remboursement facture 2026-001" // libellé (max 140 cars)
    )
);
```

### Trois modes de sortie

```java
String messageId = "MSG-" + System.currentTimeMillis(); // identifiant unique du message

// 1. byte[] — stockage BDD, réponse HTTP, etc.
PainGenerationResult result = generator.generate(requests, messageId);
byte[] xml = result.getContent();

// 2. OutputStream — streaming HTTP, export ZIP
generator.generateToStream(requests, messageId, response.getOutputStream());

// 3. Path — écriture sur disque
generator.generateToFile(requests, messageId, Path.of("/exports/virement.xml"));
```

### Résultat (`PainGenerationResult`)

```java
PainGenerationResult result = generator.generate(requests, messageId);

result.getContent();          // byte[]      — contenu XML
result.getFilename();         // String      — ex: "virements_MSG001.xml"
result.getMessageId();        // String      — messageId utilisé dans le fichier
result.getTransactionCount(); // int         — nombre de transactions
result.getControlSum();       // BigDecimal  — somme de contrôle (total des montants)
```

---

## Exemple complet — Endpoint HTTP

```java
@RestController
@RequestMapping("/api/virements")
public class VirementController {

    private final PainGenerator<SepaTransferRequest> generator;

    public VirementController(
            @Qualifier("pain001V03Generator") PainGenerator<SepaTransferRequest> generator) {
        this.generator = generator;
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody List<VirementDTO> virements) {
        List<SepaTransferRequest> requests = virements.stream()
                .map(v -> new SepaTransferRequest(
                        v.getReference(),
                        v.getMontant(),
                        "EUR",
                        v.getNomBeneficiaire(),
                        v.getIban(),
                        v.getBic(),
                        v.getLibelle()
                ))
                .toList();

        String messageId = "MSG-" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8);
        PainGenerationResult result = generator.generate(requests, messageId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + result.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_XML)
                .body(result.getContent());
    }
}
```

---

## Règles de normalisation automatique

Le starter applique silencieusement les contraintes SEPA sans lever d'erreur :

| Champ | Limite | Comportement |
|-------|--------|-------------|
| `endToEndId` | 35 caractères | Tronqué si dépassé |
| `creditorName` | 70 caractères | Tronqué si dépassé |
| `remittanceInfo` | 140 caractères | Tronqué si dépassé |
| `messageId` / `pmtInfId` | 35 caractères | Tronqué si dépassé |

---

## Désactiver le starter

```yaml
mg-bank:
  pain:
    enabled: false
```

Aucun bean n'est créé. Utile pour désactiver dans certains profils (ex: tests unitaires qui ne doivent pas charger l'auto-configuration).

---

## Structure du projet

```
mg-bank-pain-format/
├── src/main/java/mg/pain/
│   ├── autoconfigure/
│   │   ├── PainAutoConfiguration.java    # @AutoConfiguration — expose les 4 beans nommés
│   │   └── PainProperties.java           # binding mg-bank.pain.* (debtor + creditor + output)
│   ├── generator/
│   │   ├── PainGenerator.java            # interface générique <T>
│   │   ├── pain001/
│   │   │   ├── Pain001V03Generator.java  # pain.001.001.03
│   │   │   └── Pain001V09Generator.java  # pain.001.001.09
│   │   └── pain008/
│   │       └── Pain008V02Generator.java  # pain.008.001.02 (CORE et B2B via constructeur)
│   ├── domain/
│   │   ├── SepaTransferRequest.java      # DTO entrée pain.001
│   │   ├── SepaDirectDebitRequest.java   # DTO entrée pain.008
│   │   └── PainGenerationResult.java     # DTO sortie commune
│   ├── jaxb/
│   │   ├── pain001v03/                   # classes JAXB pain.001.001.03
│   │   ├── pain001v09/                   # classes JAXB pain.001.001.09
│   │   └── pain008v02/                   # classes JAXB pain.008.001.02
│   ├── util/
│   │   ├── XmlCalendarUtils.java         # helpers XMLGregorianCalendar
│   │   └── SepaStringUtils.java          # normalisation (troncature, nettoyage)
│   └── exception/
│       └── PainGenerationException.java  # exception runtime du starter
├── src/main/resources/META-INF/spring/
│   └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
├── src/test/java/mg/pain/
│   ├── Pain001V03GeneratorTest.java      # 6 tests
│   └── Pain008V02GeneratorTest.java      # 6 tests
└── pom.xml
```

---

## Feuille de route

| Version | Contenu |
|---------|---------|
| **1.0.0** | pain.001.001.03, pain.001.001.09, pain.008.001.02 (CORE + B2B), publication Maven Central |
| 1.1.0 | pain.002.001.03 (Status Report), pain.007.001.02 (Reversal) |
| 2.0.0 | Support camt.\*, pacs.\* — renommage éventuel en `mg-bank-iso20022-format` |

---

## Licence

Apache License 2.0 — voir [LICENSE](LICENSE).
