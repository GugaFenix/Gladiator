package me.gugafenix.legionmc.glad.commands.arguments;

import java.io.File;

import org.bukkit.command.CommandSender;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.tasks.Tasks.TaskId;

public class Start {
	
	public void execute(CommandSender sender, String cmd, String[] args) {
		
		
		// N�o tem permiss�o
		if (!sender.hasPermission("*")) {
			sender.sendMessage(Main.tag + "§cVocê não tem permissão para isto.");
			return;
		}
		
		// j� tem um glad rodando
		if (Gladiator.HasGladRunning()) {
			sender.sendMessage(Main.tag + "§cO servidor já possui um evento gladiador ocorrendo");
			return;
		}
		
		// N�o definiu o preset
		if (args.length == 1) sender.sendMessage(Main.tag
				+ "§fPara iniciar um evento, é preciso definir qual arquivo de preset ser� usado. \n§6Use /Glad criar para mais informaçõees.");
		else {
			
			File ioFile = new File(Main.getMain().getDataFolder().getAbsolutePath() + "/Presets/" + args[1].replace(".yml", "") + ".yml");
			
			// O preset existe
			if (ioFile.exists()) {
				
				me.gugafenix.legionmc.glad.file.File file = Main.getFileManager().getFile(ioFile.getName());
				Gladiator glad = new Gladiator(file);
				glad.runTask(TaskId.SEND_WARNS);
				sender.sendMessage(Main.tag + "§aGladiador sendo iniciado...");
				sender.sendMessage(Main.tag + "§cPara cancela-lo use §7/Glad cancelar");
				
			} else sender.sendMessage(Main.tag + "§cArquivo do preset não encontrado!");
			
		}
	}
	
}
