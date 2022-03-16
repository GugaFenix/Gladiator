/*
 * 
 */
package me.gugafenix.legionmc.glad.player;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.entity.Player;

public class GladPlayerManager {
	
	private List<GladPlayer> players;
	
	public GladPlayerManager() { players = new ArrayList<>(); }
	
	public List<GladPlayer> getPlayers() { return players; }
	
	public GladPlayer getPlayer(String name) {
		ListIterator<GladPlayer> it = players.listIterator();
		while (it.hasNext()) {
			GladPlayer pr = it.next();
			if (pr.getPlayer().getName().equalsIgnoreCase(name)) return pr;
		}
		return null;
	}
	
	public GladPlayer getPlayer(Player player) {
		ListIterator<GladPlayer> it = players.listIterator();
		while (it.hasNext()) {
			GladPlayer pr = it.next();
			if (pr.getPlayer() == player) return pr;
		}
		return null;
	}
	
}
