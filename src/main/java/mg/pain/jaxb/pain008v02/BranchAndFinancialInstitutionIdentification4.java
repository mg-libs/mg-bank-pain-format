package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BranchAndFinancialInstitutionIdentification4", propOrder = {"finInstnId"})
public class BranchAndFinancialInstitutionIdentification4 {

    @XmlElement(name = "FinInstnId", required = true)
    protected FinancialInstitutionIdentification7 finInstnId;

    public FinancialInstitutionIdentification7 getFinInstnId() { return finInstnId; }
    public void setFinInstnId(FinancialInstitutionIdentification7 value) { this.finInstnId = value; }
}
