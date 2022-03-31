package me.gugafenix.legionmc.glad.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.HClan.ListenersPlugin.PlayerDamageClanAlly;
import me.HClan.ListenersPlugin.PlayerDamageClanMember;
import me.HClan.Objects.Jogador;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.player.GladPlayer;

public class PlayerCauseDamageEvent implements Listener {
	
	@EventHandler
	void onPlayerCauseDamageInFriend(PlayerDamageClanMember e) {
		
		Player p = e.getAttacker().getPlayer();
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		if (!p.hasPermission("*") && gp != null && gp.isInGladiator()) { e.setCancelled(true); }
		
	}
	
	@EventHandler
	void onPlayerCauseDamageInAlly(PlayerDamageClanAlly e) {
		
		Jogador jogador = e.getAttacker();
		Player p = jogador.getPlayer();
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		if (!p.hasPermission("*") && gp != null && gp.isInGladiator()) {
			if (jogador.getClan().getFogoAmigo()) return;
			jogador.getClan().setFogoAmigo(true);
		}
		
	}
	
	@EventHandler
	void onDamageEvent(EntityDamageEvent e) {
		GladPlayer gp = Main.getPlayerManager().getPlayer(((Player) e.getEntity()));
		if (gp == null) return;
		if (gp.isInGladiator() && !gp.isWatching()) return;
		
		e.setCancelled(true);
	}
}
