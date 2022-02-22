//package me.gugafenix.legionmc.glad.scoreboard;
//
//import java.util.List;
//
//import org.bukkit.entity.Player;
//
//import dev.jcsoftware.jscoreboards.JPerPlayerMethodBasedScoreboard;
//import dev.jcsoftware.jscoreboards.JScoreboardOptions;
//import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle;
//
//public class GladBoard {
//	
//	private String title;
//	private List<String> lines;
//	private JPerPlayerMethodBasedScoreboard score;
//	
//	public GladBoard(String title, List<String> lines) {
//		this.title = title;
//		this.lines = lines;
//	}
//	
//	public void create(Player p) {
//		this.score = new JPerPlayerMethodBasedScoreboard(new JScoreboardOptions(JScoreboardTabHealthStyle.NONE, false));
//		score.addPlayer(p);
//		score.setTitle(p, title);
//		score.setLines(p, lines);
//		score.updateScoreboard();
//	}
//	
//}
