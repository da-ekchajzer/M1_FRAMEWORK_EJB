package fr.pantheonsorbonne.ufr27.miage.mapper;

import java.util.List;

import fr.pantheonsorbonne.ufr27.miage.pojos.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireInfoJAXB;
import fr.pantheonsorbonne.ufr27.miage.mapper.utils.MapperUtils;


public class ItineraireMapper {

	public static Itineraire mapItineraireJAXBToItineraire(ItineraireInfoJAXB itineraireInfoJAXB, String idTrain) {
		Itineraire i = new Itineraire();
		
		if(itineraireInfoJAXB.getHeureArrivee() != null) {
			i.setHeureArriveeEnGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoJAXB.getHeureArrivee()));
		}
		if(itineraireInfoJAXB.getHeureDepart() != null) {
			i.setHeureDepartDeGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoJAXB.getHeureDepart()));
		}
		
		i.setGareArrive(itineraireInfoJAXB.getGaresArrive());
		i.setGareDepart(itineraireInfoJAXB.getGaresDepart());
		
		i.setEtatItineraire(itineraireInfoJAXB.getEtatItineraire());
		i.setIdItineraire(idTrain);
		
		return i;

	}

}
