package fr.pantheonsorbonne.ufr27.miage.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.service.BDDFillerService;

@ManagedBean
@ApplicationScoped
public class BDDFillerServiceImpl implements BDDFillerService {

	@Inject
	MessageGateway messageGateway;

	private EntityManager em;

	public BDDFillerServiceImpl(EntityManager em) {
		this.em = em;
	}

	public void fill() {
		em.getTransaction().begin();

		// --------------------------------- Remplissage de la table Gare

		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes", "Montpellier", "Cabries" };

		Map<String, Gare> gares = new HashMap<>();
		for (String nomGare : nomGares) {
			Gare g = new Gare(nomGare);
			gares.put(nomGare, g);
			em.persist(g);
		}

		// --------------------------------- Remplissage de la table Train
		Train train1 = new TrainAvecResa("OUIGO");
		Train train2 = new TrainSansResa("TER");
		Train train3 = new TrainAvecResa("TGV");
		Train train4 = new TrainSansResa("TER");
		Train train5 = new TrainAvecResa("OUIGO");
		Train train6 = new TrainSansResa("TER");
		Train train7 = new TrainAvecResa("TGV");
		Train train8 = new TrainAvecResa("TGV");
		Train train9 = new TrainAvecResa("TER");

		Train[] trains = { train1, train2, train3, train4, train5, train6, train7, train8, train9 };
		for (Train t : trains)
			em.persist(t);

		// --------------------------------- Remplissage de la table Arrêt

		Arret arret1 = new Arret(gares.get("Paris - Gare de Lyon"), null, LocalDateTime.now().plusSeconds(40));
		Arret arret2 = new Arret(gares.get("Avignon-Centre"), LocalDateTime.now().plusSeconds(60),
				LocalDateTime.now().plusSeconds(70));
		Arret arret3 = new Arret(gares.get("Aix en Provence"), LocalDateTime.now().plusSeconds(90),
				LocalDateTime.now().plusSeconds(100));
		Arret arret4 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plusSeconds(120), null);

		Arret arret5 = new Arret(gares.get("Perpignan"), null, LocalDateTime.now().plusSeconds(40));
		Arret arret6 = new Arret(gares.get("Sete"), LocalDateTime.now().plusSeconds(60),
				LocalDateTime.now().plusSeconds(70));
		Arret arret7 = new Arret(gares.get("Narbonne"), LocalDateTime.now().plusSeconds(90),
				LocalDateTime.now().plusSeconds(100));
		Arret arret8 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plusSeconds(120), null);

		Arret arret9 = new Arret(gares.get("Marseille - St Charles"), null, LocalDateTime.now().plusSeconds(130));
		Arret arret10 = new Arret(gares.get("Dijon-Ville"), LocalDateTime.now().plusSeconds(150),
				LocalDateTime.now().plusSeconds(160));
		Arret arret11 = new Arret(gares.get("Lyon - Pardieu"), LocalDateTime.now().plusSeconds(180), null);

		Arret arret12 = new Arret(gares.get("Lyon - Pardieu"), null, LocalDateTime.now().plusSeconds(40));
		Arret arret13 = new Arret(gares.get("Dijon-Ville"), LocalDateTime.now().plusSeconds(60),
				LocalDateTime.now().plusSeconds(70));
		Arret arret14 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plusSeconds(90), null);

		Arret arret15 = new Arret(gares.get("Marseille - St Charles"), null, LocalDateTime.now().plusSeconds(100));
		Arret arret16 = new Arret(gares.get("Narbonne"), LocalDateTime.now().plusSeconds(120),
				LocalDateTime.now().plusSeconds(130));
		Arret arret17 = new Arret(gares.get("Sete"), LocalDateTime.now().plusSeconds(150),
				LocalDateTime.now().plusSeconds(160));
		Arret arret18 = new Arret(gares.get("Perpignan"), LocalDateTime.now().plusSeconds(180), null);

		Arret arret19 = new Arret(gares.get("Paris - Montparnasse"), null, LocalDateTime.now().plusSeconds(40));
		Arret arret20 = new Arret(gares.get("Tours"), LocalDateTime.now().plusSeconds(60),
				LocalDateTime.now().plusSeconds(70));
		Arret arret21 = new Arret(gares.get("Bordeaux - Saint-Jean"), LocalDateTime.now().plusSeconds(90), null);

		Arret arret22 = new Arret(gares.get("Bordeaux - Saint-Jean"), null, LocalDateTime.now().plusSeconds(100));
		Arret arret23 = new Arret(gares.get("Pessac"), LocalDateTime.now().plusSeconds(120),
				LocalDateTime.now().plusSeconds(130));
		Arret arret24 = new Arret(gares.get("Arcachon-Centre"), LocalDateTime.now().plusSeconds(150), null);

		Arret arret25 = new Arret(gares.get("Nantes"), null, LocalDateTime.now().plusSeconds(40));
		Arret arret26 = new Arret(gares.get("Paris - Montparnasse"), LocalDateTime.now().plusSeconds(60), null);

		Arret arret27 = new Arret(gares.get("Paris - Montparnasse"), null, LocalDateTime.now().plusSeconds(70));
		Arret arret28 = new Arret(gares.get("Bordeaux - Saint-Jean"), LocalDateTime.now().plusSeconds(90), null);

		Arret arret29 = new Arret(gares.get("Bordeaux - Saint-Jean"), null, LocalDateTime.now().plusSeconds(100));
		Arret arret30 = new Arret(gares.get("Montpellier"), LocalDateTime.now().plusSeconds(120),
				LocalDateTime.now().plusSeconds(130));
		Arret arret31 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plusSeconds(150), null);

		Arret arret32 = new Arret(gares.get("Marseille - St Charles"), null, LocalDateTime.now().plusSeconds(160));
		Arret arret33 = new Arret(gares.get("Cabries"), LocalDateTime.now().plusSeconds(180),
				LocalDateTime.now().plusSeconds(190));
		Arret arret34 = new Arret(gares.get("Aix en Provence"), LocalDateTime.now().plusSeconds(210), null);

		Arret[] arrets = { arret1, arret2, arret3, arret4, arret5, arret6, arret7, arret8, arret9, arret10, arret11,
				arret12, arret13, arret14, arret15, arret16, arret17, arret18, arret19, arret20, arret21, arret22,
				arret23, arret24, arret25, arret26, arret27, arret28, arret29, arret30, arret31, arret32, arret33,
				arret34 };

		for (Arret a : arrets)
			em.persist(a);

		// --------------------------------- Remplissage de la table Itinéraire

		Itineraire itineraire1 = new Itineraire(train1);
		itineraire1.addArret(arret1);
		itineraire1.addArret(arret2);
		itineraire1.addArret(arret3);
		itineraire1.addArret(arret4);

		Itineraire itineraire2 = new Itineraire(train2);
		itineraire2.addArret(arret5);
		itineraire2.addArret(arret6);
		itineraire2.addArret(arret7);
		itineraire2.addArret(arret8);

		Itineraire itineraire3 = new Itineraire(train1);
		itineraire3.addArret(arret9);
		itineraire3.addArret(arret10);
		itineraire3.addArret(arret11);

		Itineraire itineraire4 = new Itineraire(train3);
		itineraire4.addArret(arret12);
		itineraire4.addArret(arret13);
		itineraire4.addArret(arret14);

		Itineraire itineraire5 = new Itineraire(train4);
		itineraire5.addArret(arret15);
		itineraire5.addArret(arret16);
		itineraire5.addArret(arret17);
		itineraire5.addArret(arret18);

		Itineraire itineraire6 = new Itineraire(train5);
		itineraire6.addArret(arret19);
		itineraire6.addArret(arret20);
		itineraire6.addArret(arret21);

		Itineraire itineraire7 = new Itineraire(train6);
		itineraire7.addArret(arret22);
		itineraire7.addArret(arret23);
		itineraire7.addArret(arret24);

		Itineraire itineraire8 = new Itineraire(train7);
		itineraire8.addArret(arret25);
		itineraire8.addArret(arret26);

		Itineraire itineraire9 = new Itineraire(train7);
		itineraire9.addArret(arret27);
		itineraire9.addArret(arret28);

		Itineraire itineraire10 = new Itineraire(train8);
		itineraire10.addArret(arret29);
		itineraire10.addArret(arret30);
		itineraire10.addArret(arret31);

		Itineraire itineraire11 = new Itineraire(train9);
		itineraire11.addArret(arret32);
		itineraire11.addArret(arret33);
		itineraire11.addArret(arret34);

		Itineraire[] itineraires = { itineraire1, itineraire2, itineraire3, itineraire4, itineraire5, itineraire6,
				itineraire7, itineraire8, itineraire9, itineraire10, itineraire11 };

		for (Itineraire i : itineraires) {
			em.persist(i);
		}

		// --------------------------------- Remplissage de la table Trajet

		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"), itineraire1, 0);
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"), itineraire1, 1);
		Trajet trajet3 = new Trajet(gares.get("Aix en Provence"), gares.get("Marseille - St Charles"), itineraire1, 2);

		Trajet trajet4 = new Trajet(gares.get("Perpignan"), gares.get("Sete"), itineraire2, 0);
		Trajet trajet5 = new Trajet(gares.get("Sete"), gares.get("Narbonne"), itineraire2, 1);
		Trajet trajet6 = new Trajet(gares.get("Narbonne"), gares.get("Marseille - St Charles"), itineraire2, 2);

		// Changement
		Trajet trajet7 = new Trajet(gares.get("Marseille - St Charles"), gares.get("Dijon-Ville"), itineraire3, 0);
		Trajet trajet8 = new Trajet(gares.get("Dijon-Ville"), gares.get("Lyon - Pardieu"), itineraire3, 1);

		Trajet trajet9 = new Trajet(gares.get("Lyon - Pardieu"), gares.get("Dijon-Ville"), itineraire4, 0);
		Trajet trajet10 = new Trajet(gares.get("Dijon-Ville"), gares.get("Marseille - St Charles"), itineraire4, 1);

		// Changement
		Trajet trajet11 = new Trajet(gares.get("Marseille - St Charles"), gares.get("Narbonne"), itineraire5, 0);
		Trajet trajet12 = new Trajet(gares.get("Narbonne"), gares.get("Sete"), itineraire5, 1);
		Trajet trajet13 = new Trajet(gares.get("Sete"), gares.get("Perpignan"), itineraire5, 2);

		Trajet trajet14 = new Trajet(gares.get("Paris - Montparnasse"), gares.get("Tours"), itineraire6, 0);
		Trajet trajet15 = new Trajet(gares.get("Tours"), gares.get("Bordeaux - Saint-Jean"), itineraire6, 1);

		// Changement
		Trajet trajet16 = new Trajet(gares.get("Bordeaux - Saint-Jean"), gares.get("Pessac"), itineraire7, 0);
		Trajet trajet17 = new Trajet(gares.get("Pessac"), gares.get("Arcachon-Centre"), itineraire7, 1);

		Trajet trajet18 = new Trajet(gares.get("Nantes"), gares.get("Paris - Montparnasse"), itineraire8, 0);

		Trajet trajet19 = new Trajet(gares.get("Paris - Montparnasse"), gares.get("Bordeaux - Saint-Jean"), itineraire9,
				0);

		Trajet trajet20 = new Trajet(gares.get("Bordeaux - Saint-Jean"), gares.get("Montpellier"), itineraire9, 0);
		Trajet trajet21 = new Trajet(gares.get("Montpellier"), gares.get("Marseille - St Charles"), itineraire9, 1);

		Trajet trajet22 = new Trajet(gares.get("Marseille - St Charles"), gares.get("Cabries"), itineraire9, 0);
		Trajet trajet23 = new Trajet(gares.get("Cabries"), gares.get("Aix en Provence"), itineraire9, 1);

		Trajet[] trajets = { trajet1, trajet2, trajet3, trajet4, trajet5, trajet6, trajet7, trajet8, trajet9, trajet10,
				trajet11, trajet12, trajet13, trajet14, trajet15, trajet16, trajet17, trajet18, trajet19, trajet20,
				trajet21, trajet22, trajet23 };

		for (Trajet t : trajets)
			em.persist(t);

		// --------------------------------- Remplissage de la table Voyage
		List<Trajet> voyageTrajet1 = new LinkedList<Trajet>();
		voyageTrajet1.add(trajet1);
		voyageTrajet1.add(trajet2);
		voyageTrajet1.add(trajet3);
		Voyage voyage1 = new Voyage(voyageTrajet1);

		List<Trajet> voyageTrajet2 = new LinkedList<Trajet>();
		voyageTrajet2.add(trajet4);
		voyageTrajet2.add(trajet5);
		voyageTrajet2.add(trajet6);
		voyageTrajet2.add(trajet7);
		voyageTrajet2.add(trajet8);
		Voyage voyage2 = new Voyage(voyageTrajet2);

		List<Trajet> voyageTrajet3 = new LinkedList<Trajet>();
		voyageTrajet3.add(trajet9);
		voyageTrajet3.add(trajet10);
		voyageTrajet3.add(trajet11);
		voyageTrajet3.add(trajet12);
		voyageTrajet3.add(trajet13);
		Voyage voyage3 = new Voyage(voyageTrajet3);

		List<Trajet> voyageTrajet4 = new LinkedList<Trajet>();
		voyageTrajet4.add(trajet14);
		voyageTrajet4.add(trajet15);
		voyageTrajet4.add(trajet16);
		voyageTrajet4.add(trajet17);
		Voyage voyage4 = new Voyage(voyageTrajet4);

		List<Trajet> voyageTrajet5 = new LinkedList<Trajet>();
		voyageTrajet5.add(trajet18);
		voyageTrajet5.add(trajet19);
		Voyage voyage5 = new Voyage(voyageTrajet5);

		List<Trajet> voyageTrajet6 = new LinkedList<Trajet>();
		voyageTrajet6.add(trajet5);
		voyageTrajet6.add(trajet6);
		voyageTrajet6.add(trajet7);
		Voyage voyage6 = new Voyage(voyageTrajet6);

		List<Trajet> voyageTrajet7 = new LinkedList<Trajet>();
		voyageTrajet7.add(trajet15);
		voyageTrajet7.add(trajet16);
		Voyage voyage7 = new Voyage(voyageTrajet7);

		List<Trajet> voyageTrajet8 = new LinkedList<Trajet>();
		voyageTrajet8.add(trajet19);
		voyageTrajet8.add(trajet20);
		voyageTrajet8.add(trajet21);
		voyageTrajet8.add(trajet22);
		voyageTrajet8.add(trajet23);
		Voyage voyage8 = new Voyage(voyageTrajet8);

		Voyage[] voyages = { voyage1, voyage2, voyage3, voyage4, voyage5, voyage6, voyage7, voyage8 };

		for (Voyage v : voyages)
			em.persist(v);

		// --------------------------------- Remplissage de la table Voyageur

		String[] prenomsVoyageurs = { "Mariah", "Marc", "Sophia", "Alyssia", "Antoine", "Doudouh", "Lucie", "Lucas",
				"David", "Ben", "Maria", "Lucas", "Sophie", "Jean-Mi", "Jean", "Abdel", "Tatiana", "Charlotte",
				"Charlotte", "Abdel", "Ben", "Ben", "Mathieu", "Louis", "Jean-Luc", "Luc", "Jean", "Sophia", "Marc",
				"Manuel", "Abdel", "Jean-Pierre", "Joline", "Charmine", "Agathe" };

		String[] nomsVoyageurs = { "Dupont", "Dupont", "Durand", "Martin", "Bernard", "Thomas", "Petit", "Grand",
				"Robert", "Richard", "Richard", "Dubois", "Petit", "Petit", "Moreau", "Laurent", "Simon", "Michel",
				"Lefevre", "Legrand", "Lefebvre", "Leroy", "Roux", "Leroi", "Morel", "Fournier", "Gerard", "Poirier",
				"Pommier", "Rossignol", "Benamara", "Remus", "Giso", "Rabougris", "Mwaka" };

		for (int i = 0; i < prenomsVoyageurs.length; i++) {
			Voyageur v = new Voyageur(prenomsVoyageurs[i], nomsVoyageurs[i]);
			if (i < 5) {
				voyage1.addVoyageur(v);
				v.setVoyageActuel(voyage1);
			} else if (i >= 5 && i < 10) {
				voyage2.addVoyageur(v);
				v.setVoyageActuel(voyage2);
			} else if (i >= 10 && i < 15) {
				voyage3.addVoyageur(v);
				v.setVoyageActuel(voyage3);
			} else if (i >= 15 && i < 20) {
				voyage4.addVoyageur(v);
				v.setVoyageActuel(voyage4);
			} else if (i >= 20 && i < 25) {
				voyage5.addVoyageur(v);
				v.setVoyageActuel(voyage5);
			} else if (i >= 25 && i < 30) {
				voyage6.addVoyageur(v);
				v.setVoyageActuel(voyage6);
			} else if (i >= 30 && i < 31) {
				voyage7.addVoyageur(v);
				v.setVoyageActuel(voyage7);
			} else {
				voyage8.addVoyageur(v);
				v.setVoyageActuel(voyage8);
			}
			em.persist(v);
		}
		em.getTransaction().commit();
	}
}
