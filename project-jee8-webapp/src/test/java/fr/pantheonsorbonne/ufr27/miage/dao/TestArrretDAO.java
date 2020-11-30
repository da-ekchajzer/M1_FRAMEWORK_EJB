package fr.pantheonsorbonne.ufr27.miage.dao;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestUtils;

class TestArrretDAO {

	@BeforeAll
	public void setup() {
		TestUtils.startServer();
	}

	@Test
	public void test1() {
		assertTrue(true);
	}

	@AfterAll
	public void end() {
		TestUtils.stopServer();
	}
}
