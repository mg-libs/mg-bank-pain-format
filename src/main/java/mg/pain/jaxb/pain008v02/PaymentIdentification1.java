package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentIdentification1", propOrder = {"instrId", "endToEndId"})
public class PaymentIdentification1 {

    @XmlElement(name = "InstrId")
    protected String instrId;

    @XmlElement(name = "EndToEndId", required = true)
    protected String endToEndId;

    public String getInstrId() { return instrId; }
    public void setInstrId(String value) { this.instrId = value; }

    public String getEndToEndId() { return endToEndId; }
    public void setEndToEndId(String value) { this.endToEndId = value; }
}
