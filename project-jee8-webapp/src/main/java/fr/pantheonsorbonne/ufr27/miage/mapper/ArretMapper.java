package fr.pantheonsorbonne.ufr27.miage.mapper;

import java.time.LocalDateTime;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;

public class ArretMapper {

	public static ArretJAXB mapArretToArretJAXB(Arret a) {
		ObjectFactory factory = new ObjectFactory();
		ArretJAXB arretJAXB = factory.createArretJAXB();
		LocalDateTime heureArrivee = a.getHeureArriveeEnGare();
		LocalDateTime heureDepart = a.getHeureDepartDeGare();

		arretJAXB.setGare(a.getGare().getNom());
		arretJAXB.setHeureArrivee(MapperUtils.localDateTimeToXmlGregorianCalendar(heureArrivee));
		arretJAXB.setHeureDepart(MapperUtils.localDateTimeToXmlGregorianCalendar(heureDepart));

		return arretJAXB;

	}

	public static Arret mapArretJAXBToArret(ArretJAXB a) {
		Arret arret = new Arret();
		Gare gare = new Gare(a.getGare());

		arret.setGare(gare);
		arret.setHeureArriveeEnGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(a.getHeureArrivee()));
		arret.setHeureDepartDeGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(a.getHeureDepart()));

		return arret;
	}

}
