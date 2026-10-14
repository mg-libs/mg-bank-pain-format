package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransaction10", propOrder = {"mndtRltdInf"})
public class DirectDebitTransaction10 {

    @XmlElement(name = "MndtRltdInf")
    protected MandateRelatedInformation14 mndtRltdInf;

    public MandateRelatedInformation14 getMndtRltdInf() { return mndtRltdInf; }
    public void setMndtRltdInf(MandateRelatedInformation14 value) { this.mndtRltdInf = value; }
}
