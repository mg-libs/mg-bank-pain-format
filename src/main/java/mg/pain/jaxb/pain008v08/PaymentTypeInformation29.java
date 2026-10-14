package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformation29", propOrder = {"svcLvl", "lclInstrm", "seqTp"})
public class PaymentTypeInformation29 {

    /** En V08, SvcLvl est une liste (0..unbounded). */
    @XmlElement(name = "SvcLvl")
    protected List<ServiceLevel8Choice> svcLvl;

    @XmlElement(name = "LclInstrm")
    protected LocalInstrument2Choice lclInstrm;

    @XmlElement(name = "SeqTp")
    @XmlSchemaType(name = "string")
    protected SequenceType3Code seqTp;

    public List<ServiceLevel8Choice> getSvcLvl() {
        if (svcLvl == null) svcLvl = new ArrayList<>();
        return svcLvl;
    }

    public LocalInstrument2Choice getLclInstrm() { return lclInstrm; }
    public void setLclInstrm(LocalInstrument2Choice value) { this.lclInstrm = value; }

    public SequenceType3Code getSeqTp() { return seqTp; }
    public void setSeqTp(SequenceType3Code value) { this.seqTp = value; }
}
