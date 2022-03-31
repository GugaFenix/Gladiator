package me.gugafenix.legionmc.glad.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.main.Main;

public class ScoreManager {
	
	private List<Scoreboard> scoreboards;
	
	public ScoreManager() { scoreboards = new ArrayList<>(); }
	
	public List<Scoreboard> getScoreboards() { return scoreboards; }
	
	public Scoreboard getScoreBoard(String objName) {
		
		ListIterator<Scoreboard> it = scoreboards.listIterator();
		Scoreboard board = null;
		while (it.hasNext()) if ((board = it.next()).getObjectiveName().contentEquals(objName)) return board;
		return null;
	}
	
public Scoreboard getScoreBoard(Player player) {
		
		ListIterator<Scoreboard> it = scoreboards.listIterator();
		Scoreboard board = null;
		while (it.hasNext()) if ((board = it.next()).getBukkitPlayer() == player) return board;
		return null;
	}
	
	public static ScoreManager getManager() {
		return Main.getScoreBoardManager();
	}
	
}
