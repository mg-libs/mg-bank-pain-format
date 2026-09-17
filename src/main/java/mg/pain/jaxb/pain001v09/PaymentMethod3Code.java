package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "PaymentMethod3Code")
@XmlEnum
public enum PaymentMethod3Code {
    CHK, TRF, TRA;

    public String value() { return name(); }
    public static PaymentMethod3Code fromValue(String v) { return valueOf(v); }
}
