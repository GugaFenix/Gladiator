package me.gugafenix.legionmc.glad.events;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.utils.API;

public class ProibitedEventOnGlad implements Listener {
	
	@EventHandler
	void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		if (!p.hasPermission("*") && gp != null && gp.isInGladiator()) {
			
			List<String> allowCommands = Main.getMain().getConfig().getStringList("ComandosPermitidos");
			
			if (allowCommands.isEmpty()) return;
			if (allowCommands.contains("/" + e.getMessage().replace("/", ""))) return;
			
			e.setCancelled(true);
			p.sendMessage(Main.tag + "§cVocê não pode usar este comando dentro do gladiador");
			API.getApi().playSound(p, Sound.VILLAGER_NO);
		}
		
	}
	
	@EventHandler
	void onPlayerBreakBlockEvent(BlockBreakEvent e) {
		Player p = e.getPlayer();
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		if (!p.hasPermission("*") && gp != null && gp.isInGladiator()) e.setCancelled(true);
		
	}
	
	@EventHandler
	void onPlayerPlaceBlockEvent(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		if (!p.hasPermission("*") && gp != null && gp.isInGladiator()) e.setCancelled(true);
	}
	
}
