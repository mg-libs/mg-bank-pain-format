package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActiveOrHistoricCurrencyAndAmount")
public class ActiveOrHistoricCurrencyAndAmount {

    @XmlAttribute(name = "Ccy", required = true)
    protected String ccy;

    @XmlValue
    protected BigDecimal value;

    public String getCcy() { return ccy; }
    public void setCcy(String value) { this.ccy = value; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
}
