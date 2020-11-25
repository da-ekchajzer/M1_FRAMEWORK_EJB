package fr.pantheonsorbonne.ufr27.miage.pojos;

import java.time.LocalDateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;

public class ArretTrain {

	String nomGare;
	LocalDateTime heureArrive;
	LocalDateTime heureDepart;
	
	public ArretTrain(String nomGare, LocalDateTime heureArrive, LocalDateTime heureDepart) {
		this.nomGare = nomGare;
		this.heureArrive = heureArrive;
		this.heureDepart = heureDepart;
	}
	
	public ArretJAXB getXMLArret() {
		ObjectFactory factory = new ObjectFactory();
		ArretJAXB arret = factory.createArretJAXB();

		try {
			arret.setGare(nomGare);
			arret.setHeureArrive(DatatypeFactory.newInstance().newXMLGregorianCalendar(heureArrive.toString()));
			arret.setHeureDepart(DatatypeFactory.newInstance().newXMLGregorianCalendar(heureDepart.toString()));

		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return arret;
	}
	
	
	public String getNomGare() {
		return nomGare;
	}
	public void setNomGare(String nomGare) {
		this.nomGare = nomGare;
	}
	public LocalDateTime getHeureDepart() {
		return heureDepart;
	}
	public void setHeureDepart(LocalDateTime heureDepart) {
		this.heureDepart = heureDepart;
	}
	public LocalDateTime getheureArrive() {
		return heureArrive;
	}
	public void setheureArrive(LocalDateTime heureArrive) {
		this.heureArrive = heureArrive;
	}

}