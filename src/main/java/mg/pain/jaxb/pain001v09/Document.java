package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"cstmrCdtTrfInitn"})
@XmlRootElement(name = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.09")
public class Document {

    @XmlElement(name = "CstmrCdtTrfInitn", required = true)
    protected CustomerCreditTransferInitiationV09 cstmrCdtTrfInitn;

    public CustomerCreditTransferInitiationV09 getCstmrCdtTrfInitn() { return cstmrCdtTrfInitn; }
    public void setCstmrCdtTrfInitn(CustomerCreditTransferInitiationV09 value) { this.cstmrCdtTrfInitn = value; }
}
