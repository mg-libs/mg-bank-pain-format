package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerDirectDebitInitiationV02", propOrder = {"grpHdr", "pmtInf"})
public class CustomerDirectDebitInitiationV02 {

    @XmlElement(name = "GrpHdr", required = true)
    protected GroupHeader39 grpHdr;

    @XmlElement(name = "PmtInf", required = true)
    protected List<PaymentInstructionInformation4> pmtInf;

    public GroupHeader39 getGrpHdr() { return grpHdr; }
    public void setGrpHdr(GroupHeader39 value) { this.grpHdr = value; }

    public List<PaymentInstructionInformation4> getPmtInf() {
        if (pmtInf == null) pmtInf = new ArrayList<>();
        return pmtInf;
    }
}
