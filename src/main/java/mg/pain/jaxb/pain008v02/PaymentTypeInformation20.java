package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformation20", propOrder = {"svcLvl", "lclInstrm", "seqTp"})
public class PaymentTypeInformation20 {

    @XmlElement(name = "SvcLvl")
    protected ServiceLevel8Choice svcLvl;

    @XmlElement(name = "LclInstrm")
    protected LocalInstrument2Choice lclInstrm;

    @XmlElement(name = "SeqTp")
    @XmlSchemaType(name = "string")
    protected SequenceType3Code seqTp;

    public ServiceLevel8Choice getSvcLvl() { return svcLvl; }
    public void setSvcLvl(ServiceLevel8Choice value) { this.svcLvl = value; }

    public LocalInstrument2Choice getLclInstrm() { return lclInstrm; }
    public void setLclInstrm(LocalInstrument2Choice value) { this.lclInstrm = value; }

    public SequenceType3Code getSeqTp() { return seqTp; }
    public void setSeqTp(SequenceType3Code value) { this.seqTp = value; }
}
