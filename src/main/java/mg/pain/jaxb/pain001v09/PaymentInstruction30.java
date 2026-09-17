package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentInstruction30", propOrder = {
    "pmtInfId", "pmtMtd", "nbOfTxs", "ctrlSum",
    "pmtTpInf", "reqdExctnDt", "dbtr", "dbtrAcct", "dbtrAgt", "chrgBr", "cdtTrfTxInf"
})
public class PaymentInstruction30 {

    @XmlElement(name = "PmtInfId", required = true)
    protected String pmtInfId;

    @XmlElement(name = "PmtMtd", required = true)
    protected PaymentMethod3Code pmtMtd;

    @XmlElement(name = "NbOfTxs")
    protected String nbOfTxs;

    @XmlElement(name = "CtrlSum")
    protected BigDecimal ctrlSum;

    @XmlElement(name = "PmtTpInf")
    protected PaymentTypeInformation26 pmtTpInf;

    /** En v09, ReqdExctnDt est un DateAndDateTime2Choice (date ou dateTime). */
    @XmlElement(name = "ReqdExctnDt", required = true)
    protected DateAndDateTime2Choice reqdExctnDt;

    @XmlElement(name = "Dbtr", required = true)
    protected PartyIdentification135 dbtr;

    @XmlElement(name = "DbtrAcct", required = true)
    protected CashAccount24 dbtrAcct;

    @XmlElement(name = "DbtrAgt", required = true)
    protected BranchAndFinancialInstitutionIdentification6 dbtrAgt;

    @XmlElement(name = "ChrgBr")
    protected ChargeBearerType1Code chrgBr;

    @XmlElement(name = "CdtTrfTxInf", required = true)
    protected List<CreditTransferTransaction34> cdtTrfTxInf;

    public String getPmtInfId() { return pmtInfId; }
    public void setPmtInfId(String value) { this.pmtInfId = value; }

    public PaymentMethod3Code getPmtMtd() { return pmtMtd; }
    public void setPmtMtd(PaymentMethod3Code value) { this.pmtMtd = value; }

    public String getNbOfTxs() { return nbOfTxs; }
    public void setNbOfTxs(String value) { this.nbOfTxs = value; }

    public BigDecimal getCtrlSum() { return ctrlSum; }
    public void setCtrlSum(BigDecimal value) { this.ctrlSum = value; }

    public PaymentTypeInformation26 getPmtTpInf() { return pmtTpInf; }
    public void setPmtTpInf(PaymentTypeInformation26 value) { this.pmtTpInf = value; }

    public DateAndDateTime2Choice getReqdExctnDt() { return reqdExctnDt; }
    public void setReqdExctnDt(DateAndDateTime2Choice value) { this.reqdExctnDt = value; }

    public PartyIdentification135 getDbtr() { return dbtr; }
    public void setDbtr(PartyIdentification135 value) { this.dbtr = value; }

    public CashAccount24 getDbtrAcct() { return dbtrAcct; }
    public void setDbtrAcct(CashAccount24 value) { this.dbtrAcct = value; }

    public BranchAndFinancialInstitutionIdentification6 getDbtrAgt() { return dbtrAgt; }
    public void setDbtrAgt(BranchAndFinancialInstitutionIdentification6 value) { this.dbtrAgt = value; }

    public ChargeBearerType1Code getChrgBr() { return chrgBr; }
    public void setChrgBr(ChargeBearerType1Code value) { this.chrgBr = value; }

    public List<CreditTransferTransaction34> getCdtTrfTxInf() {
        if (cdtTrfTxInf == null) cdtTrfTxInf = new ArrayList<>();
        return cdtTrfTxInf;
    }
}
