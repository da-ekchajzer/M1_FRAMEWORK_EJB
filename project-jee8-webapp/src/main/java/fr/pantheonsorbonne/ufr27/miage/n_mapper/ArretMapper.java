package fr.pantheonsorbonne.ufr27.miage.n_mapper;

import java.time.LocalDateTime;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;

public class ArretMapper {

	public static ArretJAXB mapArretToArretJAXB(Arret a) {
		ObjectFactory factory = new ObjectFactory();
		ArretJAXB arretJAXB = factory.createArretJAXB();
		LocalDateTime heureArrivee = a.getHeureArriveeEnGare();
		LocalDateTime heureDepart = a.getHeureDepartDeGare();

		arretJAXB.setGare(a.getGare().getNom());
		if (heureArrivee != null) {
			arretJAXB.setHeureArrive(MapperUtils.localDateTimeToXmlGregorianCalendar(a.getHeureArriveeEnGare()));
		}
		if (heureDepart != null) {
			arretJAXB.setHeureDepart(MapperUtils.localDateTimeToXmlGregorianCalendar(a.getHeureDepartDeGare()));
		}
		return arretJAXB;

	}

	public static Arret mapArretJAXBToArret(ArretJAXB a) {
		Arret arret = new Arret();
		Gare gare = new Gare(a.getGare());

		arret.setGare(gare);
		arret.setHeureArriveeEnGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(a.getHeureArrive()));
		arret.setHeureDepartDeGare(MapperUtils.xmlGregorianCalendarToLocalDateTime(a.getHeureDepart()));

		return arret;
	}

}
