package mg.pain.jaxb.pain001v09;

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Choix entre une date seule (Dt) ou une date-heure (DtTm).
 * Pour SEPA Credit Transfer, utiliser Dt (date d'exécution demandée).
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DateAndDateTime2Choice", propOrder = {"dt", "dtTm"})
public class DateAndDateTime2Choice {

    @XmlElement(name = "Dt")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dt;

    @XmlElement(name = "DtTm")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dtTm;

    public XMLGregorianCalendar getDt() { return dt; }
    public void setDt(XMLGregorianCalendar value) { this.dt = value; }

    public XMLGregorianCalendar getDtTm() { return dtTm; }
    public void setDtTm(XMLGregorianCalendar value) { this.dtTm = value; }
}
