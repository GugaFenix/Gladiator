/*
 * 
 */
package me.gugafenix.legionmc.glad.spawnselection.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;

public class PlayerChatEvent implements Listener {
	
	@EventHandler
	void onPlayerChatEvent(AsyncPlayerChatEvent e) {
		System.out.println(e.getMessage());
		Player p = e.getPlayer();
		if (SpawnSelectManager.getManager().getSelect(p) == null) return;
		if (!e.getMessage().equalsIgnoreCase("PRONTO") && !e.getMessage().equalsIgnoreCase("CANCELAR")) return;
		
		e.setCancelled(true);
		SpawnSelect select = SpawnSelectManager.getManager().getSelect(p);
		if (e.getMessage().equalsIgnoreCase("PRONTO")) select.finishSelection();
		else if (e.getMessage().equalsIgnoreCase("CANCELAR")) select.cancelSelection();
		
	}
	
}
