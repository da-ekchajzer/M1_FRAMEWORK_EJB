package fr.pantheonsorbonne.ufr27.miage.n_mapper;

import java.time.LocalDateTime;

import javax.xml.datatype.XMLGregorianCalendar;

public class MapperUtils {
	public static LocalDateTime xmlGregorianCalendar2LocalDateTime(XMLGregorianCalendar xgc) {
		final int offsetSeconds = xgc.toGregorianCalendar().toZonedDateTime().getOffset().getTotalSeconds();
		final LocalDateTime localDateTime = xgc.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
		return localDateTime.minusSeconds(offsetSeconds);
	}
}
