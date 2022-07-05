package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	private PremierLeagueDAO dao;
	private Graph<Player,DefaultWeightedEdge> grafo;
	private Map<Integer,Player> idMap;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<Integer,Player>();
		this.dao.listAllPlayers(idMap);
	}
	
	public List<Match> getTuttiMatch(){
		return dao.getAllMatches();
	}
	
	public void creaGrafo(Match match) {
		this.grafo = new SimpleDirectedWeightedGraph<Player,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//aggiungo vertici
		Graphs.addAllVertices(this.grafo, dao.playersMatch(match));
		
		//aggiungo archi
		for(Adiacenza a : dao.getAdiacenze(match, idMap)) {
			if(a.getPeso() >= 0) {
				//p1 meglio di p2
				if(grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
					Graphs.addEdgeWithVertices(this.grafo, a.getP1(), 
							a.getP2(), a.getPeso());
				}
			} else {
				//p2 meglio di p1
				if(grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
					Graphs.addEdgeWithVertices(this.grafo, a.getP2(), 
							a.getP1(), (-1) * a.getPeso());
				}
			}
		}
	}

	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public boolean grafoCreato() {
		if(this.grafo == null) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public GiocatoreMigliore getBest() {
		if(grafo == null) {
			return null;
		}
		
		Player best = null;
		Double maxDelta = (double) Integer.MIN_VALUE;
		
		for(Player p : this.grafo.vertexSet()) {
			// calcolo la somma dei pesi degli archi uscenti
			double pesoUscente = 0.0;
			for(DefaultWeightedEdge edge : this.grafo.outgoingEdgesOf(p)) {
				pesoUscente += this.grafo.getEdgeWeight(edge);
			}
			
			// calcolo la somma dei pesi degli archi entranti
			double pesoEntrante = 0.0;
			for(DefaultWeightedEdge edge : this.grafo.incomingEdgesOf(p)) {
				pesoEntrante += this.grafo.getEdgeWeight(edge);
			}
			
			double delta = pesoUscente - pesoEntrante;
			if(delta > maxDelta) {
				best = p;
				maxDelta = delta;
			}
		}
		
		return new GiocatoreMigliore (best,maxDelta);
	}
	
	
	
	
	
	
}
