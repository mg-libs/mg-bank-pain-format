package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerDirectDebitInitiationV08", propOrder = {"grpHdr", "pmtInf"})
public class CustomerDirectDebitInitiationV08 {

    @XmlElement(name = "GrpHdr", required = true)
    protected GroupHeader83 grpHdr;

    @XmlElement(name = "PmtInf", required = true)
    protected List<PaymentInstruction43> pmtInf;

    public GroupHeader83 getGrpHdr() { return grpHdr; }
    public void setGrpHdr(GroupHeader83 value) { this.grpHdr = value; }

    public List<PaymentInstruction43> getPmtInf() {
        if (pmtInf == null) pmtInf = new ArrayList<>();
        return pmtInf;
    }
}
