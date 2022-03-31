package me.gugafenix.legionmc.glad.border;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.objects.Gladiator;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_8_R3.WorldBorder;

public class BorderManager {
	private List<Player> players;
	double startSize;
	private int lessSize;
	private int lessTime;
	private Location center;
	private World world;
	private WorldBorder border;
	private WorldBorder lastBorder;
	private Gladiator glad;
	
	public BorderManager(World world, List<Player> players, Gladiator glad) {
		this.setGlad(glad);
		this.players = players;
		this.world = world;
		this.border = new WorldBorder();
		this.lastBorder = null;
		
		if (world == glad.getWorld()) create("Arena");
		else create("DeathMatch");
	}
	
	public void create(String ag) {
		border.setSize(getGlad().getStartSize());
		FileConfiguration config = glad.getPreset().getConfig();
		border.setCenter(config.getInt("Borda." + ag + ".Centro.X"), config.getInt("Borda." + ag + ".Centro.Z"));
		border.setDamageAmount(config.getInt("Borda." + ag + ".Dano"));
		border.setWarningDistance(3);
		sendBorderPacket();
	}
	
	public void update(float less, String ag) {
		
		remove();
		border = new WorldBorder();
		FileConfiguration config = glad.getPreset().getConfig();
		border.setDamageAmount(config.getInt("Borda." + ag + ".Dano"));
		border.setCenter(config.getInt("Borda." + ag + ".Centro.X"), config.getInt("Borda." + ag + ".Centro.Z"));
		border.setSize(lastBorder.getSize() - less);
		border.setWarningDistance(5);
		border.setDamageAmount(glad.getBorderDamage());
		sendBorderPacket();
	}
	
	private void remove() {
		if (border == null) return;
		this.lastBorder = border;
		this.border = null;
	}
	
	public void sendBorderPacket() {
		for (Player p : players) {
			CraftPlayer cp = ((CraftPlayer) p);
			PacketPlayOutWorldBorder pkt = new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.INITIALIZE);
			cp.getHandle().playerConnection.sendPacket(pkt);
		}
	}
	
	public List<Player> getPlayers() { return players; }
	
	public void setPlayers(List<Player> players) { this.players = players; }
	
	public double getStartSize() { return startSize; }
	
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
	
	public Gladiator getGlad() { return glad; }
	
	public void setGlad(Gladiator glad) { this.glad = glad; }
	
}
