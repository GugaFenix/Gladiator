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
import org.bukkit.scheduler.BukkitRunnable;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;
import me.gugafenix.legionmc.glad.utils.API;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;

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
			
			p.getInventory().setItem(4, e.getItemInHand());
			
			if (select.getWorld() != p.getWorld()) return;
			if (!e.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§aAdicionar spawn point")) return;
			
			List<Location> locs = select.getLocations();
			locs.add(block.getLocation());
			Location loc = block.getLocation();
			
			new BukkitRunnable() {
				int stage = 0;
				
				@Override
				public void run() {
					if (loc.getBlock().getType() != Material.ENDER_PORTAL_FRAME) {
						this.cancel();
						return;
					}
					
					if (stage > 7) {
						p.sendMessage("§aSpawnpoint definido!");
						API.getApi().playSound(p, Sound.ITEM_BREAK);
						
						PacketPlayOutBlockBreakAnimation pb = new PacketPlayOutBlockBreakAnimation(1,
								new BlockPosition(loc.getX(), loc.getY(), loc.getZ()), 10);
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(pb);
						
						this.cancel();
						return;
					}
					
					PacketPlayOutBlockBreakAnimation pb = new PacketPlayOutBlockBreakAnimation(1,
							new BlockPosition(loc.getX(), loc.getY(), loc.getZ()), stage);
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(pb);
					stage++;
				}
			}.runTaskTimerAsynchronously(Main.getMain(), 0, 10);
		}
	}
	
}
