package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransactionInformation9", propOrder = {
        "pmtId", "instdAmt", "drctDbtTx", "dbtrAgt", "dbtr", "dbtrAcct", "rmtInf"
})
public class DirectDebitTransactionInformation9 {

    @XmlElement(name = "PmtId", required = true)
    protected PaymentIdentification1 pmtId;

    @XmlElement(name = "InstdAmt", required = true)
    protected ActiveOrHistoricCurrencyAndAmount instdAmt;

    @XmlElement(name = "DrctDbtTx")
    protected DirectDebitTransaction6 drctDbtTx;

    @XmlElement(name = "DbtrAgt", required = true)
    protected BranchAndFinancialInstitutionIdentification4 dbtrAgt;

    @XmlElement(name = "Dbtr", required = true)
    protected PartyIdentification32 dbtr;

    @XmlElement(name = "DbtrAcct", required = true)
    protected CashAccount16 dbtrAcct;

    @XmlElement(name = "RmtInf")
    protected RemittanceInformation5 rmtInf;

    public PaymentIdentification1 getPmtId() { return pmtId; }
    public void setPmtId(PaymentIdentification1 value) { this.pmtId = value; }

    public ActiveOrHistoricCurrencyAndAmount getInstdAmt() { return instdAmt; }
    public void setInstdAmt(ActiveOrHistoricCurrencyAndAmount value) { this.instdAmt = value; }

    public DirectDebitTransaction6 getDrctDbtTx() { return drctDbtTx; }
    public void setDrctDbtTx(DirectDebitTransaction6 value) { this.drctDbtTx = value; }

    public BranchAndFinancialInstitutionIdentification4 getDbtrAgt() { return dbtrAgt; }
    public void setDbtrAgt(BranchAndFinancialInstitutionIdentification4 value) { this.dbtrAgt = value; }

    public PartyIdentification32 getDbtr() { return dbtr; }
    public void setDbtr(PartyIdentification32 value) { this.dbtr = value; }

    public CashAccount16 getDbtrAcct() { return dbtrAcct; }
    public void setDbtrAcct(CashAccount16 value) { this.dbtrAcct = value; }

    public RemittanceInformation5 getRmtInf() { return rmtInf; }
    public void setRmtInf(RemittanceInformation5 value) { this.rmtInf = value; }
}
