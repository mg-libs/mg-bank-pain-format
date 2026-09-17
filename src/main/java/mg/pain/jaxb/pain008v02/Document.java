package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"cstmrDrctDbtInitn"})
@XmlRootElement(name = "Document")
public class Document {

    @XmlElement(name = "CstmrDrctDbtInitn", required = true)
    protected CustomerDirectDebitInitiationV02 cstmrDrctDbtInitn;

    public CustomerDirectDebitInitiationV02 getCstmrDrctDbtInitn() { return cstmrDrctDbtInitn; }
    public void setCstmrDrctDbtInitn(CustomerDirectDebitInitiationV02 value) { this.cstmrDrctDbtInitn = value; }
}
