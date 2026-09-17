package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitutionIdentification7", propOrder = {"bic"})
public class FinancialInstitutionIdentification7 {

    @XmlElement(name = "BIC")
    protected String bic;

    public String getBIC() { return bic; }
    public void setBIC(String value) { this.bic = value; }
}
