package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitutionIdentification18", propOrder = {"bicfi"})
public class FinancialInstitutionIdentification18 {

    @XmlElement(name = "BICFI")
    protected String bicfi;

    public String getBICFI() { return bicfi; }
    public void setBICFI(String value) { this.bicfi = value; }
}
