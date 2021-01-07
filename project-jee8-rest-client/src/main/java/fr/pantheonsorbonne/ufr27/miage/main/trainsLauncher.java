package fr.pantheonsorbonne.ufr27.miage.main;

import javax.xml.datatype.DatatypeConfigurationException;

public class trainsLauncher {
	public static void main(String[] args) throws DatatypeConfigurationException, InterruptedException {
		int nbtrain = 7;

		Thread.sleep(5000);

		for (int idTrain = 6; idTrain < nbtrain; idTrain++) {
			Train train = new Train(idTrain);
			Thread thread = new Thread(train);
			thread.start();
		}
	}
}
