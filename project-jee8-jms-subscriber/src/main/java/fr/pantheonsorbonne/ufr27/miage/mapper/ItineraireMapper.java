package fr.pantheonsorbonne.ufr27.miage.mapper;

import fr.pantheonsorbonne.ufr27.miage.mapper.utils.MapperUtils;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireInfoJAXB;
import fr.pantheonsorbonne.ufr27.miage.pojos.Itineraire;


public class ItineraireMapper {

	public static Itineraire mapItineraireJAXBToItineraire(ItineraireInfoJAXB itineraireInfoJAXB, String idTrain) {
		Itineraire i = new Itineraire();
		
		if(itineraireInfoJAXB.getHeureArrivee() != null) {
			i.setHeureArriveeEnGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoJAXB.getHeureArrivee()));
		}
		if(itineraireInfoJAXB.getHeureDepart() != null) {
			i.setHeureDepartDeGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoJAXB.getHeureDepart()));
		}
		i.setEtatItineraire(itineraireInfoJAXB.getEtatItineraire());
		i.setIdItineraire(idTrain);
		
		return i;

	}
	
	

}
