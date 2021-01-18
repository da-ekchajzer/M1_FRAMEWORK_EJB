package fr.pantheonsorbonne.ufr27.miage.mapper;

import fr.pantheonsorbonne.ufr27.miage.pojos.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfosItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.mapper.utils.MapperUtils;

public class ItineraireMapper {

	public static Itineraire mapItineraireJAXBToItineraire(InfosItineraireJAXB itineraireInfoJAXB, String idTrain) {
		Itineraire i = new Itineraire();

		if (itineraireInfoJAXB.getHeureArrivee() != null) {
			i.setHeureArriveeEnGare(
					MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoJAXB.getHeureArrivee()));
		}
		if (itineraireInfoJAXB.getHeureDepart() != null) {
			i.setHeureDepartDeGare(
					MapperUtils.xmlGregorianCalendarToLocalDateTime(itineraireInfoJAXB.getHeureDepart()));
		}
		i.setGareArrivee(itineraireInfoJAXB.getGareArrivee());
		i.setGareDepart(itineraireInfoJAXB.getGareDepart());
		i.setGaresDesservies(itineraireInfoJAXB.getGaresDesservies());
		i.setEtatItineraire(itineraireInfoJAXB.getEtatItineraire());
		i.setIdItineraire(idTrain);

		return i;
	}

}
