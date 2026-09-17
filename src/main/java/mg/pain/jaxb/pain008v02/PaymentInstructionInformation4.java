package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentInstructionInformation4", propOrder = {
        "pmtInfId", "pmtMtd", "nbOfTxs", "ctrlSum",
        "pmtTpInf", "reqdColltnDt",
        "cdtr", "cdtrAcct", "cdtrAgt",
        "chrgBr", "drctDbtTxInf"
})
public class PaymentInstructionInformation4 {

    @XmlElement(name = "PmtInfId", required = true)
    protected String pmtInfId;

    @XmlElement(name = "PmtMtd", required = true)
    @XmlSchemaType(name = "string")
    protected PaymentMethod2Code pmtMtd;

    @XmlElement(name = "NbOfTxs")
    protected String nbOfTxs;

    @XmlElement(name = "CtrlSum")
    protected BigDecimal ctrlSum;

    @XmlElement(name = "PmtTpInf")
    protected PaymentTypeInformation20 pmtTpInf;

    @XmlElement(name = "ReqdColltnDt", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar reqdColltnDt;

    @XmlElement(name = "Cdtr", required = true)
    protected PartyIdentification32 cdtr;

    @XmlElement(name = "CdtrAcct", required = true)
    protected CashAccount16 cdtrAcct;

    @XmlElement(name = "CdtrAgt", required = true)
    protected BranchAndFinancialInstitutionIdentification4 cdtrAgt;

    @XmlElement(name = "ChrgBr")
    @XmlSchemaType(name = "string")
    protected ChargeBearerType1Code chrgBr;

    @XmlElement(name = "DrctDbtTxInf", required = true)
    protected List<DirectDebitTransactionInformation9> drctDbtTxInf;

    public String getPmtInfId() { return pmtInfId; }
    public void setPmtInfId(String value) { this.pmtInfId = value; }

    public PaymentMethod2Code getPmtMtd() { return pmtMtd; }
    public void setPmtMtd(PaymentMethod2Code value) { this.pmtMtd = value; }

    public String getNbOfTxs() { return nbOfTxs; }
    public void setNbOfTxs(String value) { this.nbOfTxs = value; }

    public BigDecimal getCtrlSum() { return ctrlSum; }
    public void setCtrlSum(BigDecimal value) { this.ctrlSum = value; }

    public PaymentTypeInformation20 getPmtTpInf() { return pmtTpInf; }
    public void setPmtTpInf(PaymentTypeInformation20 value) { this.pmtTpInf = value; }

    public XMLGregorianCalendar getReqdColltnDt() { return reqdColltnDt; }
    public void setReqdColltnDt(XMLGregorianCalendar value) { this.reqdColltnDt = value; }

    public PartyIdentification32 getCdtr() { return cdtr; }
    public void setCdtr(PartyIdentification32 value) { this.cdtr = value; }

    public CashAccount16 getCdtrAcct() { return cdtrAcct; }
    public void setCdtrAcct(CashAccount16 value) { this.cdtrAcct = value; }

    public BranchAndFinancialInstitutionIdentification4 getCdtrAgt() { return cdtrAgt; }
    public void setCdtrAgt(BranchAndFinancialInstitutionIdentification4 value) { this.cdtrAgt = value; }

    public ChargeBearerType1Code getChrgBr() { return chrgBr; }
    public void setChrgBr(ChargeBearerType1Code value) { this.chrgBr = value; }

    public List<DirectDebitTransactionInformation9> getDrctDbtTxInf() {
        if (drctDbtTxInf == null) drctDbtTxInf = new ArrayList<>();
        return drctDbtTxInf;
    }
}
