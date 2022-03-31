/*
 * 
 */
package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.utils.Messages;
import me.gugafenix.legionmc.glad.utils.PlaceHolder;

public class Reload {
	
	public void execute(CommandSender sender, String cmd, String[] args) {
		
		if (!sender.hasPermission("*") && sender == Bukkit.getConsoleSender()) {
			sender.sendMessage(PlaceHolder.replace((Player) sender, Messages.permission));
			return;
		}
		
		Main.getFileManager().loadConfigs();
		if (Gladiator.hasGladRunning()) Gladiator.getGladRunning().cancel();
		Gladiator.setGladRunning(null);
		sender.sendMessage(Main.tag + "§aO plugin de gladiador foi recarregado com êxito!");
	}
	
}
