package mg.pain.autoconfigure;

import mg.pain.domain.AddressFormat;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration du starter mg-bank-pain-format.
 *
 * <pre>
 * mg-bank:
 *   pain:
 *     enabled: true
 *     debtor:
 *       name: "MA SOCIÉTÉ SAS"
 *       iban: "FR7630004000031234567890143"
 *       bic: "BNPAFRPPXXX"
 *     output:
 *       charset: "UTF-8"
 * </pre>
 *
 * Tous les générateurs disponibles sont exposés comme beans Spring nommés.
 * Le consommateur injecte celui dont il a besoin via @Qualifier :
 * <ul>
 *   <li>@Qualifier("pain001V03Generator") — pain.001.001.03 (virement SEPA)</li>
 *   <li>@Qualifier("pain001V09Generator") — pain.001.001.09 (virement SEPA Instant)</li>
 *   <li>@Qualifier("pain008V02CoreGenerator") — pain.008.001.02 SDD Core (prélèvement)</li>
 *   <li>@Qualifier("pain008V02B2bGenerator")  — pain.008.001.02 SDD B2B (prélèvement interentreprises)</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "mg-bank.pain")
public class PainProperties {

    /** Active ou désactive le starter. Défaut : true. */
    private boolean enabled = true;

    /**
     * Format global des adresses postales (PstlAdr) dans les fichiers générés.
     * S'applique à tous les générateurs (pain.001 et pain.008).
     * Si null, aucune adresse n'est générée même si les champs sont renseignés.
     * Valeurs : HYBRID (AdrLine + TwnNm + Ctry) ou STRUCTURED (StrtNm + BldgNb + PstCd + TwnNm + Ctry).
     */
    private AddressFormat addressFormat;

    private final Debtor debtor = new Debtor();
    private final Creditor creditor = new Creditor();
    private final Output output = new Output();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public AddressFormat getAddressFormat() { return addressFormat; }
    public void setAddressFormat(AddressFormat addressFormat) { this.addressFormat = addressFormat; }

    public Debtor getDebtor() { return debtor; }
    public Creditor getCreditor() { return creditor; }
    public Output getOutput() { return output; }

    /**
     * Informations du créancier (collecteur de prélèvements — pain.008).
     * Le créancier est l'organisme qui initie le prélèvement SEPA.
     */
    public static class Creditor {

        /** Nom du créancier. */
        private String name;

        /** IBAN du compte créancier. */
        private String iban;

        /** BIC/SWIFT de la banque du créancier. */
        private String bic;

        /**
         * Identifiant Créancier SEPA (ICS) — obligatoire pour SDD Core.
         * Format : 2 lettres pays + 2 chiffres contrôle + 3 lettres id type + identifiant national.
         * Ex: FR72ZZZ123456
         */
        private String creditorId;

        /**
         * Écriture groupée (BtchBookg) dans pain.008.
         * true  = la banque passe une seule écriture globale sur le relevé.
         * false = la banque passe une écriture par transaction (défaut, meilleure traçabilité).
         */
        private boolean batchBooking = false;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getIban() { return iban; }
        public void setIban(String iban) { this.iban = iban; }

        public String getBic() { return bic; }
        public void setBic(String bic) { this.bic = bic; }

        public String getCreditorId() { return creditorId; }
        public void setCreditorId(String creditorId) { this.creditorId = creditorId; }

        public boolean isBatchBooking() { return batchBooking; }
        public void setBatchBooking(boolean batchBooking) { this.batchBooking = batchBooking; }

        // ---- Adresse postale du créancier (PstlAdr dans pain.008 PmtInf/Cdtr) ----

        private String country;
        private String townName;
        private String postalCode;
        private String addressLine1;
        private String addressLine2;
        private String streetName;
        private String buildingNumber;

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getTownName() { return townName; }
        public void setTownName(String townName) { this.townName = townName; }

        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

        public String getAddressLine2() { return addressLine2; }
        public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

        public String getStreetName() { return streetName; }
        public void setStreetName(String streetName) { this.streetName = streetName; }

        public String getBuildingNumber() { return buildingNumber; }
        public void setBuildingNumber(String buildingNumber) { this.buildingNumber = buildingNumber; }
    }

    /** Informations du débiteur (émetteur des virements — pain.001). */
    public static class Debtor {

        /** Nom de l'organisme émetteur. */
        private String name;

        /** IBAN du compte débiteur. */
        private String iban;

        /** BIC/SWIFT de la banque du débiteur. */
        private String bic;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getIban() { return iban; }
        public void setIban(String iban) { this.iban = iban; }

        public String getBic() { return bic; }
        public void setBic(String bic) { this.bic = bic; }
    }

    /** Paramètres de sortie du fichier XML généré. */
    public static class Output {

        /** Encodage du fichier XML. Défaut : UTF-8. */
        private String charset = "UTF-8";

        public String getCharset() { return charset; }
        public void setCharset(String charset) { this.charset = charset; }
    }
}
