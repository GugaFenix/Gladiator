/*
 * 
 */
package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.objects.Timer;

public class Reload {
	
	public void execute(CommandSender sender, String cmd, String[] args) {
		
		if (!sender.hasPermission("*") && sender == Bukkit.getConsoleSender()) sender.sendMessage("§cVocê não tem permissão para isto");
		
		Main.getFileManager().loadConfigs();
		if (Gladiator.hasGladRunning()) Gladiator.getGladRunning().cancel();
		Gladiator.setGladRunning(null);
		
		Timer.getTimer().start();
		
		sender.sendMessage(Main.tag + "§aO plugin de gladiador foi recarregado com êxito!");
	}
	
}
