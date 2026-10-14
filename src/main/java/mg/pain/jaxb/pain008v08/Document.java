package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"cstmrDrctDbtInitn"})
@XmlRootElement(name = "Document")
public class Document {

    @XmlElement(name = "CstmrDrctDbtInitn", required = true)
    protected CustomerDirectDebitInitiationV08 cstmrDrctDbtInitn;

    public CustomerDirectDebitInitiationV08 getCstmrDrctDbtInitn() { return cstmrDrctDbtInitn; }
    public void setCstmrDrctDbtInitn(CustomerDirectDebitInitiationV08 value) { this.cstmrDrctDbtInitn = value; }
}
