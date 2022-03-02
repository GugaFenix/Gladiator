package me.gugafenix.legionmc.glad.spawnselection.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;

public class BugOnSelectEvent implements Listener {
	
	private SpawnSelect select;
	
	@EventHandler
	void onPlayerDropBlockEvent(PlayerDropItemEvent e) {
		
		Player p = e.getPlayer();
		ItemStack item = e.getItemDrop().getItemStack();
		select = SpawnSelectManager.getManager().getSelect(p);
		
		if (select == null) return;
		if (select.getWorld() != p.getWorld()) return;
		if (item == null || item.getType() != Material.ENDER_PORTAL_FRAME) return;
		
		boolean hasMeta = p.getItemInHand().hasItemMeta();
		boolean hasName = p.getItemInHand().getItemMeta().hasDisplayName();
		boolean hasComponents = hasMeta && hasName;
		
		if (hasComponents) {
			if (!item.getItemMeta().getDisplayName().equalsIgnoreCase("§aAdicionar spawn point")) return;
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	void onPlayerDamageEvent(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		
		Player p = (Player) e.getEntity();
		select = SpawnSelectManager.getManager().getSelect(p);
		
		if (select == null) return;
		
		e.setCancelled(true);
	}
	
	@EventHandler
	void onCommandEvent(PlayerCommandPreprocessEvent e) {
		
		Player p = e.getPlayer();
		select = SpawnSelectManager.getManager().getSelect(p);
		
		if (select == null) return;
		
		p.sendMessage("§cVocê não pode executar comandos durante a seleção de spawns");
		e.setCancelled(true);
	}
	
}
