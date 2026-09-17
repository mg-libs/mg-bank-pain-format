package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemittanceInformation5", propOrder = {"ustrd"})
public class RemittanceInformation5 {

    @XmlElement(name = "Ustrd")
    protected String ustrd;

    public String getUstrd() { return ustrd; }
    public void setUstrd(String value) { this.ustrd = value; }
}
