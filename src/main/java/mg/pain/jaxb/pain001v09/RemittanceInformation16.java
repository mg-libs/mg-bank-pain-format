package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemittanceInformation16", propOrder = {"ustrd"})
public class RemittanceInformation16 {

    @XmlElement(name = "Ustrd")
    protected String ustrd;

    public String getUstrd() { return ustrd; }
    public void setUstrd(String value) { this.ustrd = value; }
}
