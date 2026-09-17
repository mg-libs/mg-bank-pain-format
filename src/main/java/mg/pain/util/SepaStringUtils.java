package mg.pain.util;

/**
 * Utilitaires de normalisation des chaînes pour les fichiers ISO 20022 SEPA.
 */
public final class SepaStringUtils {

    private SepaStringUtils() {}

    /**
     * Tronque une chaîne à la longueur maximale autorisée.
     * Retourne null si la valeur est null.
     */
    public static String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    /**
     * Normalise un nom pour SEPA : trim, suppression des caractères non conformes.
     * Tronque à 70 caractères (limite SEPA pour les noms).
     */
    public static String normalizeName(String value) {
        if (value == null || value.isBlank()) return "";
        return truncate(value.trim(), 70);
    }

    /**
     * Normalise un libellé de virement (RemittanceInformation).
     * Tronque à 140 caractères (limite SEPA).
     */
    public static String normalizeRemittanceInfo(String value) {
        if (value == null || value.isBlank()) return "";
        return truncate(value.trim(), 140);
    }

    /**
     * Normalise un MessageId ou EndToEndId.
     * Tronque à 35 caractères (limite ISO 20022).
     */
    public static String normalizeId(String value) {
        if (value == null || value.isBlank()) return "";
        return truncate(value.trim(), 35);
    }
}
