package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformation26", propOrder = {"svcLvl"})
public class PaymentTypeInformation26 {

    @XmlElement(name = "SvcLvl")
    protected ServiceLevel8Choice svcLvl;

    public ServiceLevel8Choice getSvcLvl() { return svcLvl; }
    public void setSvcLvl(ServiceLevel8Choice value) { this.svcLvl = value; }
}
