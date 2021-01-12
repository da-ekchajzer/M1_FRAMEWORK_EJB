package fr.pantheonsorbonne.ufr27.miage.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class MapperUtils {
	public static LocalDateTime xmlGregorianCalendarToLocalDateTime(XMLGregorianCalendar xgc) {
		LocalDateTime ldt = null;
		if (xgc != null) {
			GregorianCalendar gc = xgc.toGregorianCalendar();
			ZonedDateTime zdt = gc.toZonedDateTime();
			ldt = zdt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
		}
		return ldt;
	}

	public static XMLGregorianCalendar localDateTimeToXmlGregorianCalendar(LocalDateTime ldt) {
		XMLGregorianCalendar xgc = null;
		if (ldt != null) {
			ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
			GregorianCalendar gc = GregorianCalendar.from(zdt);
			try {
				xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		}
		return xgc;
	}
}
