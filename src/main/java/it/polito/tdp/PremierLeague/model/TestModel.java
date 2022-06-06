package it.polito.tdp.PremierLeague.model;

import org.jgrapht.Graph;

public class TestModel {

	public static void main(String[] args) {
		Model m = new Model();
		
		Match p = m.partita(12);
		
		m.creaGrafo(p);
		System.out.println(m.deltaPlayerMax());

	}

}
