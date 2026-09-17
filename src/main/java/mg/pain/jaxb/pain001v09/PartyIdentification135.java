package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentification135", propOrder = {"nm"})
public class PartyIdentification135 {

    @XmlElement(name = "Nm")
    protected String nm;

    public String getNm() { return nm; }
    public void setNm(String value) { this.nm = value; }
}
