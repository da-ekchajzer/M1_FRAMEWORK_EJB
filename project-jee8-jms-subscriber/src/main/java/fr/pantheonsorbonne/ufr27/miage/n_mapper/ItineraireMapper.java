package fr.pantheonsorbonne.ufr27.miage.n_mapper;

import fr.pantheonsorbonne.ufr27.miage.POJO.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireInfoGareJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.utils.MapperUtils;


public class ItineraireMapper {

	public static Itineraire mapItineraireJAXBToItineraire(ItineraireInfoGareJAXB itineraireInfoGareJAXB, String idTrain) {
		Itineraire i = new Itineraire();
		
		i.setHeureArriveeEnGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoGareJAXB.getHeureArrivee()));
		i.setHeureDepartDeGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoGareJAXB.getHeureDepart()));
		i.setEtatItineraire(itineraireInfoGareJAXB.getEtatItineraire());
		i.setIdItineraire(idTrain);
		
		return i;

	}
	
	

}
