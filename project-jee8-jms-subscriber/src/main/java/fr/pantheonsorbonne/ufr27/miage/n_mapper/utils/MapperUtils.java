package fr.pantheonsorbonne.ufr27.miage.n_mapper.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class MapperUtils {
	public static LocalDateTime xmlGregorianCalendarToLocalDateTime(XMLGregorianCalendar xgc) {	
		GregorianCalendar gc = xgc.toGregorianCalendar();
		ZonedDateTime zdt = gc.toZonedDateTime();
		LocalDateTime ldt = zdt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
		System.out.println("&&& ldt = " + ldt);
		return ldt;
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
