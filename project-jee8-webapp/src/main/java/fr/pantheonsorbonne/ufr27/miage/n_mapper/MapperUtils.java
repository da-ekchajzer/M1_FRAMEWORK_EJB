package fr.pantheonsorbonne.ufr27.miage.n_mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class MapperUtils {
	public static LocalDateTime xmlGregorianCalendarToLocalDateTime(XMLGregorianCalendar xgc) {
		final int offsetSeconds = xgc.toGregorianCalendar().toZonedDateTime().getOffset().getTotalSeconds();
		final LocalDateTime localDateTime = xgc.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
		return localDateTime.minusSeconds(offsetSeconds);
	}

	public static XMLGregorianCalendar localDateTimeToXmlGregorianCalendar(LocalDateTime ldt) {
		ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
		GregorianCalendar gc = GregorianCalendar.from(zdt);
		XMLGregorianCalendar xgc = null;
		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return xgc;
	}
}
