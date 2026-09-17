package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CashAccount16", propOrder = {"id"})
public class CashAccount16 {

    @XmlElement(name = "Id", required = true)
    protected AccountIdentification4Choice id;

    public AccountIdentification4Choice getId() { return id; }
    public void setId(AccountIdentification4Choice value) { this.id = value; }
}
