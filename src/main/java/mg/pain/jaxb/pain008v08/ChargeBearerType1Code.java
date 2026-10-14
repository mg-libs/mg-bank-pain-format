package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ChargeBearerType1Code")
@XmlEnum
public enum ChargeBearerType1Code {
    DEBT, CRED, SHAR, SLEV;
}
