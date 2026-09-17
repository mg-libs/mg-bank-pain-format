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
}
