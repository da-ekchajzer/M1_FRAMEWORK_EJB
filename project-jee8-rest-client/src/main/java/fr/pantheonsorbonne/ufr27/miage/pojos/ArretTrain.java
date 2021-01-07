package fr.pantheonsorbonne.ufr27.miage.pojos;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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

		arret.setGare(nomGare);
		arret.setHeureArrivee(this.localDateTimeToXmlGregorianCalendar(this.heureArrive));
		arret.setHeureDepart(this.localDateTimeToXmlGregorianCalendar(this.heureDepart));
		return arret;
	}
	
	public XMLGregorianCalendar localDateTimeToXmlGregorianCalendar(LocalDateTime ldt) {
		if(ldt == null) return null;
		ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
		GregorianCalendar gc = GregorianCalendar.from(zdt);
		XMLGregorianCalendar xgc = null;
		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return xgc;
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
	public LocalDateTime getheureArrivee() {
		return heureArrive;
	}
	public void setheureArrive(LocalDateTime heureArrive) {
		this.heureArrive = heureArrive;
	}

}