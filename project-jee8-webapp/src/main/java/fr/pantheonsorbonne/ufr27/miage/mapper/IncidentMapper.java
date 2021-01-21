package fr.pantheonsorbonne.ufr27.miage.mapper;

import fr.pantheonsorbonne.ufr27.miage.jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;

public class IncidentMapper {

	public static Incident mapIncidentJAXBToIncident(IncidentJAXB i) {
		Incident incident = new Incident(i.getTypeIncident());
		incident.setEtat(i.getEtatIncident());
		incident.setHeureDebut(MapperUtils.xmlGregorianCalendarToLocalDateTime(i.getDebutIncident()));
		incident.initHeureTheoriqueDeFin(CodeTypeIncident.valueOf(i.getTypeIncident()).getTempEstimation());
		return incident;
	}
}
