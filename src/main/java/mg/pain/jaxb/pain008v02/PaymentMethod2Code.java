package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/** Méthode de paiement pour pain.008 — toujours DD (Direct Debit). */
@XmlType(name = "PaymentMethod2Code")
@XmlEnum
public enum PaymentMethod2Code {
    DD;

    public String value() { return name(); }
    public static PaymentMethod2Code fromValue(String v) { return valueOf(v); }
}
