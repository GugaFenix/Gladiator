/*
 * 
 */
package me.gugafenix.legionmc.glad.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.scoreboard.ScoreManager;
import me.gugafenix.legionmc.glad.scoreboard.Scoreboard;
import me.gugafenix.legionmc.glad.utils.Messages;
import me.gugafenix.legionmc.glad.utils.PlaceHolder;

public class PlayerRemoveEvent implements Listener {
	
	@EventHandler
	void onPlayerDeathEvent(PlayerDeathEvent e) {
		Player d = e.getEntity();
		Player p = e.getEntity().getKiller();
		GladPlayer gpm = Main.getPlayerManager().getPlayer(d);
		GladPlayer gpk = Main.getPlayerManager().getPlayer(p);
		Gladiator glad = Gladiator.getGladRunning();
		
		if (glad == null) return;
		if (gpm == null || !gpm.isInGladiator()) return;
		
		e.setDeathMessage(null);
		
		if (d.getKiller() == null) {
			for (Player pl : Bukkit.getOnlinePlayers())
				pl.sendMessage(PlaceHolder.replace(pl, Messages.suicide.replace("%dead%", d.getName())));
		} else {
			gpk.setKills(gpk.getKills() + 1);
			for (Player pl : Bukkit.getOnlinePlayers())
				pl.sendMessage(PlaceHolder.replace(pl, Messages.suicide.replace("%dead%", d.getName()).replace("%killer%", p.getName())));
		}
		
		gpm.deleteFromGladiator(glad);
		glad.updateInfoFromAll();
		
		d.spigot().respawn();
		d.teleport(glad.getCabin().getLocation());
		
		Scoreboard score = ScoreManager.getManager().getScoreBoard(p);
		if (score != null) {
			for (GladPlayer gp : glad.getPlayers()) {
				Player pl = gp.getPlayer();
				score = ScoreManager.getManager().getScoreBoard(pl.getName());
				if (score == null) {
					score = new Scoreboard(pl, pl.getName());
					score.create();
					score.setObjectiveName(glad.getScoreboard().get(0));
				}
				score.update();
			}
			
		}
	}
}
