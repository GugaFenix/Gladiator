package me.gugafenix.legionmc.glad.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;

public class PlayerRemoveEvent implements Listener {
	
	@EventHandler
	void onPlayerDeathEvent(PlayerDeathEvent e) {
		Player d = e.getEntity();
		Player p = e.getEntity().getKiller();
		GladPlayer gpm = Main.getPlayerManager().getPlayer(p);
		GladPlayer gpk = Main.getPlayerManager().getPlayer(d);
		Gladiator glad = Gladiator.getGladRunning();
		
		if (glad == null) return;
		if (gpm == null || !gpm.isInGladiator()) return;
		
		e.setDeathMessage(null);
		gpk.updateInfos();
		gpm.deleteFromGladiator(glad);
		glad.updateInfoFromAll();
	}
}
