package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.file.File;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.utils.API;

public class Create {
	public void execute(CommandSender sender, String cmd, String[] args) {
		Player p = (Player) sender;
		
		if (!p.hasPermission("*")) {
			p.sendMessage("§cVocê não tem permissão para isto");
			return;
		}
		
		if (args.length > 1) {
			if (Main.getFileManager().getFile(args[1].replace(".yml", "") + ".yml") == null) {
				String name = args[1].replace(".yml", "") + ".yml";
				Main.getFileManager().saveResource("Default.yml", Main.getMain().getDataFolder() + "/Presets/", name, false);
				new File(name, Main.getMain().getDataFolder() + "/Presets/" + name);
				p.sendMessage(Main.tag + "§aArquivo preset criado! Para edita-lo v� até §7'Plugins/Gladiator/Presets/" + name + "' §a.");
				API.getApi().playSound(p, Sound.ANVIL_USE, Sound.CREEPER_HISS);
			} else {
				p.sendMessage(Main.tag + "§cJá existe um preset com este nome!");
				API.getApi().playSound(p, Sound.VILLAGER_NO, Sound.ARROW_HIT);
			}
		} else {
			p.sendMessage(Main.tag + "§cUse /Glad criar <nome do preset>, para criar um novo arquivo preset do gladiador.");
			return;
		}
	}
}
