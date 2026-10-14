package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemittanceInformation16", propOrder = {"ustrd"})
public class RemittanceInformation16 {

    @XmlElement(name = "Ustrd")
    protected List<String> ustrd;

    public List<String> getUstrd() {
        if (ustrd == null) ustrd = new ArrayList<>();
        return ustrd;
    }
}
