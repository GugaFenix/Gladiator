package me.gugafenix.legionmc.glad.spawnselection.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;
import me.gugafenix.legionmc.glad.utils.API;

public class PlayerBreakBlock implements Listener {
	
	@EventHandler
	void onPlayerBreakBlockEvent(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block block = e.getBlock();
		
		if (block == null || block.getType() != Material.ENDER_PORTAL_FRAME) return;
		
		SpawnSelect select = SpawnSelectManager.getManager().getSelect(p);
		if (select == null) return;
		
		List<Location> locs = select.getLocations();
		if (!locs.contains(block.getLocation())) return;
		
		locs.remove(block.getLocation());
		API.getApi().playSound(p, Sound.PISTON_RETRACT);
		
	}
	
}
