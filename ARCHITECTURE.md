# mg-bank-pain-format — Architecture

## Vision

Spring Boot Starter couvrant la famille complète des messages **ISO 20022 Payment
Initiation (pain)**. Publié sur Maven Central, il permet à tout projet Spring Boot
de générer des fichiers de paiement conformes aux standards bancaires internationaux
en ajoutant une seule dépendance.

Le starter ne stocke rien, n'initie aucun paiement, et n'a aucune dépendance au
domaine métier du consommateur. Il prend des données en entrée et retourne un fichier
structuré en sortie.

---

## Décisions d'architecture

| Sujet | Décision | Raison |
|-------|----------|--------|
| Type de livrable | Spring Boot Starter (JAR + auto-configuration) | Intégration native Spring Boot, zéro infrastructure |
| Périmètre | Famille pain.* ISO 20022 complète | Un seul starter pour tous les formats Payment Initiation |
| Couplage domaine | Aucun — DTOs neutres internes | Réutilisable par n'importe quel projet |
| Sortie | byte[], OutputStream, fichier — au choix | Le consommateur décide quoi faire du résultat |
| Sélection de version | Beans Spring nommés + `@Qualifier` | Pas d'enum version — extensible à pain.008, camt.*, etc. |
| Publication | Maven Central | Distribution publique standard |
| Génération XML | JAXB (Jakarta XML Binding) | Standard Java pour ISO 20022, classes dérivées des XSD officiels |

---

## Coordonnées Maven

```xml
<dependency>
    <groupId>io.github.mg-libs</groupId>
    <artifactId>mg-bank-pain-format-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Formats supportés

### Phase 1 (v1.0.0)

| Bean Spring | Format | Nom ISO 20022 | Usage |
|-------------|--------|--------------|-------|
| `pain001V03Generator` | pain.001.001.03 | CustomerCreditTransferInitiationV03 | Virement SEPA (version actuelle) |
| `pain001V09Generator` | pain.001.001.09 | CustomerCreditTransferInitiationV09 | Virement SEPA Instant (SCT Inst) |

### Phase 2 (v1.1.0 — v1.2.0)

| Format | Nom ISO 20022 | Usage |
|--------|--------------|-------|
| pain.002.001.03 | CustomerPaymentStatusReportV03 | Retour statut banque (lecture seule) |
| pain.007.001.02 | CustomerPaymentReversalV02 | Annulation de virement |
| pain.008.001.02 | CustomerDirectDebitInitiationV02 | Prélèvement SEPA Core (SDD Core) |
| pain.008.001.03 | CustomerDirectDebitInitiationV03 | Prélèvement SEPA B2B (SDD B2B) |

---

## Configuration consommateur

```yaml
mg-bank:
  pain:
    enabled: true          # true par défaut
    debtor:
      name: "MA SOCIÉTÉ SAS"
      iban: "FR7630004000031234567890143"
      bic: "BNPAFRPPXXX"
    output:
      charset: "UTF-8"     # ou ISO-8859-1 selon banque
```

---

## DTOs neutres

Le starter définit ses propres objets d'entrée/sortie, sans dépendance au domaine
du consommateur. Le consommateur mappe ses propres entités vers ces DTOs.

### `SepaTransferRequest` (entrée pain.001)

```java
public class SepaTransferRequest {
    private String endToEndId;       // identifiant bout-en-bout unique (max 35 cars)
    private BigDecimal amount;       // montant
    private String currency;         // devise (défaut : "EUR")
    private String creditorName;     // nom du bénéficiaire (max 70 cars)
    private String creditorIban;     // IBAN du bénéficiaire
    private String creditorBic;      // BIC du bénéficiaire
    private String remittanceInfo;   // libellé virement (max 140 cars)
}
```

### `SepaDirectDebitRequest` (entrée pain.008 — Phase 2)

```java
public class SepaDirectDebitRequest {
    private String endToEndId;
    private String mandateId;        // référence mandat
    private LocalDate mandateDate;   // date de signature du mandat
    private BigDecimal amount;
    private String currency;
    private String debtorName;
    private String debtorIban;
    private String debtorBic;
    private String remittanceInfo;
}
```

### `PainGenerationResult` (sortie commune)

```java
public class PainGenerationResult {
    private byte[] content;          // contenu XML généré
    private String filename;         // nom de fichier suggéré (ex: virements_MSG001.xml)
    private String messageId;        // msgId utilisé dans le fichier
    private int transactionCount;    // nombre de transactions
    private BigDecimal controlSum;   // somme de contrôle (total des montants)
}
```

---

## Interface principale

```java
public interface PainGenerator<T> {
    // Retourne le résultat complet (XML en byte[] + métadonnées)
    PainGenerationResult generate(List<T> requests, String messageId);

    // Écriture directe dans un stream
    void generateToStream(List<T> requests, String messageId, OutputStream out);

    // Écriture directe dans un fichier (répertoires parents créés automatiquement)
    void generateToFile(List<T> requests, String messageId, Path outputPath);
}
```

---

## Auto-configuration Spring Boot

Deux beans nommés sont toujours exposés simultanement (pas de `default-version`).
Le consommateur choisit sa version via `@Qualifier`.

```java
@AutoConfiguration
@ConditionalOnProperty(prefix = "mg-bank.pain", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PainProperties.class)
public class PainAutoConfiguration {

    @Bean("pain001V03Generator")
    public PainGenerator<SepaTransferRequest> pain001V03Generator(PainProperties properties) {
        return new Pain001V03Generator(properties);
    }

    @Bean("pain001V09Generator")
    public PainGenerator<SepaTransferRequest> pain001V09Generator(PainProperties properties) {
        return new Pain001V09Generator(properties);
    }
}
```

### Pourquoi des beans nommés plutôt qu'un enum version ?

Un enum `Pain001Version { V03, V09 }` aurait forcé à ajouter une valeur à chaque
nouveau format (pain.008, camt.*). Les beans nommés sont extensibles sans toucher
aux classes existantes — chaque nouveau format ajoute simplement un `@Bean` dans
l'auto-configuration.

---

## Différences pain.001.001.03 vs pain.001.001.09

| Élément | V03 | V09 |
|---------|-----|-----|
| Classe document | `CustomerCreditTransferInitiationV03` | `CustomerCreditTransferInitiationV09` |
| GroupHeader | `GroupHeader32` | `GroupHeader85` |
| PaymentInstruction | `PaymentInstruction3` | `PaymentInstruction30` |
| CreditTransferTransaction | `CreditTransferTransactionInformation10` | `CreditTransferTransaction34` |
| ReqdExctnDt | `XMLGregorianCalendar` (date simple) | `DateAndDateTime2Choice` (date ou dateTime) |
| Identification BIC | `BIC` dans `FinancialInstitutionIdentification5` | `BICFI` dans `FinancialInstitutionIdentification18` |
| Namespace XSD | `urn:iso:std:iso:20022:tech:xsd:pain.001.001.03` | `urn:iso:std:iso:20022:tech:xsd:pain.001.001.09` |

---

## Structure du projet

```
mg-bank-pain-format/
├── src/main/java/mg/pain/
│   ├── autoconfigure/
│   │   ├── PainAutoConfiguration.java    # @AutoConfiguration — expose les beans nommés
│   │   └── PainProperties.java           # binding mg-bank.pain.*
│   ├── generator/
│   │   ├── PainGenerator.java            # interface générique <T>
│   │   ├── pain001/
│   │   │   ├── Pain001V03Generator.java  # pain.001.001.03
│   │   │   └── Pain001V09Generator.java  # pain.001.001.09
│   │   └── pain008/                      # Phase 2
│   │       ├── Pain008V02Generator.java  # pain.008.001.02 (SDD Core)
│   │       └── Pain008V03Generator.java  # pain.008.001.03 (SDD B2B)
│   ├── domain/
│   │   ├── SepaTransferRequest.java      # DTO entrée pain.001
│   │   ├── SepaDirectDebitRequest.java   # DTO entrée pain.008 (Phase 2)
│   │   └── PainGenerationResult.java     # DTO sortie commune
│   ├── jaxb/
│   │   ├── pain001v03/                   # classes JAXB pain.001.001.03
│   │   ├── pain001v09/                   # classes JAXB pain.001.001.09
│   │   └── pain008v02/                   # classes JAXB pain.008.001.02 (Phase 2)
│   ├── util/
│   │   ├── XmlCalendarUtils.java         # helpers XMLGregorianCalendar
│   │   └── SepaStringUtils.java          # normalisation (troncature, nettoyage)
│   └── exception/
│       └── PainGenerationException.java  # exception runtime du starter
├── src/main/resources/META-INF/spring/
│   └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
├── src/test/java/mg/pain/
│   └── Pain001V03GeneratorTest.java
├── README.md
└── pom.xml
```

---

## Corrections apportées vs pain.001 dans ClemsApp

| # | Problème original | Correction dans le starter |
|---|------------------|---------------------------|
| 1 | Couplage à `InvoiceGetResponseDTO` | DTO neutre `SepaTransferRequest` |
| 2 | Sortie uniquement fichier sur disque | byte[], OutputStream, fichier au choix |
| 3 | `JAXBContext` recréé à chaque appel | `static final JAXBContext` (singleton) |
| 4 | Une seule version pain.001.001.03 | Beans nommés — V03 et V09 simultanément disponibles |
| 5 | Paramètre `insee` mal nommé | `remittanceInfo` (sémantique claire) |
| 6 | Encodage ISO-8859-1 hardcodé | Configurable via `mg-bank.pain.output.charset` |

---

## Feuille de route

| Version | Contenu |
|---------|---------|
| **1.0.0** | pain.001.001.03, pain.001.001.09, auto-configuration, publication Maven Central |
| 1.1.0 | pain.002.001.03 (Status Report), pain.007.001.02 (Reversal) |
| 1.2.0 | pain.008.001.02 (SDD Core), pain.008.001.03 (SDD B2B) |
| 2.0.0 | Support camt.\*, pacs.\* — renommage éventuel en `mg-bank-iso20022-format` |
