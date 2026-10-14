package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentification135", propOrder = {"nm", "pstlAdr"})
public class PartyIdentification135 {

    @XmlElement(name = "Nm")
    protected String nm;

    @XmlElement(name = "PstlAdr")
    protected PostalAddress24 pstlAdr;

    public String getNm() { return nm; }
    public void setNm(String value) { this.nm = value; }

    public PostalAddress24 getPstlAdr() { return pstlAdr; }
    public void setPstlAdr(PostalAddress24 value) { this.pstlAdr = value; }
}
