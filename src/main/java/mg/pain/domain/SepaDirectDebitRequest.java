package mg.pain.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO d'entrée neutre pour la génération d'un prélèvement SEPA (pain.008).
 * Sans dépendance au domaine du consommateur.
 *
 * <p>Le {@code collectionDate} et le {@code sequenceType} s'appliquent au niveau
 * du bloc {@code PmtInf}. Toutes les requêtes d'un même appel à {@code generate()}
 * doivent partager les mêmes valeurs pour ces deux champs.
 */
public class SepaDirectDebitRequest {

    /** Identifiant bout-en-bout unique (EndToEndId) — max 35 caractères. */
    private String endToEndId;

    /** Montant — doit être strictement positif. */
    private BigDecimal amount;

    /** Devise (défaut : EUR). */
    private String currency = "EUR";

    /** Nom du débiteur (celui dont le compte est prélevé) — max 70 caractères. */
    private String debtorName;

    /** IBAN du débiteur. */
    private String debtorIban;

    /** BIC/SWIFT de la banque du débiteur. */
    private String debtorBic;

    /** Libellé du prélèvement (RemittanceInformation) — max 140 caractères. */
    private String remittanceInfo;

    /** Référence unique du mandat de prélèvement (MndtId) — max 35 caractères. */
    private String mandateId;

    /** Date de signature du mandat (DtOfSgntr). */
    private LocalDate mandateDate;

    /**
     * Date souhaitée de prélèvement (ReqdColltnDt) — niveau PmtInf.
     * Doit être identique pour toutes les requêtes d'un même appel.
     */
    private LocalDate collectionDate;

    /**
     * Type de séquence (SeqTp) — niveau PmtInf.
     * Valeurs SEPA : FRST (premier), RCUR (récurrent), OOFF (ponctuel), FNAL (dernier).
     * Doit être identique pour toutes les requêtes d'un même appel.
     */
    private String sequenceType;

    // ---- Adresse postale du débiteur (PstlAdr) ----
    // Incluse dans le XML uniquement si debtorCountry ET debtorTownName sont renseignés.
    // Le format (HYBRID / STRUCTURED) est un paramètre global défini dans PainProperties.

    /** Code pays ISO alpha-2 (ex : "FR") — obligatoire si PstlAdr présent. */
    private String debtorCountry;

    /** Ville (max 35 caractères) — obligatoire si PstlAdr présent. */
    private String debtorTownName;

    /** Code postal (optionnel, max 16 caractères). */
    private String debtorPostalCode;

    // Mode HYBRID
    /** Première ligne d'adresse libre (AdrLine, max 70 caractères). */
    private String debtorAddressLine1;
    /** Deuxième ligne d'adresse libre (AdrLine, max 70 caractères). */
    private String debtorAddressLine2;

    // Mode STRUCTURED
    /** Nom de la rue (StrtNm, max 70 caractères). */
    private String debtorStreetName;
    /** Numéro de bâtiment (BldgNb, max 16 caractères). */
    private String debtorBuildingNumber;

    // ---- Créancier final (UltmtCdtr) ----

    /** Nom du créancier final si différent du créancier SEPA (optionnel, max 70 caractères). */
    private String ultimateCreditorName;

    public SepaDirectDebitRequest() {}

    public SepaDirectDebitRequest(String endToEndId, BigDecimal amount, String currency,
                                  String debtorName, String debtorIban, String debtorBic,
                                  String remittanceInfo, String mandateId, LocalDate mandateDate,
                                  LocalDate collectionDate, String sequenceType) {
        this.endToEndId = endToEndId;
        this.amount = amount;
        this.currency = currency;
        this.debtorName = debtorName;
        this.debtorIban = debtorIban;
        this.debtorBic = debtorBic;
        this.remittanceInfo = remittanceInfo;
        this.mandateId = mandateId;
        this.mandateDate = mandateDate;
        this.collectionDate = collectionDate;
        this.sequenceType = sequenceType;
    }

    public String getEndToEndId() { return endToEndId; }
    public void setEndToEndId(String endToEndId) { this.endToEndId = endToEndId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDebtorName() { return debtorName; }
    public void setDebtorName(String debtorName) { this.debtorName = debtorName; }

    public String getDebtorIban() { return debtorIban; }
    public void setDebtorIban(String debtorIban) { this.debtorIban = debtorIban; }

    public String getDebtorBic() { return debtorBic; }
    public void setDebtorBic(String debtorBic) { this.debtorBic = debtorBic; }

    public String getRemittanceInfo() { return remittanceInfo; }
    public void setRemittanceInfo(String remittanceInfo) { this.remittanceInfo = remittanceInfo; }

    public String getMandateId() { return mandateId; }
    public void setMandateId(String mandateId) { this.mandateId = mandateId; }

    public LocalDate getMandateDate() { return mandateDate; }
    public void setMandateDate(LocalDate mandateDate) { this.mandateDate = mandateDate; }

    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }

    public String getSequenceType() { return sequenceType; }
    public void setSequenceType(String sequenceType) { this.sequenceType = sequenceType; }

    public String getDebtorCountry() { return debtorCountry; }
    public void setDebtorCountry(String debtorCountry) { this.debtorCountry = debtorCountry; }

    public String getDebtorTownName() { return debtorTownName; }
    public void setDebtorTownName(String debtorTownName) { this.debtorTownName = debtorTownName; }

    public String getDebtorPostalCode() { return debtorPostalCode; }
    public void setDebtorPostalCode(String debtorPostalCode) { this.debtorPostalCode = debtorPostalCode; }

    public String getDebtorAddressLine1() { return debtorAddressLine1; }
    public void setDebtorAddressLine1(String debtorAddressLine1) { this.debtorAddressLine1 = debtorAddressLine1; }

    public String getDebtorAddressLine2() { return debtorAddressLine2; }
    public void setDebtorAddressLine2(String debtorAddressLine2) { this.debtorAddressLine2 = debtorAddressLine2; }

    public String getDebtorStreetName() { return debtorStreetName; }
    public void setDebtorStreetName(String debtorStreetName) { this.debtorStreetName = debtorStreetName; }

    public String getDebtorBuildingNumber() { return debtorBuildingNumber; }
    public void setDebtorBuildingNumber(String debtorBuildingNumber) { this.debtorBuildingNumber = debtorBuildingNumber; }

    public String getUltimateCreditorName() { return ultimateCreditorName; }
    public void setUltimateCreditorName(String ultimateCreditorName) { this.ultimateCreditorName = ultimateCreditorName; }
}
