package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupHeader85", propOrder = {"msgId", "creDtTm", "nbOfTxs", "ctrlSum", "initgPty"})
public class GroupHeader85 {

    @XmlElement(name = "MsgId", required = true)
    protected String msgId;

    @XmlElement(name = "CreDtTm", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creDtTm;

    @XmlElement(name = "NbOfTxs", required = true)
    protected String nbOfTxs;

    @XmlElement(name = "CtrlSum")
    protected BigDecimal ctrlSum;

    @XmlElement(name = "InitgPty", required = true)
    protected PartyIdentification135 initgPty;

    public String getMsgId() { return msgId; }
    public void setMsgId(String value) { this.msgId = value; }

    public XMLGregorianCalendar getCreDtTm() { return creDtTm; }
    public void setCreDtTm(XMLGregorianCalendar value) { this.creDtTm = value; }

    public String getNbOfTxs() { return nbOfTxs; }
    public void setNbOfTxs(String value) { this.nbOfTxs = value; }

    public BigDecimal getCtrlSum() { return ctrlSum; }
    public void setCtrlSum(BigDecimal value) { this.ctrlSum = value; }

    public PartyIdentification135 getInitgPty() { return initgPty; }
    public void setInitgPty(PartyIdentification135 value) { this.initgPty = value; }
}
