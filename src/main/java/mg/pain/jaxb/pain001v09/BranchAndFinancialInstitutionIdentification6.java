package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BranchAndFinancialInstitutionIdentification6", propOrder = {"finInstnId"})
public class BranchAndFinancialInstitutionIdentification6 {

    @XmlElement(name = "FinInstnId", required = true)
    protected FinancialInstitutionIdentification18 finInstnId;

    public FinancialInstitutionIdentification18 getFinInstnId() { return finInstnId; }
    public void setFinInstnId(FinancialInstitutionIdentification18 value) { this.finInstnId = value; }
}
