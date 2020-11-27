package fr.pantheonsorbonne.ufr27.miage.n_mapper;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;

public class ArretMapper {

	
	
	public static ArretJAXB mapArretToArretJAXB(Arret a) {
		ObjectFactory factory = new ObjectFactory();
		ArretJAXB arretJAXB = factory.createArretJAXB();
		
		arretJAXB.setGare(a.getGare().getNom());
		try {
			arretJAXB.setHeureArrive(DatatypeFactory.newInstance().newXMLGregorianCalendar(a.getHeureArriveeEnGare().toString()));
			arretJAXB.setHeureDepart(DatatypeFactory.newInstance().newXMLGregorianCalendar(a.getHeureDepartDeGare().toString()));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		
		return arretJAXB;
		
	}
	
	public static Arret mapArretJAXBToArret(ArretJAXB  a) {
		Arret arret = new Arret();
		Gare gare = new Gare(a.getGare());
	
		arret.setGare(gare);
		arret.setHeureArriveeEnGare(MapperUtils.xmlGregorianCalendar2LocalDateTime(a.getHeureArrive()));
		arret.setHeureDepartDeGare(MapperUtils.xmlGregorianCalendar2LocalDateTime(a.getHeureDepart()));
		
		return arret;
	}
	
}
