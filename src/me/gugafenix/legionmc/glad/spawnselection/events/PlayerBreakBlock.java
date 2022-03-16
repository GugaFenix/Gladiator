/*
 * 
 */
package me.gugafenix.legionmc.glad.spawnselection.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;

public class PlayerBreakBlock implements Listener {
	
	@EventHandler
	void onPlayerBreakBlockEvent(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block block = e.getBlock();
		
		SpawnSelect select = SpawnSelectManager.getManager().getSelect(p);
		if (select == null) return;
		
		List<Location> locs = select.getLocations();
		if (!locs.contains(block.getLocation())) {
			e.setCancelled(true);
			return;
		}
		
		Location loc = block.getLocation();
		locs.remove(loc);
		
		p.sendMessage("§cSpawn removido.");
	}
	
}
