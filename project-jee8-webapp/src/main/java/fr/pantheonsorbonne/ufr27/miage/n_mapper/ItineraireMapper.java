package fr.pantheonsorbonne.ufr27.miage.n_mapper;

import java.util.List;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;

public class ItineraireMapper {
		
	public static ItineraireJAXB mapItineraireToItineraireJAXB(Itineraire itineraire) {
		ObjectFactory factory = new ObjectFactory();
		ItineraireJAXB itineraireJAXB = factory.createItineraireJAXB();
		List<ArretJAXB> arretsJAXB = itineraireJAXB.getArrets();
		
		for(int i = 0; i < itineraire.getArretsDesservis().size();i++) {
			arretsJAXB.add(ArretMapper.mapArretToArretJAXB(itineraire.getArretsDesservis().get(i)));
		}
		
		itineraireJAXB.setEtatItineraire(itineraire.getEtat());
		
		return itineraireJAXB;
	}
}
