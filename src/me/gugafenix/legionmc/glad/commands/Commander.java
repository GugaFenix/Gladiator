package me.gugafenix.legionmc.glad.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.commands.arguments.Cancel;
import me.gugafenix.legionmc.glad.commands.arguments.Create;
import me.gugafenix.legionmc.glad.commands.arguments.Help;
import me.gugafenix.legionmc.glad.commands.arguments.Join;
import me.gugafenix.legionmc.glad.commands.arguments.Leave;
import me.gugafenix.legionmc.glad.commands.arguments.SelectWarriors;
import me.gugafenix.legionmc.glad.commands.arguments.Start;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;

public class Commander extends Command {
	
	public Commander() { super("GLADIADOR"); }
	
	@Override
	public boolean execute(CommandSender sender, String cmd, String[] args) {
		
		if (!(sender instanceof Player)) return false;
		Player p = (Player) sender;
		
		if (args.length > 0) {
			switch (preparedArgument(args[0]).toLowerCase()) {
			
			case "help":
				new Help().execute(sender, cmd, args);
				break;
			case "start":
				new Start().execute(sender, cmd, args);
				break;
			case "create":
				new Create().execute(sender, cmd, args);
				break;
			case "selecionarguerreiros":
				if (!Gladiator.HasGladRunning()) {
					p.sendMessage(Main.tag + "§cNão há nenhum gladiador ocorrendo.");
					p.playSound(p.getLocation(), Sound.VILLAGER_NO, 10, 10);
					return false;
				}
				new SelectWarriors().execute(sender, cmd, args);
				break;
			case "join":
				if (!Gladiator.HasGladRunning()) {
					p.sendMessage(Main.tag + " §cNão há nenhum gladiador ocorrendo.");
					p.playSound(p.getLocation(), Sound.VILLAGER_NO, 10, 10);
					return false;
				}
				
				if (new Join().execute(sender, cmd, args)) {
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
				} else p.playSound(p.getLocation(), Sound.VILLAGER_NO, 10, 10);
				break;
			case "cancel":
				new Cancel().execute(p, cmd, args);
				break;
			case "leave":
				new Leave().execute(p, cmd, args);
				break;
			default:
				p.sendMessage(Main.tag + "§cComando não encontrado");
				p.playSound(p.getLocation(), Sound.VILLAGER_NO, 10, 10);
				break;
			
			}
			
		} else new Help().execute(sender, cmd, args);
		
		return false;
	}
	
	private String preparedArgument(String string) {
		
		String arg = string;
		
		if (arg.equalsIgnoreCase("ajuda") || arg.equalsIgnoreCase("comandos")) return "help";
		else if (arg.equalsIgnoreCase("iniciar") || arg.equalsIgnoreCase("come�ar")) return "start";
		else if (arg.equalsIgnoreCase("criar") || arg.equalsIgnoreCase("gerar")) return "create";
		else if (arg.equalsIgnoreCase("cancelar") || arg.equalsIgnoreCase("parar")) return "cancel";
		else if (arg.equalsIgnoreCase("forcarinicio") || arg.equalsIgnoreCase("forcarstart")) return "forcestart";
		else if (arg.equalsIgnoreCase("forcardeathmatch")) arg = "forcedeathmatch";
		else if (arg.equalsIgnoreCase("for�arvencedor") || arg.equalsIgnoreCase("for�arwinner")) return "forcewinner";
		else if (arg.equalsIgnoreCase("top")) arg = "cancel";
		else if (arg.equalsIgnoreCase("puxar") || arg.equalsIgnoreCase("teleport")) return "warp";
		else if (arg.equalsIgnoreCase("entrar") || arg.equalsIgnoreCase("participar")) return "join";
		else if (arg.equalsIgnoreCase("sair") || arg.equalsIgnoreCase("quitar")) return "leave";
		else if (arg.equalsIgnoreCase("assistir") || arg.equalsIgnoreCase("camarote")) return "watch";
		else if (arg.equalsIgnoreCase("info")) return "log";
		else return arg;
		
		return null;
	}
	
}
