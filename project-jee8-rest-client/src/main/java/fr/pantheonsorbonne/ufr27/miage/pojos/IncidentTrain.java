package fr.pantheonsorbonne.ufr27.miage.pojos;

import java.time.LocalDateTime;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;

public class IncidentTrain {

	int typeIncident;
	int etatIncident;
	LocalDateTime heureIncident;
	
	public IncidentTrain(int typeIncident) {
		this.typeIncident = typeIncident;
		this.etatIncident = 1;
		this.heureIncident = LocalDateTime.now();
	}

	public IncidentJAXB getXMLIncident() {
		ObjectFactory factory = new ObjectFactory();
		IncidentJAXB incident = factory.createIncidentJAXB();

		try {
			incident.setTypeIncident(this.typeIncident);
			incident.setEtatIncident(this.etatIncident);
			incident.setHeureIncident(
					DatatypeFactory.newInstance().newXMLGregorianCalendar(heureIncident.toString()));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return incident;
	}

	public void setEtatIncident(int i) {
		etatIncident = 0;
		
	}

}
