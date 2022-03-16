/*
 * 
 */
package me.gugafenix.legionmc.glad.cabin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.objects.Gladiator;

public class Cabin {
	private Location location;
	private Gladiator gladiator;
	private List<Player> players;
	
	public Cabin(Gladiator glad) {
		this.players = new ArrayList<>();
		this.gladiator = glad;
		this.location = unserialize(glad.getPreset().getConfig().getString("Camarote"));
	}
	
	public Location unserialize(String string) {
		String args[] = string.split(":");
		Double X = Double.parseDouble(args[0]);
		Double Y = Double.parseDouble(args[1]);
		Double Z = Double.parseDouble(args[2]);
		
		return new Location(gladiator.getWorld(), X, Y, Z);
	}
	
	public Location getLocation() { return location; }
	
	public void setLocation(Location location) { this.location = location; }
	
	public Gladiator getGladiator() { return gladiator; }
	
	public void setGladiator(Gladiator gladiator) { this.gladiator = gladiator; }
	
	public List<Player> getPlayers() { return players; }
	
	public void setPlayers(List<Player> players) { this.players = players; }
	
}
