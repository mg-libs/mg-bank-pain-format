package mg.pain.domain;

/**
 * Mode de représentation de l'adresse postale (PstlAdr) dans les fichiers ISO 20022.
 *
 * <p>ISO 20022 distingue trois modes, dont deux sont supportés ici :
 * <ul>
 *   <li>{@link #HYBRID}     — rue et bâtiment en lignes libres (AdrLine),
 *       ville et pays en champs structurés.</li>
 *   <li>{@link #STRUCTURED} — chaque composant dans son propre champ XML dédié.</li>
 * </ul>
 *
 * <p>Dans les deux cas, {@code TwnNm} (ville) et {@code Ctry} (pays) sont
 * obligatoires dès que l'adresse est présente.
 */
public enum AddressFormat {

    /**
     * Mode hybride : adresse libre sur 1 ou 2 lignes (AdrLine), complétée par
     * les champs structurés PstCd, TwnNm et Ctry.
     */
    HYBRID,

    /**
     * Mode entièrement structuré : StrtNm, BldgNb, PstCd, TwnNm, Ctry —
     * aucune ligne libre (AdrLine).
     */
    STRUCTURED
}
