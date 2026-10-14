package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceLevel8Choice", propOrder = {"cd"})
public class ServiceLevel8Choice {

    @XmlElement(name = "Cd")
    protected String cd;

    public String getCd() { return cd; }
    public void setCd(String value) { this.cd = value; }
}
