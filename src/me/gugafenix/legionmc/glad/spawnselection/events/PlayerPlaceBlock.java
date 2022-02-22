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

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;
import me.gugafenix.legionmc.glad.utils.API;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;

public class PlayerPlaceBlock implements Listener {
	
	public PlayerPlaceBlock() { Main.registerEvent(new PlayerPlaceBlock()); }
	
	@EventHandler
	void onPlayerPlaceBlockEvent(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block block = e.getBlockPlaced();
		
		if (block == null || block.getType() != Material.ENDER_PORTAL) return;
		
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
			
			CraftPlayer cp = (CraftPlayer) p;
			
			cp.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityWeather(new EntityLightning(cp.getHandle().getWorld(),
					block.getLocation().getX(), block.getLocation().getY(), cp.getLocation().getZ(), false, false)));
		}
		
	}
	
}
