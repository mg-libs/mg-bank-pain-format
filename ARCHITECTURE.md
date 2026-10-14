# mg-bank-pain-format — Architecture

> Pour l'installation, la configuration et les exemples d'utilisation, voir [README.md](README.md).

---

## Décisions d'architecture

| Sujet | Décision | Raison |
|-------|----------|--------|
| Type de livrable | Spring Boot Starter (JAR + auto-configuration) | Intégration native Spring Boot, zéro infrastructure |
| Périmètre | Famille pain.* ISO 20022 | Un seul starter pour tous les formats Payment Initiation |
| Couplage domaine | Aucun — DTOs neutres | Réutilisable par n'importe quel projet |
| Sortie | byte[], OutputStream, Path | Le consommateur décide quoi faire du résultat |
| Sélection de version | Beans Spring nommés + `@Qualifier` | Extensible sans modifier les classes existantes |
| Format d'adresse | Global dans `PainProperties.addressFormat` | Données issues d'une seule base — format uniforme pour tout le fichier |
| Packages JAXB | Un package par version de schéma | Namespaces XML distincts — fusion impossible sans casser la sérialisation |
| Génération XML | JAXB 4.0 (Jakarta XML Binding) | Standard Java pour ISO 20022, classes dérivées des XSD officiels |

---

## Formats supportés

### v1.0.0

| Bean Spring | Format | Nom ISO 20022 | Classe document |
|-------------|--------|--------------|----------------|
| `pain001V03Generator` | pain.001.001.03 | CustomerCreditTransferInitiationV03 | `CustomerCreditTransferInitiationV03` |
| `pain001V09Generator` | pain.001.001.09 | CustomerCreditTransferInitiationV09 | `CustomerCreditTransferInitiationV09` |
| `pain008V02CoreGenerator` | pain.008.001.02 | CustomerDirectDebitInitiationV02 | `CustomerDirectDebitInitiationV02` |
| `pain008V02B2bGenerator` | pain.008.001.02 | CustomerDirectDebitInitiationV02 | `CustomerDirectDebitInitiationV02` |

### v1.1.0

| Bean Spring | Format | Nom ISO 20022 | Classe document |
|-------------|--------|--------------|----------------|
| `pain008V08CoreGenerator` | pain.008.001.08 | CustomerDirectDebitInitiationV08 | `CustomerDirectDebitInitiationV08` |
| `pain008V08B2bGenerator` | pain.008.001.08 | CustomerDirectDebitInitiationV08 | `CustomerDirectDebitInitiationV08` |

---

## DTOs neutres

### `SepaTransferRequest` (entrée pain.001)

```java
private String endToEndId;            // max 35 cars
private BigDecimal amount;
private String currency;              // défaut : "EUR"
private String creditorName;          // max 70 cars
private String creditorIban;
private String creditorBic;
private String remittanceInfo;        // max 140 cars
// Adresse du bénéficiaire (optionnel — format global PainProperties)
private String creditorCountry;       // ISO alpha-2
private String creditorTownName;      // max 35 cars
private String creditorPostalCode;    // max 16 cars
private String creditorAddressLine1;  // HYBRID, max 70 cars
private String creditorAddressLine2;  // HYBRID, max 70 cars
private String creditorStreetName;    // STRUCTURED, max 70 cars
private String creditorBuildingNumber;// STRUCTURED, max 16 cars
```

### `SepaDirectDebitRequest` (entrée pain.008)

```java
private String endToEndId;            // max 35 cars
private BigDecimal amount;
private String currency;              // défaut : "EUR"
private String debtorName;            // max 70 cars
private String debtorIban;
private String debtorBic;
private String remittanceInfo;        // max 140 cars
private String mandateId;             // MndtId, max 35 cars
private LocalDate mandateDate;        // DtOfSgntr
private LocalDate collectionDate;     // ReqdColltnDt — niveau PmtInf, commun à tout le lot
private String sequenceType;          // SeqTp — niveau PmtInf : FRST | RCUR | OOFF | FNAL
// Adresse du débiteur (optionnel — format global PainProperties)
private String debtorCountry;
private String debtorTownName;
private String debtorPostalCode;
private String debtorAddressLine1;    // HYBRID
private String debtorAddressLine2;    // HYBRID
private String debtorStreetName;      // STRUCTURED
private String debtorBuildingNumber;  // STRUCTURED
// Créancier final (optionnel)
private String ultimateCreditorName;  // UltmtCdtr, max 70 cars
```

### `PainGenerationResult` (sortie commune)

```java
private byte[] content;          // contenu XML généré
private String filename;         // ex: "prelevements_MSG001.xml"
private String messageId;
private int transactionCount;
private BigDecimal controlSum;   // somme des montants
```

---

## Interface principale

```java
public interface PainGenerator<T> {
    PainGenerationResult generate(List<T> requests, String messageId);
    void generateToStream(List<T> requests, String messageId, OutputStream out);
    void generateToFile(List<T> requests, String messageId, Path outputPath);
}
```

---

## Auto-configuration Spring Boot

Six beans nommés exposés simultanément. Le consommateur choisit via `@Qualifier`.

```java
@AutoConfiguration
@ConditionalOnProperty(prefix = "mg-bank.pain", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PainProperties.class)
public class PainAutoConfiguration {
    @Bean("pain001V03Generator")     public PainGenerator<SepaTransferRequest>     pain001V03Generator(...)     { ... }
    @Bean("pain001V09Generator")     public PainGenerator<SepaTransferRequest>     pain001V09Generator(...)     { ... }
    @Bean("pain008V02CoreGenerator") public PainGenerator<SepaDirectDebitRequest>  pain008V02CoreGenerator(...) { ... }
    @Bean("pain008V02B2bGenerator")  public PainGenerator<SepaDirectDebitRequest>  pain008V02B2bGenerator(...)  { ... }
    @Bean("pain008V08CoreGenerator") public PainGenerator<SepaDirectDebitRequest>  pain008V08CoreGenerator(...) { ... }
    @Bean("pain008V08B2bGenerator")  public PainGenerator<SepaDirectDebitRequest>  pain008V08B2bGenerator(...)  { ... }
}
```

**Pourquoi des beans nommés plutôt qu'un enum version ?**
Un enum `Pain001Version { V03, V09 }` aurait forcé à ajouter une valeur à chaque nouveau format.
Les beans nommés permettent d'ajouter un format en ajoutant un seul `@Bean` — sans toucher aux classes existantes.

---

## Différences pain.001.001.03 vs pain.001.001.09

| Élément | V03 | V09 |
|---------|-----|-----|
| GroupHeader | `GroupHeader32` | `GroupHeader85` |
| PaymentInstruction | `PaymentInstruction3` | `PaymentInstruction30` |
| CreditTransferTransaction | `CreditTransferTransactionInformation10` | `CreditTransferTransaction34` |
| ReqdExctnDt | `XMLGregorianCalendar` | `DateAndDateTime2Choice` |
| Identification établissement | `BIC` | `BICFI` |
| PostalAddress | `PostalAddress6` | `PostalAddress24` |
| Namespace | `urn:iso:std:iso:20022:tech:xsd:pain.001.001.03` | `urn:iso:std:iso:20022:tech:xsd:pain.001.001.09` |

---

## Différences pain.008.001.02 vs pain.008.001.08

| Élément | V02 | V08 |
|---------|-----|-----|
| GroupHeader | `GroupHeader39` | `GroupHeader83` |
| PaymentInstruction | `PaymentInstructionInformation4` | `PaymentInstruction43` |
| DirectDebitTransaction | `DirectDebitTransactionInformation9` | `DirectDebitTransactionInformation24` |
| MandateRelatedInformation | `MandateRelatedInformation6` | `MandateRelatedInformation14` (+ `AmdmntInd`) |
| Identification établissement | `BIC` | `BICFI` |
| PostalAddress | `PostalAddress6` | `PostalAddress24` |
| ServiceLevel | Élément unique | Liste (`getSvcLvl().add(...)`) |
| RemittanceInformation | `Ustrd` scalaire | `Ustrd` en liste |
| Namespace | `urn:iso:std:iso:20022:tech:xsd:pain.008.001.02` | `urn:iso:std:iso:20022:tech:xsd:pain.008.001.08` |

---

## Adresses postales

L'adresse est incluse uniquement si `country` ET `townName` sont renseignés.
Le mode est défini globalement dans `PainProperties.addressFormat` — un seul mode par fichier,
car les données proviennent d'une source uniforme.

| Mode | Éléments XML générés |
|------|---------------------|
| `HYBRID` | `AdrLine` (1-2) + `PstCd` + `TwnNm` + `Ctry` |
| `STRUCTURED` | `StrtNm` + `BldgNb` + `PstCd` + `TwnNm` + `Ctry` |

> Le mode `unstructured` (uniquement `AdrLine` sans `TwnNm`/`Ctry`) n'est pas supporté —
> non conforme aux règles SEPA.

---

## Packages JAXB

Un package par version de schéma, avec son propre `package-info.java` déclarant le namespace cible.
La fusion entre packages est impossible : JAXB rattache les classes au namespace de leur package,
une classe partagée ne pourrait appartenir qu'à un seul namespace.

```
jaxb/
├── pain001v03/   # urn:iso:std:iso:20022:tech:xsd:pain.001.001.03
├── pain001v09/   # urn:iso:std:iso:20022:tech:xsd:pain.001.001.09
├── pain008v02/   # urn:iso:std:iso:20022:tech:xsd:pain.008.001.02
└── pain008v08/   # urn:iso:std:iso:20022:tech:xsd:pain.008.001.08
```

---

## Structure du projet

```
mg-bank-pain-format/
├── src/main/java/mg/pain/
│   ├── autoconfigure/
│   │   ├── PainAutoConfiguration.java    # 6 beans nommés
│   │   └── PainProperties.java           # binding mg-bank.pain.*
│   ├── generator/
│   │   ├── PainGenerator.java            # interface générique <T>
│   │   ├── pain001/
│   │   │   ├── Pain001V03Generator.java
│   │   │   └── Pain001V09Generator.java
│   │   └── pain008/
│   │       ├── Pain008V02Generator.java  # CORE et B2B via constructeur
│   │       └── Pain008V08Generator.java  # CORE et B2B via constructeur
│   ├── domain/
│   │   ├── AddressFormat.java            # enum HYBRID | STRUCTURED
│   │   ├── SepaTransferRequest.java
│   │   ├── SepaDirectDebitRequest.java
│   │   └── PainGenerationResult.java
│   ├── jaxb/
│   │   ├── pain001v03/
│   │   ├── pain001v09/
│   │   ├── pain008v02/
│   │   └── pain008v08/
│   ├── util/
│   │   ├── XmlCalendarUtils.java
│   │   └── SepaStringUtils.java
│   └── exception/
│       └── PainGenerationException.java
├── src/main/resources/META-INF/spring/
│   └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
├── src/test/java/mg/pain/
│   ├── Pain001V03GeneratorTest.java      # 6 tests
│   ├── Pain001V09GeneratorTest.java      # 9 tests
│   ├── Pain008V02GeneratorTest.java      # 6 tests
│   └── Pain008V08GeneratorTest.java      # 11 tests  — 32 total
├── ARCHITECTURE.md
├── README.md
└── pom.xml
```

---

## Corrections apportées vs pain.001 dans ClemsApp

| # | Problème original | Correction |
|---|------------------|-----------|
| 1 | Couplage à `InvoiceGetResponseDTO` | DTO neutre `SepaTransferRequest` |
| 2 | Sortie uniquement fichier sur disque | byte[], OutputStream, Path au choix |
| 3 | `JAXBContext` recréé à chaque appel | `static final JAXBContext` (singleton) |
| 4 | Une seule version pain.001.001.03 | Beans nommés — V03 et V09 simultanément disponibles |
| 5 | Paramètre `insee` mal nommé | `remittanceInfo` |
| 6 | Encodage ISO-8859-1 hardcodé | Configurable via `mg-bank.pain.output.charset` |
