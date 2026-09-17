package mg.pain.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

/**
 * Utilitaires de conversion vers XMLGregorianCalendar pour les fichiers ISO 20022.
 */
public final class XmlCalendarUtils {

    private XmlCalendarUtils() {}

    /**
     * Retourne la date et l'heure courante sans millisecondes ni fuseau horaire,
     * conformément aux exigences SEPA pour CreDtTm.
     */
    public static XMLGregorianCalendar nowDateTime() throws DatatypeConfigurationException {
        GregorianCalendar gcal = GregorianCalendar.from(
                LocalDateTime.now().atZone(ZoneId.systemDefault())
        );
        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        xmlCal.setFractionalSecond(null);
        xmlCal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        return xmlCal;
    }

    /**
     * Retourne la date courante sans heure ni fuseau horaire,
     * conformément aux exigences SEPA pour ReqdExctnDt.
     */
    public static XMLGregorianCalendar nowDate() throws DatatypeConfigurationException {
        return fromLocalDate(LocalDate.now());
    }

    /**
     * Convertit un {@link LocalDate} en {@link XMLGregorianCalendar} date-only,
     * sans heure ni fuseau horaire.
     * Utilisé pour ReqdColltnDt (pain.008) et DtOfSgntr (date de signature du mandat).
     */
    public static XMLGregorianCalendar fromLocalDate(LocalDate date) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                DatatypeConstants.FIELD_UNDEFINED
        );
    }
}
