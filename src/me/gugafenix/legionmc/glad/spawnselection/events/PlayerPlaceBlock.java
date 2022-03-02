package me.gugafenix.legionmc.glad.spawnselection.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;
import me.gugafenix.legionmc.glad.utils.API;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class PlayerPlaceBlock implements Listener {
	
	@EventHandler
	void onPlayerPlaceBlockEvent(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block block = e.getBlockPlaced();
		
		if (block == null || block.getType() != Material.ENDER_PORTAL_FRAME) return;
		
		boolean hasMeta = p.getItemInHand().hasItemMeta();
		boolean hasName = p.getItemInHand().getItemMeta().hasDisplayName();
		boolean hasComponents = hasMeta && hasName;
		
		SpawnSelect select = SpawnSelectManager.getManager().getSelect(p);
		
		if (hasComponents) {
			if (select == null) return;
			if (select.getWorld() != p.getWorld()) return;
			if (!e.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§aAdicionar spawn point")) return;
			
			List<Location> locs = select.getLocations();
			locs.add(block.getLocation());
			API.getApi().playSound(p, Sound.LEVEL_UP);
			
			
			Location loc = block.getLocation();
			
			for (int i = loc.getBlockY(); i < loc.getBlockY() + 5; i++) {
				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.LAVA, true, (float) (loc.getX() + 500),
						(float) (i), (float) (loc.getZ() + 500), (float) 0, (float) 0, (float) 0, (float) 0, 1);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			}
			
		}
		
	}
	
}
