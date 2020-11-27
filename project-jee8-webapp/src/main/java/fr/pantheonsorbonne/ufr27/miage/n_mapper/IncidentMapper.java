package fr.pantheonsorbonne.ufr27.miage.n_mapper;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;

public class IncidentMapper {
	
	public static Incident mapIncidentJAXBToIncident(IncidentJAXB  i) {
		Incident indicident = new Incident();
	
		indicident.setEtat(i.getEtatIncident());
		indicident.setHeureDebut(MapperUtils.xmlGregorianCalendar2LocalDateTime(i.getHeureIncident()));
		indicident.setTypeIncident(i.getTypeIncident());
		return indicident;
	}
}
