package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ChargeBearerType1Code")
@XmlEnum
public enum ChargeBearerType1Code {
    DEBT, CRED, SHAR, SLEV;

    public String value() { return name(); }
    public static ChargeBearerType1Code fromValue(String v) { return valueOf(v); }
}
