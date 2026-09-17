package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MandateRelatedInformation6", propOrder = {"mndtId", "dtOfSgntr"})
public class MandateRelatedInformation6 {

    /** Référence unique du mandat de prélèvement — max 35 caractères. */
    @XmlElement(name = "MndtId")
    protected String mndtId;

    /** Date de signature du mandat. */
    @XmlElement(name = "DtOfSgntr")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dtOfSgntr;

    public String getMndtId() { return mndtId; }
    public void setMndtId(String value) { this.mndtId = value; }

    public XMLGregorianCalendar getDtOfSgntr() { return dtOfSgntr; }
    public void setDtOfSgntr(XMLGregorianCalendar value) { this.dtOfSgntr = value; }
}
