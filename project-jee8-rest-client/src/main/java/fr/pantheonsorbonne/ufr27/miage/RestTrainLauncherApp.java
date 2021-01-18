package fr.pantheonsorbonne.ufr27.miage;

import javax.xml.datatype.DatatypeConfigurationException;

import fr.pantheonsorbonne.ufr27.miage.train.Train;

public class RestTrainLauncherApp {

	public static void main(String[] args) throws DatatypeConfigurationException, InterruptedException {
		int nbtrain = 10;

		Thread.sleep(20000);

		for (int idTrain = 1; idTrain < nbtrain; idTrain++) {
			Train train = new Train(idTrain);
			Thread thread = new Thread(train);
			thread.start();
		}
	}
}
