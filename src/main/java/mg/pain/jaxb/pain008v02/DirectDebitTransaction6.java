package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransaction6", propOrder = {"mndtRltdInf"})
public class DirectDebitTransaction6 {

    @XmlElement(name = "MndtRltdInf")
    protected MandateRelatedInformation6 mndtRltdInf;

    public MandateRelatedInformation6 getMndtRltdInf() { return mndtRltdInf; }
    public void setMndtRltdInf(MandateRelatedInformation6 value) { this.mndtRltdInf = value; }
}
