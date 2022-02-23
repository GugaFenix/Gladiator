package me.gugafenix.legionmc.glad.spawnselection.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;

public class PlayerChatEvent implements Listener {
	
	public PlayerChatEvent() { Main.registerEvent(new PlayerChatEvent()); }
	
	@EventHandler
	void onPlayerChatEvent(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (SpawnSelectManager.getManager().getSelect(p) == null) return;
		if (!e.getMessage().equalsIgnoreCase("PRONTO") || !e.getMessage().equalsIgnoreCase("CANCELAR")) return;
		
		SpawnSelect select = SpawnSelectManager.getManager().getSelect(p);
		
		if (e.getMessage().equalsIgnoreCase("PRONTO")) select.finishSelection();
		else select.cancelSelection();
		
	}
	
}
