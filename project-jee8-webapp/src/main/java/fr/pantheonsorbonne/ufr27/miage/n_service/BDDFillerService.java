package fr.pantheonsorbonne.ufr27.miage.n_service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jvnet.hk2.annotations.Service;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;

@Service
public class BDDFillerService {

	private EntityManager em;

	public BDDFillerService(EntityManager em) {
		this.em = em;
	}

	public void fill() {
		em.getTransaction().begin();

		// --------------------------------- Remplissage de la table Gare ----------------------------------
		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes" };

		Map<String, Gare> gares = new HashMap<>();
		for (String nomGare : nomGares) {
			Gare g = new Gare(nomGare);
			gares.put(nomGare, g);
			em.persist(g);
		}

		// --------------------------------- Remplissage de la table Voyageur -------------------------------

		String[] prenomsVoyageurs = { "Mariah", "Marc", "Sophia", "Alyssia", "Antoine", "Doudouh", "Lucie", "Lucas",
				"David", "Ben", "Maria", "Lucas", "Sophie", "Jean-Mi", "Jean", "Abdel", "Tatiana", "Charlotte",
				"Charlotte", "Abdel", "Ben", "Ben", "Mathieu", "Louis", "Jean-Luc", "Luc", "Jean", "Sophia", "Marc",
				"Manuel" };

		String[] nomsVoyageurs = { "Dupont", "Dupont", "Durand", "Martin", "Bernard", "Thomas", "Petit", "Grand",
				"Robert", "Richard", "Richard", "Dubois", "Petit", "Petit", "Moreau", "Laurent", "Simon", "Michel",
				"Lefevre", "Legrand", "Lefebvre", "Leroy", "Roux", "Leroi", "Morel", "Fournier", "Gerard", "Poirier",
				"Pommier", "Rossignol" };

		List<Voyageur> voyageursAyantReservesTrain3 = new ArrayList<>();
		List<Voyageur> voyageursAyantReservesTrain4 = new ArrayList<>();
		for (int i = 0; i < prenomsVoyageurs.length; i++) {
			Voyageur v = new Voyageur(prenomsVoyageurs[i], nomsVoyageurs[i]);
			if (i < 10)
				voyageursAyantReservesTrain3.add(v);
			else if (i >= 17)
				voyageursAyantReservesTrain4.add(v);
			em.persist(v);
		}

		// --------------------------------- Remplissage de la table Train ------------------------------------
		Train train1 = new TrainSansResa();
		Train train2 = new TrainSansResa();
		Train train3 = new TrainAvecResa(voyageursAyantReservesTrain3);
		Train train4 = new TrainAvecResa(voyageursAyantReservesTrain4);

		Train[] trains = { train1, train2, train3, train4 };
		for (Train t : trains)
			em.persist(t);

		// --------------------------------- Remplissage de la table Itinéraire -------------------------------

		// --------------------------------- Remplissage de la table Trajet -----------------------------------

		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"));
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"));
		Trajet trajet3 = new Trajet(gares.get("Aix en Provence"), gares.get("Marseille - St Charles"));
		Trajet trajet4 = new Trajet(gares.get("Marseille - St Charles"), gares.get("Dijon-Ville"));
		Trajet trajet5 = new Trajet(gares.get("Dijon-Ville"), gares.get("Lyon - Pardieu"));
		Trajet trajet6 = new Trajet(gares.get("Lyon - Pardieu"), gares.get("Narbonne"));
		Trajet trajet7 = new Trajet(gares.get("Narbonne"), gares.get("Sete"));
		Trajet trajet8 = new Trajet(gares.get("Sete"), gares.get("Perpignan"));

		Trajet trajet9 = new Trajet(gares.get("Paris - Montparnasse"), gares.get("Tours"));
		Trajet trajet10 = new Trajet(gares.get("Tours"), gares.get("Bordeaux - Saint-Jean"));
		Trajet trajet11 = new Trajet(gares.get("Bordeaux - Saint-Jean"), gares.get("Pessac"));
		Trajet trajet12 = new Trajet(gares.get("Pessac"), gares.get("Arcachon-Centre"));

		Trajet trajet13 = new Trajet(gares.get("Paris - Montparnasse"), gares.get("Nantes"));

		Trajet[] trajets = { trajet1, trajet2, trajet3, trajet4, trajet5, trajet6, trajet7, trajet8, trajet9, trajet10,
				trajet11, trajet12, trajet13 };

		for (Trajet t : trajets)
			em.persist(t);

		// --------------------------------- Remplissage de la table Voyage ------------------------------------

		em.getTransaction().commit();
	}
}
