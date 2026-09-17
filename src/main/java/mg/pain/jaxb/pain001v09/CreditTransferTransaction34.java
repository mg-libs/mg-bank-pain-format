package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditTransferTransaction34", propOrder = {
    "pmtId", "amt", "cdtrAgt", "cdtr", "cdtrAcct", "rmtInf"
})
public class CreditTransferTransaction34 {

    @XmlElement(name = "PmtId", required = true)
    protected PaymentIdentification6 pmtId;

    @XmlElement(name = "Amt", required = true)
    protected AmountType3Choice amt;

    @XmlElement(name = "CdtrAgt")
    protected BranchAndFinancialInstitutionIdentification6 cdtrAgt;

    @XmlElement(name = "Cdtr")
    protected PartyIdentification135 cdtr;

    @XmlElement(name = "CdtrAcct")
    protected CashAccount24 cdtrAcct;

    @XmlElement(name = "RmtInf")
    protected RemittanceInformation16 rmtInf;

    public PaymentIdentification6 getPmtId() { return pmtId; }
    public void setPmtId(PaymentIdentification6 value) { this.pmtId = value; }

    public AmountType3Choice getAmt() { return amt; }
    public void setAmt(AmountType3Choice value) { this.amt = value; }

    public BranchAndFinancialInstitutionIdentification6 getCdtrAgt() { return cdtrAgt; }
    public void setCdtrAgt(BranchAndFinancialInstitutionIdentification6 value) { this.cdtrAgt = value; }

    public PartyIdentification135 getCdtr() { return cdtr; }
    public void setCdtr(PartyIdentification135 value) { this.cdtr = value; }

    public CashAccount24 getCdtrAcct() { return cdtrAcct; }
    public void setCdtrAcct(CashAccount24 value) { this.cdtrAcct = value; }

    public RemittanceInformation16 getRmtInf() { return rmtInf; }
    public void setRmtInf(RemittanceInformation16 value) { this.rmtInf = value; }
}
