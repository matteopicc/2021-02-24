package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;


public class PremierLeagueDAO {
	
	public void listAllPlayers(Map<Integer,Player> idMap){
		String sql = "SELECT * FROM Players";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getInt("PlayerID"))) {
					Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
					idMap.put(player.getPlayerID(), player);
				}

			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Team> listAllTeams(){
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Team getTeamByID(int id) {
		String sql="SELECT * "
				+ "FROM teams "
				+ "WHERE teams.TeamID = ?";
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet res = st.executeQuery();
			Team team = null;
			while (res.next()) {

			 team = new Team(res.getInt("TeamID"), res.getString("Name"));

			}
			conn.close();
			return team;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> getAllMatches(){
		String sql="SELECT * "
				+ "FROM matches";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while(res.next()) {
				Team casa = this.getTeamByID(res.getInt("TeamHomeID"));
				Team trasferta = this.getTeamByID(res.getInt("TeamAwayID"));
				Match match = new Match(res.getInt("MatchID"),res.getInt("TeamHomeID"),res.getInt("TeamAwayID"),res.getInt("TeamHomeFormation"),res.getInt("TeamAwayFormation"),res.getInt("ResultOfTeamHome"),res.getTimestamp("Date").toLocalDateTime(),casa.getName(),trasferta.getName());
				result.add(match);
			}
			conn.close();
			return result;
			
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	
	public List<Player> playersMatch(Match match){
		String sql="SELECT p.PlayerID AS id , p.Name AS nome "
				+ "FROM actions a, players p "
				+ "WHERE a.PlayerID = p.PlayerID AND a.MatchID= ? "
				+ "GROUP BY p.PlayerID,p.Name";
		List<Player> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, match.getMatchID());
			ResultSet res = st.executeQuery();
			while(res.next()) {
				Player player = new Player(res.getInt("id"),res.getString("nome"));
				result.add(player);
			}
			conn.close();
			return result;
			
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	
		
	}
	
	
	
	public List<Adiacenza> getAdiacenze(Match m, Map<Integer,Player> idMap){
		String sql = "SELECT a1.PlayerID as p1, a2.PlayerID as p2, "
				+ "((a1.totalSuccessfulPassesAll + a1.assists)/a1.timePlayed - (a2.totalSuccessfulPassesAll + a2.assists)/a2.timePlayed) as peso "
				+ "FROM Actions a1, Actions a2 "
				+ "WHERE a1.MatchID = a2.MatchID "
				+ "AND a1.MatchID = ? "
				+ "AND a1.PlayerID > a2.PlayerID "
				+ "AND a1.TeamID <> a2.TeamID";
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(idMap.containsKey(res.getInt("p1")) && idMap.containsKey(res.getInt("p2"))) {
					result.add(new Adiacenza(idMap.get(res.getInt("p1")), idMap.get(res.getInt("p2")), res.getDouble("peso")));
				}
			}
			
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	}
	
	

