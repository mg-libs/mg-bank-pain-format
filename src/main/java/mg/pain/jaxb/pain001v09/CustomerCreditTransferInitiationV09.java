package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerCreditTransferInitiationV09", propOrder = {"grpHdr", "pmtInf"})
public class CustomerCreditTransferInitiationV09 {

    @XmlElement(name = "GrpHdr", required = true)
    protected GroupHeader85 grpHdr;

    @XmlElement(name = "PmtInf", required = true)
    protected List<PaymentInstruction30> pmtInf;

    public GroupHeader85 getGrpHdr() { return grpHdr; }
    public void setGrpHdr(GroupHeader85 value) { this.grpHdr = value; }

    public List<PaymentInstruction30> getPmtInf() {
        if (pmtInf == null) pmtInf = new ArrayList<>();
        return pmtInf;
    }
}
