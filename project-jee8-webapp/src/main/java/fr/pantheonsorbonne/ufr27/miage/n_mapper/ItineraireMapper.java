package fr.pantheonsorbonne.ufr27.miage.n_mapper;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;

public class ItineraireMapper {
	
	@Inject
	private static ArretMapper arretMapper;
	
	public static ItineraireJAXB mapItineraireToItineraireJAXB(Itineraire itineraire) {
		ObjectFactory factory = new ObjectFactory();
		ItineraireJAXB initeraireJAXB = factory.createItineraireJAXB();
		List<ArretJAXB> arretsJAXB = new LinkedList<ArretJAXB>();
		
		for(int i = 0; i < itineraire.getGaresDesservies().size();i++) {
			arretsJAXB.add(arretMapper.mapArretToArretJAXB(itineraire.getGaresDesservies().get(i)));
		}
		
		initeraireJAXB.setArrets(arretsJAXB);
		initeraireJAXB.setEtatItineraire(itineraire.getEtat());
		
		return initeraireJAXB;
	}


}
