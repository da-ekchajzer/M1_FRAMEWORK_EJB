package fr.pantheonsorbonne.ufr27.miage.n_mapper;

import fr.pantheonsorbonne.ufr27.miage.POJO.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireInfoJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.utils.MapperUtils;


public class ItineraireMapper {

	public static Itineraire mapItineraireJAXBToItineraire(ItineraireInfoJAXB itineraireInfoJAXB, String idTrain) {
		Itineraire i = new Itineraire();
		
		if(itineraireInfoJAXB.getHeureArrivee() != null) {
			i.setHeureArriveeEnGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoJAXB.getHeureArrivee()));
		}
		if(itineraireInfoJAXB.getHeureDepart() != null) {
			i.setHeureDepartDeGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoJAXB.getHeureDepart()));
		}
		
		i.setGaresDeservis(itineraireInfoJAXB.getGares());
		
		i.setEtatItineraire(itineraireInfoJAXB.getEtatItineraire());
		i.setIdItineraire(idTrain);
		
		return i;

	}
	
	

}
