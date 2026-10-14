package mg.pain.jaxb.pain008v08;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MandateRelatedInformation14", propOrder = {
        "mndtId", "dtOfSgntr", "amdmntInd"
})
public class MandateRelatedInformation14 {

    @XmlElement(name = "MndtId")
    protected String mndtId;

    @XmlElement(name = "DtOfSgntr")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dtOfSgntr;

    @XmlElement(name = "AmdmntInd")
    protected Boolean amdmntInd;

    public String getMndtId() { return mndtId; }
    public void setMndtId(String value) { this.mndtId = value; }

    public XMLGregorianCalendar getDtOfSgntr() { return dtOfSgntr; }
    public void setDtOfSgntr(XMLGregorianCalendar value) { this.dtOfSgntr = value; }

    public Boolean getAmdmntInd() { return amdmntInd; }
    public void setAmdmntInd(Boolean value) { this.amdmntInd = value; }
}
