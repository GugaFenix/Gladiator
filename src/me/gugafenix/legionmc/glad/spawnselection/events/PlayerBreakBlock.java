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
import org.bukkit.event.block.BlockBreakEvent;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;
import me.gugafenix.legionmc.glad.utils.API;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class PlayerBreakBlock implements Listener {
	
	public PlayerBreakBlock() { Main.registerEvent(new PlayerBreakBlock()); }
	
	@EventHandler
	void onPlayerBreakBlockEvent(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block block = e.getBlock();
		
		if (block == null || block.getType() != Material.ENDER_PORTAL) return;
		
		SpawnSelect select = SpawnSelectManager.getManager().getSelect(p);
		
		if (select == null) return;
		
		List<Location> locs = select.getLocations();
		locs.remove(block.getLocation());
		API.getApi().playSound(p, Sound.PISTON_RETRACT);
		
		Location loc = block.getLocation();
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) (loc.getX()),
				(float) (loc.getY()), (float) (loc.getZ()), (float) 0, (float) 0, (float) 0, (float) 0, 1);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	
}
