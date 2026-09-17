package mg.pain.domain;

import java.math.BigDecimal;

/**
 * DTO d'entrée neutre pour la génération d'un virement SEPA (pain.001).
 * Sans dépendance au domaine du consommateur.
 */
public class SepaTransferRequest {

    /** Identifiant bout-en-bout unique (EndToEndId) — max 35 caractères. */
    private String endToEndId;

    /** Montant du virement — doit être strictement positif. */
    private BigDecimal amount;

    /** Devise (défaut : EUR). */
    private String currency = "EUR";

    /** Nom du bénéficiaire — max 70 caractères. */
    private String creditorName;

    /** IBAN du bénéficiaire. */
    private String creditorIban;

    /** BIC/SWIFT du bénéficiaire. */
    private String creditorBic;

    /** Libellé du virement (RemittanceInformation) — max 140 caractères. */
    private String remittanceInfo;

    public SepaTransferRequest() {}

    public SepaTransferRequest(String endToEndId, BigDecimal amount, String currency,
                               String creditorName, String creditorIban,
                               String creditorBic, String remittanceInfo) {
        this.endToEndId = endToEndId;
        this.amount = amount;
        this.currency = currency;
        this.creditorName = creditorName;
        this.creditorIban = creditorIban;
        this.creditorBic = creditorBic;
        this.remittanceInfo = remittanceInfo;
    }

    public String getEndToEndId() { return endToEndId; }
    public void setEndToEndId(String endToEndId) { this.endToEndId = endToEndId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCreditorName() { return creditorName; }
    public void setCreditorName(String creditorName) { this.creditorName = creditorName; }

    public String getCreditorIban() { return creditorIban; }
    public void setCreditorIban(String creditorIban) { this.creditorIban = creditorIban; }

    public String getCreditorBic() { return creditorBic; }
    public void setCreditorBic(String creditorBic) { this.creditorBic = creditorBic; }

    public String getRemittanceInfo() { return remittanceInfo; }
    public void setRemittanceInfo(String remittanceInfo) { this.remittanceInfo = remittanceInfo; }
}
