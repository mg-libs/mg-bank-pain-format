package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountIdentification4Choice", propOrder = {"iban"})
public class AccountIdentification4Choice {

    @XmlElement(name = "IBAN")
    protected String iban;

    public String getIBAN() { return iban; }
    public void setIBAN(String value) { this.iban = value; }
}
