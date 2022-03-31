/*
 * 
 */
package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.file.File;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelect;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;
import me.gugafenix.legionmc.glad.utils.Messages;
import me.gugafenix.legionmc.glad.utils.PlaceHolder;

public class SetSpawnPoints {
	
	public void execute(Player p, String cmd, String[] args) {
		
		if (!p.hasPermission("*")) {
			p.sendMessage(PlaceHolder.replace(p, Messages.permission));
			return;
		}
		
		if (SpawnSelectManager.getManager().getSelect(p) != null) {
			p.sendMessage("§cVocê já está em um processo de seleção de spawnpoints");
			return;
		}
		
		File file = Main.getFileManager().getFile(args[1].replace(".yml", "") + ".yml");
		
		if (file == null) {
			p.sendMessage("§cArquivo preset não encontrado");
			return;
		}
		
		World world = Bukkit.getWorld(file.getConfig().getString("Evento.Mundo"));
		new SpawnSelect(p, world, file).startSelection();
		
	}
	
}
