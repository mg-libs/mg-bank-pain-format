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

    // ---- Adresse postale du bénéficiaire (PstlAdr) ----
    // Incluse dans le XML uniquement si creditorCountry ET creditorTownName sont renseignés.
    // Le format (HYBRID / STRUCTURED) est un paramètre global défini dans PainProperties.

    /** Code pays ISO alpha-2 (ex : "FR") — obligatoire si PstlAdr présent. */
    private String creditorCountry;

    /** Ville (max 35 caractères) — obligatoire si PstlAdr présent. */
    private String creditorTownName;

    /** Code postal (optionnel, max 16 caractères). */
    private String creditorPostalCode;

    // Mode HYBRID
    /** Première ligne d'adresse libre (AdrLine, max 70 caractères). */
    private String creditorAddressLine1;
    /** Deuxième ligne d'adresse libre (AdrLine, max 70 caractères). */
    private String creditorAddressLine2;

    // Mode STRUCTURED
    /** Nom de la rue (StrtNm, max 70 caractères). */
    private String creditorStreetName;
    /** Numéro de bâtiment (BldgNb, max 16 caractères). */
    private String creditorBuildingNumber;

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

    public String getCreditorCountry() { return creditorCountry; }
    public void setCreditorCountry(String creditorCountry) { this.creditorCountry = creditorCountry; }

    public String getCreditorTownName() { return creditorTownName; }
    public void setCreditorTownName(String creditorTownName) { this.creditorTownName = creditorTownName; }

    public String getCreditorPostalCode() { return creditorPostalCode; }
    public void setCreditorPostalCode(String creditorPostalCode) { this.creditorPostalCode = creditorPostalCode; }

    public String getCreditorAddressLine1() { return creditorAddressLine1; }
    public void setCreditorAddressLine1(String creditorAddressLine1) { this.creditorAddressLine1 = creditorAddressLine1; }

    public String getCreditorAddressLine2() { return creditorAddressLine2; }
    public void setCreditorAddressLine2(String creditorAddressLine2) { this.creditorAddressLine2 = creditorAddressLine2; }

    public String getCreditorStreetName() { return creditorStreetName; }
    public void setCreditorStreetName(String creditorStreetName) { this.creditorStreetName = creditorStreetName; }

    public String getCreditorBuildingNumber() { return creditorBuildingNumber; }
    public void setCreditorBuildingNumber(String creditorBuildingNumber) { this.creditorBuildingNumber = creditorBuildingNumber; }
}
