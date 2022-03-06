package me.gugafenix.legionmc.glad.border;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.objects.Gladiator;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_8_R3.WorldBorder;

public class BorderManager {
	private List<Player> players;
	private int startSize, lessSize;
	private int lessTime;
	private Location center;
	private World world;
	private WorldBorder border;
	private WorldBorder lastBorder;
	
	public BorderManager(World world, List<Player> players) {
		this.players = players;
		this.world = world;
		this.border = new WorldBorder();
		this.lastBorder = null;
	}
	
	public void update(int less) {
		remove();
		border = new WorldBorder();
		border.setCenter(center.getX(), center.getZ());
		border.setSize(lastBorder.getSize() - less);
		border.setWarningDistance(3);
		
		for (Player p : players) {
			CraftPlayer cp = ((CraftPlayer) p);
			PacketPlayOutWorldBorder pktinitialize = new PacketPlayOutWorldBorder((WorldBorder) border, EnumWorldBorderAction.INITIALIZE);
			PacketPlayOutWorldBorder pktcenter = new PacketPlayOutWorldBorder((WorldBorder) border, EnumWorldBorderAction.SET_CENTER);
			PacketPlayOutWorldBorder pktsize = new PacketPlayOutWorldBorder((WorldBorder) border, EnumWorldBorderAction.SET_SIZE);
			PacketPlayOutWorldBorder pktdistance = new PacketPlayOutWorldBorder((WorldBorder) border,
					EnumWorldBorderAction.SET_WARNING_BLOCKS);
			
			cp.getHandle().playerConnection.sendPacket(pktinitialize);
			cp.getHandle().playerConnection.sendPacket(pktcenter);
			cp.getHandle().playerConnection.sendPacket(pktsize);
			cp.getHandle().playerConnection.sendPacket(pktdistance);
		}
	}
	
	private void remove() {
		if (border == null) return;
		this.lastBorder = border;
		this.border = null;
		PacketPlayOutWorldBorder pktinitialize = new PacketPlayOutWorldBorder((WorldBorder) border, EnumWorldBorderAction.INITIALIZE);
		for (Player p : players) { ((CraftPlayer) p).getHandle().playerConnection.sendPacket(pktinitialize); }
	}
	
	public List<Player> getPlayers() { return players; }
	
	public void setPlayers(List<Player> players) { this.players = players; }

	public int getStartSize() { return startSize; }

	public void setStartSize(int startSize) { this.startSize = startSize; }

	public int getLessSize() { return lessSize; }

	public void setLessSize(int lessSize) { this.lessSize = lessSize; }

	public int getLessTime() { return lessTime; }

	public void setLessTime(int lessTime) { this.lessTime = lessTime; }

	public Location getCenter() { return center; }

	public void setCenter(Location location) { this.center = location; }

	public World getWorld() { return world; }

	public void setWorld(World world) { this.world = world; }

	public WorldBorder getBorder() { return border; }

	public void setBorder(WorldBorder border) { this.border = border; }
	
	
	
}
