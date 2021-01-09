package fr.pantheonsorbonne.ufr27.miage.pojos;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;

public class IncidentTrain {

	int typeIncident;
	int etatIncident;
	LocalDateTime debutIncident;

	public IncidentTrain(int typeIncident) {
		this.typeIncident = typeIncident;
		this.etatIncident = 1;
		this.debutIncident = LocalDateTime.now();
	}

	public IncidentJAXB getXMLIncident() {
		ObjectFactory factory = new ObjectFactory();
		IncidentJAXB incident = factory.createIncidentJAXB();

		incident.setTypeIncident(this.typeIncident);
		incident.setEtatIncident(this.etatIncident);
		incident.setDebutIncident(localDateTimeToXmlGregorianCalendar(debutIncident));

		return incident;
	}

	public int getTypeIncident() {
		return typeIncident;
	}

	public void setEtatIncident(int i) {
		etatIncident = i;
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
