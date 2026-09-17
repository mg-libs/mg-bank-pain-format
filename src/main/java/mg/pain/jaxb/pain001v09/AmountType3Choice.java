package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountType3Choice", propOrder = {"instdAmt"})
public class AmountType3Choice {

    @XmlElement(name = "InstdAmt")
    protected ActiveOrHistoricCurrencyAndAmount instdAmt;

    public ActiveOrHistoricCurrencyAndAmount getInstdAmt() { return instdAmt; }
    public void setInstdAmt(ActiveOrHistoricCurrencyAndAmount value) { this.instdAmt = value; }
}
