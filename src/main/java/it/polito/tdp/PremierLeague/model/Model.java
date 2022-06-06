package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	private PremierLeagueDAO dao;
	private Graph<Player,DefaultWeightedEdge>grafo;
	private Map<Integer, Player> giocatori;
	private List<Player> players;
	private Map<Integer, Player> giocatori2;
	
	public Model() {
		dao = new PremierLeagueDAO();
		this.giocatori = new HashMap<Integer,Player>();
	}
	
	public List<Match>getMatch(){
		return dao.listAllMatches();
	}
	
	public void creaGrafo(Match m) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//vertici
		dao.giocatoriPartita(giocatori, m);
		this.players = new ArrayList<Player>(giocatori.values());
		Graphs.addAllVertices(this.grafo, players);
		
		//archi
		for(Adiacenza a: this.dao.getArchi(m, giocatori)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(),a.getPeso());
		}
		
		System.out.println("grafo creato");
		System.out.println("#Vertici : "+this.grafo.vertexSet().size());
		System.out.println("#Archi : "+this.grafo.edgeSet().size());
	}
	
	public Match partita(int idMatch) {
		Match m  = null;
		List<Match>lista = dao.listAllMatches();
		for(Match match : lista) {
			if(match.getMatchID()==idMatch) {
				m = match;
			}
		}
		return m;
	}
	
	public String deltaPlayerMax(){
		this.giocatori2 = new HashMap<Integer,Player>();
		Player best = null;
		int max=-10000;
		for(Player p: this.grafo.vertexSet()) {
			int deltaT=-100;
			int deltaI=0;
			int deltaU=0;
			for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(p)) {
				deltaU += this.grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge f: this.grafo.incomingEdgesOf(p)) {
				deltaI += this.grafo.getEdgeWeight(f);
			}
			deltaT = (deltaU)-(deltaI);
			this.giocatori2.put(deltaT, p);
		}
		for(Integer i: giocatori2.keySet()) {
			if(i>max) {
				max=i;
				best = giocatori2.get(max);
			}
		}
		return best.toString() +" "+max;
		
	}
	
	public int getNVertici(){
		return this.grafo.vertexSet().size();
	}
	public int getNArchi(){
		return this.grafo.edgeSet().size();
	}
	
	public boolean grafoCreato() {
		if(this.grafo==null) {
			return false;
		}
		return true;
	}
	
	
}
