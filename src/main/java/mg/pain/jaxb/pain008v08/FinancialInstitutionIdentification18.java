package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitutionIdentification18", propOrder = {"bicfi"})
public class FinancialInstitutionIdentification18 {

    /** BICFI remplace BIC à partir de pain.008.001.08. */
    @XmlElement(name = "BICFI")
    protected String bicfi;

    public String getBICFI() { return bicfi; }
    public void setBICFI(String value) { this.bicfi = value; }
}
