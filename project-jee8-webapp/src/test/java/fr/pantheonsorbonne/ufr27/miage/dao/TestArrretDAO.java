package fr.pantheonsorbonne.ufr27.miage.dao;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestUtils;

public class TestArrretDAO {
	@BeforeClass
	public void setup() {
		TestUtils.startServer();
	}
	
	@Test
	public void test1() {
		assertTrue(true);
	}
	
	
	@AfterClass
	public void end() {
		TestUtils.stopServer();
	}
}
