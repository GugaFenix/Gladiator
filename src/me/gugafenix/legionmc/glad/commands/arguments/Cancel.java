/*
 * 
 */
package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.utils.API;

public class Cancel {
	
	public void execute(Player p, String cmd, String[] args) {
		
		if (!p.hasPermission("*")) {
			p.sendMessage("§cVocê não tem permissão para isto");
			return;
		}
		
		if (!Gladiator.hasGladRunning()) {
			p.sendMessage(Main.tag + "§cNenhum comando encontrado");
			API.getApi().playSound(p, Sound.VILLAGER_NO);
			return;
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(Main.tag + "§cO evento gladiador foi cancelado!");
			API.getApi().playSound(player, Sound.ENDERDRAGON_GROWL);
			API.getApi().sendTitle(Main.tag, "§b§lSE LIGA NO CHAT", player);
		}
		
		Gladiator.getGladRunning().cancel();
		
	}
	
}
