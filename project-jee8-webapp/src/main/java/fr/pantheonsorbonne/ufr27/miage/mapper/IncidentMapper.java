package fr.pantheonsorbonne.ufr27.miage.mapper;

import fr.pantheonsorbonne.ufr27.miage.jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;

public class IncidentMapper {

	public static Incident mapIncidentJAXBToIncident(IncidentJAXB i) {
		Incident incident = new Incident();
		incident.setEtat(i.getEtatIncident());
		incident.setTypeIncident(i.getTypeIncident());
		incident.setHeureDebut(MapperUtils.xmlGregorianCalendarToLocalDateTime(i.getDebutIncident()));
		incident.initHeureTheoriqueDeFin(CodeTypeIncident.getTempEstimation(i.getTypeIncident()));
		return incident;
	}
}
