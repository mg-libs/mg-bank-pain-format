package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocalInstrument2Choice", propOrder = {"cd", "prtry"})
public class LocalInstrument2Choice {

    /** Code instrument local SEPA : CORE ou B2B. */
    @XmlElement(name = "Cd")
    protected String cd;

    @XmlElement(name = "Prtry")
    protected String prtry;

    public String getCd() { return cd; }
    public void setCd(String value) { this.cd = value; }

    public String getPrtry() { return prtry; }
    public void setPrtry(String value) { this.prtry = value; }
}
