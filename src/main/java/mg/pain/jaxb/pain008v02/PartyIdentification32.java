package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentification32", propOrder = {"nm", "pstlAdr"})
public class PartyIdentification32 {

    @XmlElement(name = "Nm")
    protected String nm;

    @XmlElement(name = "PstlAdr")
    protected PostalAddress6 pstlAdr;

    public String getNm() { return nm; }
    public void setNm(String value) { this.nm = value; }

    public PostalAddress6 getPstlAdr() { return pstlAdr; }
    public void setPstlAdr(PostalAddress6 value) { this.pstlAdr = value; }
}
