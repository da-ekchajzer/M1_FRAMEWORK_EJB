package fr.pantheonsorbonne.ufr27.miage.main;

import javax.xml.datatype.DatatypeConfigurationException;

public class trainsLauncher {

	public static void main(String[] args) throws DatatypeConfigurationException, InterruptedException {
		int nbtrain = 10;

		Thread.sleep(5000);

		for (int idTrain = 1; idTrain < nbtrain; idTrain++) {
			Train train = new Train(idTrain);
			Thread thread = new Thread(train);
			thread.start();
		}
		
//		Train train2 = new Train(2);
//		Thread thread2 = new Thread(train2);
//		thread2.start();
//		Train train5 = new Train(5);
//		Thread thread5 = new Thread(train5);
//		thread5.start();
//		Train train7 = new Train(7);
//		Thread thread7 = new Thread(train7);
//		thread7.start();
	}
}
