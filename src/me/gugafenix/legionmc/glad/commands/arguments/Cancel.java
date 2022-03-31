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
import me.gugafenix.legionmc.glad.utils.Messages;
import me.gugafenix.legionmc.glad.utils.PlaceHolder;

public class Cancel {
	
	public void execute(Player p, String cmd, String[] args) {
		
		if (!p.hasPermission("*")) {
			p.sendMessage(PlaceHolder.replace(p, Messages.permission));
			return;
		}
		
		if (!Gladiator.hasGladRunning()) {
			p.sendMessage(Main.tag + "§cNenhum comando encontrado");
			API.getApi().playSound(p, Sound.VILLAGER_NO);
			return;
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(PlaceHolder.replace(p, Messages.cancelled));
			API.getApi().playSound(player, Sound.ENDERDRAGON_GROWL);
		}
		
		Gladiator.getGladRunning().cancel();
		
	}
	
}
