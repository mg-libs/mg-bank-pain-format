package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "SequenceType3Code")
@XmlEnum
public enum SequenceType3Code {

    FRST, RCUR, FNAL, OOFF;

    public static SequenceType3Code fromValue(String value) {
        return valueOf(value);
    }
}
