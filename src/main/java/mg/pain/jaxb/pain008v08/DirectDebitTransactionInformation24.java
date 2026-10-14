package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransactionInformation24", propOrder = {
        "pmtId", "instdAmt", "drctDbtTx", "ultmtCdtr",
        "dbtrAgt", "dbtr", "dbtrAcct", "rmtInf"
})
public class DirectDebitTransactionInformation24 {

    @XmlElement(name = "PmtId", required = true)
    protected PaymentIdentification13 pmtId;

    @XmlElement(name = "InstdAmt", required = true)
    protected ActiveOrHistoricCurrencyAndAmount instdAmt;

    @XmlElement(name = "DrctDbtTx")
    protected DirectDebitTransaction10 drctDbtTx;

    @XmlElement(name = "UltmtCdtr")
    protected PartyIdentification135 ultmtCdtr;

    @XmlElement(name = "DbtrAgt", required = true)
    protected BranchAndFinancialInstitutionIdentification6 dbtrAgt;

    @XmlElement(name = "Dbtr", required = true)
    protected PartyIdentification135 dbtr;

    @XmlElement(name = "DbtrAcct", required = true)
    protected CashAccount38 dbtrAcct;

    @XmlElement(name = "RmtInf")
    protected RemittanceInformation16 rmtInf;

    public PaymentIdentification13 getPmtId() { return pmtId; }
    public void setPmtId(PaymentIdentification13 value) { this.pmtId = value; }

    public ActiveOrHistoricCurrencyAndAmount getInstdAmt() { return instdAmt; }
    public void setInstdAmt(ActiveOrHistoricCurrencyAndAmount value) { this.instdAmt = value; }

    public DirectDebitTransaction10 getDrctDbtTx() { return drctDbtTx; }
    public void setDrctDbtTx(DirectDebitTransaction10 value) { this.drctDbtTx = value; }

    public PartyIdentification135 getUltmtCdtr() { return ultmtCdtr; }
    public void setUltmtCdtr(PartyIdentification135 value) { this.ultmtCdtr = value; }

    public BranchAndFinancialInstitutionIdentification6 getDbtrAgt() { return dbtrAgt; }
    public void setDbtrAgt(BranchAndFinancialInstitutionIdentification6 value) { this.dbtrAgt = value; }

    public PartyIdentification135 getDbtr() { return dbtr; }
    public void setDbtr(PartyIdentification135 value) { this.dbtr = value; }

    public CashAccount38 getDbtrAcct() { return dbtrAcct; }
    public void setDbtrAcct(CashAccount38 value) { this.dbtrAcct = value; }

    public RemittanceInformation16 getRmtInf() { return rmtInf; }
    public void setRmtInf(RemittanceInformation16 value) { this.rmtInf = value; }
}
