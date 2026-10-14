package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CashAccount38", propOrder = {"id"})
public class CashAccount38 {

    @XmlElement(name = "Id", required = true)
    protected AccountIdentification4Choice id;

    public AccountIdentification4Choice getId() { return id; }
    public void setId(AccountIdentification4Choice value) { this.id = value; }
}
