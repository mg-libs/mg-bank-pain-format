package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceLevel8Choice", propOrder = {"cd", "prtry"})
public class ServiceLevel8Choice {

    @XmlElement(name = "Cd")
    protected String cd;

    @XmlElement(name = "Prtry")
    protected String prtry;

    public String getCd() { return cd; }
    public void setCd(String value) { this.cd = value; }

    public String getPrtry() { return prtry; }
    public void setPrtry(String value) { this.prtry = value; }
}
