package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Type de séquence de prélèvement SEPA.
 * <ul>
 *   <li>FRST — premier prélèvement sur un mandat</li>
 *   <li>RCUR — prélèvement récurrent</li>
 *   <li>OOFF — prélèvement ponctuel (one-off)</li>
 *   <li>FNAL — dernier prélèvement sur un mandat</li>
 * </ul>
 */
@XmlType(name = "SequenceType3Code")
@XmlEnum
public enum SequenceType3Code {
    FRST, RCUR, OOFF, FNAL;

    public String value() { return name(); }
    public static SequenceType3Code fromValue(String v) { return valueOf(v); }
}
