package mg.pain.autoconfigure;

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

    private final Debtor debtor = new Debtor();
    private final Creditor creditor = new Creditor();
    private final Output output = new Output();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

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

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getIban() { return iban; }
        public void setIban(String iban) { this.iban = iban; }

        public String getBic() { return bic; }
        public void setBic(String bic) { this.bic = bic; }

        public String getCreditorId() { return creditorId; }
        public void setCreditorId(String creditorId) { this.creditorId = creditorId; }
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
