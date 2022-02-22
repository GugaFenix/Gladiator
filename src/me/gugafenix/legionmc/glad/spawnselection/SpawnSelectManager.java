package me.gugafenix.legionmc.glad.spawnselection;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.main.Main;

public class SpawnSelectManager {
	
	private List<SpawnSelect> selects;
	
	public SpawnSelectManager() { selects = new ArrayList<>(); }
	
	public List<SpawnSelect> getSelects() { return selects; }
	
	public SpawnSelect getSelect(Player p) {
		ListIterator<SpawnSelect> it = getSelects().listIterator();
		while (it.hasNext()) {
			SpawnSelect ss = it.next();
			if (ss.getPlayer().getName().contentEquals(p.getName())) return ss;
		}
		return null;
	}
	
	public static SpawnSelectManager getManager() { return Main.getSpawnsManager(); }
	
}
