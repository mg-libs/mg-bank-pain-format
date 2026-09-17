package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentification32", propOrder = {"nm"})
public class PartyIdentification32 {

    @XmlElement(name = "Nm")
    protected String nm;

    public String getNm() { return nm; }
    public void setNm(String value) { this.nm = value; }
}
