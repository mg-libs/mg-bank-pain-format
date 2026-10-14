package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocalInstrument2Choice", propOrder = {"cd"})
public class LocalInstrument2Choice {

    @XmlElement(name = "Cd")
    protected String cd;

    public String getCd() { return cd; }
    public void setCd(String value) { this.cd = value; }
}
