# mg-bank-pain-format-spring-boot-starter

Spring Boot Starter pour la génération de fichiers **ISO 20022 Payment Initiation (pain.\*)**.

Ajoute une seule dépendance et génère des fichiers de virement et prélèvement SEPA conformes aux standards bancaires. Le starter ne stocke rien, n'initie aucun paiement, et n'a aucun couplage au domaine métier du consommateur.

---

## Beans disponibles

| Bean Spring | Format | Usage |
|-------------|--------|-------|
| `pain001V03Generator` | pain.001.001.03 | Virement SEPA Credit Transfer |
| `pain001V09Generator` | pain.001.001.09 | Virement SEPA Instant (SCT Inst) |
| `pain008V02CoreGenerator` | pain.008.001.02 | Prélèvement SDD Core (legacy) |
| `pain008V02B2bGenerator` | pain.008.001.02 | Prélèvement SDD B2B (legacy) |
| `pain008V08CoreGenerator` | pain.008.001.08 | Prélèvement SDD Core (version courante) |
| `pain008V08B2bGenerator` | pain.008.001.08 | Prélèvement SDD B2B (version courante) |

---

## Installation

### Prérequis

- Java 21+
- Spring Boot 3.x

### Option A — Maven Central

```xml
<dependency>
    <groupId>io.github.mg-libs</groupId>
    <artifactId>mg-bank-pain-format-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Option B — Installation locale (développement)

```bash
mvn clean install -DskipTests
```

---

## Configuration

```yaml
mg-bank:
  pain:
    enabled: true          # true par défaut — false pour désactiver tous les beans

    # Format d'adresse postale (global — s'applique à toutes les adresses du fichier)
    # HYBRID     : AdrLine (1-2 lignes libres) + PstCd + TwnNm + Ctry
    # STRUCTURED : StrtNm + BldgNb + PstCd + TwnNm + Ctry
    # Absent     : aucune adresse générée
    address-format: HYBRID

    # Pour pain.001 (virements) — compte émetteur
    debtor:
      name: "MA SOCIÉTÉ SAS"
      iban: "FR7630004000031234567890143"
      bic:  "BNPAFRPPXXX"
      # Adresse (optionnel — générée si country + town-name renseignés)
      country:       "FR"
      town-name:     "PARIS"
      postal-code:   "75001"
      address-line1: "12 RUE DE LA PAIX"   # mode HYBRID
      # street-name:     "RUE DE LA PAIX"  # mode STRUCTURED
      # building-number: "12"              # mode STRUCTURED

    # Pour pain.008 (prélèvements) — compte collecteur
    creditor:
      name: "MA SOCIÉTÉ SAS"
      iban: "FR7630004000031234567890143"
      bic:  "BNPAFRPPXXX"
      creditor-id:   "FR72ZZZ123456"   # Identifiant Créancier SEPA (ICS)
      batch-booking: false             # false = un mouvement par transaction sur le relevé
      # Adresse (optionnel)
      country:       "FR"
      town-name:     "PARIS"
      postal-code:   "75001"
      address-line1: "12 RUE DE LA PAIX"

    output:
      charset: "UTF-8"     # UTF-8 par défaut — certaines banques exigent ISO-8859-1
```

---

## Utilisation — pain.008 (prélèvement SEPA)

```java
@Service
public class PrelevementService {

    // Choisir via @Qualifier :
    //   "pain008V08CoreGenerator" (recommandé)  "pain008V08B2bGenerator"
    //   "pain008V02CoreGenerator" (legacy)       "pain008V02B2bGenerator" (legacy)
    public PrelevementService(
            @Qualifier("pain008V08CoreGenerator") PainGenerator<SepaDirectDebitRequest> generator) {
        this.generator = generator;
    }

    public byte[] genererPrelevement(List<MonPrelevement> prelevements, LocalDate dateExecution) {
        List<SepaDirectDebitRequest> requests = prelevements.stream()
                .map(p -> {
                    SepaDirectDebitRequest req = new SepaDirectDebitRequest(
                            p.getReference(),
                            p.getMontant(), "EUR",
                            p.getNomDebiteur(), p.getIbanDebiteur(), p.getBicDebiteur(),
                            p.getLibelle(),
                            p.getReferenceMandat(), p.getDateSignatureMandat(),
                            dateExecution, "RCUR"   // FRST | RCUR | OOFF | FNAL
                    );
                    // Adresse du débiteur (optionnel)
                    req.setDebtorCountry("FR");
                    req.setDebtorTownName(p.getVille());
                    req.setDebtorAddressLine1(p.getAdresse());  // HYBRID
                    // Créancier final si différent du créancier SEPA (optionnel)
                    req.setUltimateCreditorName(p.getBeneficiaireEffectif());
                    return req;
                })
                .toList();

        String messageId = "SDD-" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8);
        return generator.generate(requests, messageId).getContent();
    }
}
```

> **Important :** toutes les requêtes d'un même appel doivent partager le même `collectionDate`
> et le même `sequenceType` — ce sont des champs de niveau lot (`PmtInf`) dans le schéma SEPA.

---

## Utilisation — pain.001 (virement SEPA)

```java
@Service
public class VirementService {

    // "pain001V03Generator" ou "pain001V09Generator"
    public VirementService(
            @Qualifier("pain001V03Generator") PainGenerator<SepaTransferRequest> generator) {
        this.generator = generator;
    }
}
```

```java
List<SepaTransferRequest> requests = List.of(
    new SepaTransferRequest(
        "REF-2026-001",               // endToEndId (max 35 cars)
        new BigDecimal("150.00"),
        "EUR",
        "DUPONT JEAN",                // nom du bénéficiaire (max 70 cars)
        "FR7630006000011234567890189", // IBAN
        "AGRIFRPP882",                // BIC
        "Remboursement facture 2026-001" // libellé (max 140 cars)
    )
);
```

---

## Trois modes de sortie

```java
String messageId = "MSG-" + System.currentTimeMillis();

// 1. byte[] — stockage BDD, réponse HTTP
PainGenerationResult result = generator.generate(requests, messageId);
byte[] xml = result.getContent();

// 2. OutputStream — streaming HTTP, ZIP
generator.generateToStream(requests, messageId, response.getOutputStream());

// 3. Path — écriture sur disque
generator.generateToFile(requests, messageId, Path.of("/exports/virement.xml"));
```

```java
result.getContent();          // byte[]     — contenu XML
result.getFilename();         // String     — ex: "virements_MSG001.xml"
result.getMessageId();        // String     — messageId utilisé dans le fichier
result.getTransactionCount(); // int        — nombre de transactions
result.getControlSum();       // BigDecimal — somme de contrôle
```

---

## Normalisation automatique

| Champ | Limite | Comportement |
|-------|--------|-------------|
| `endToEndId`, `mandateId`, `messageId` | 35 caractères | Tronqué |
| `creditorName`, `debtorName`, `ultimateCreditorName` | 70 caractères | Tronqué |
| `remittanceInfo` | 140 caractères | Tronqué |
| `townName`, `addressLine1/2`, `streetName` | 35 / 70 / 70 caractères | Tronqué |
| `postalCode`, `buildingNumber` | 16 caractères | Tronqué |

---

## Désactiver le starter

```yaml
mg-bank:
  pain:
    enabled: false
```

---

## Feuille de route

| Version | Contenu |
|---------|---------|
| **1.0.0** | pain.001.001.03, pain.001.001.09, pain.008.001.02 (CORE + B2B) |
| **1.1.0** | pain.008.001.08 (CORE + B2B), adresses postales (HYBRID / STRUCTURED), BtchBookg configurable, UltmtCdtr |
| 1.2.0 | pain.002.001.03 (Status Report), pain.007.001.02 (Reversal) |
| 2.0.0 | Support camt.\*, pacs.\* |

---

## Licence

Apache License 2.0 — voir [LICENSE](LICENSE).
