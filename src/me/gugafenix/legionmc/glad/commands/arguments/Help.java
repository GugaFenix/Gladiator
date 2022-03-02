package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.main.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Help {
	
	public void execute(CommandSender sender, String cmd, String[] args) {
		System.out.println("Called help argument");
		Player p = (Player) sender;
		String tag = Main.tag;
		
		if (args.length > 1) {
			
			if (args[1].equalsIgnoreCase("jogador")) {
				
				String string = "participar - Entra no gladiador,camarote - Entra no evento em modo espectador,sair - Sai do gladiador caso ainda n�o tenha iniciado,top - Exibe os 10 cl�s com a melhor coloca��o de vit�rias,info - Exibe os cl�s e a quantidade de jogadores restantes no evento";
				for (int i = 0; i < 200; i++) p.sendMessage("");
				p.sendMessage(tag + "§d§lComandos do player");
				
				for (String msg : string.split(",")) p.sendMessage("§7/Glad §b" + msg.replace("-", "§7-§b"));
				
			} else if (args[1].equalsIgnoreCase("equipe")) {
				
				String string = "Criar - Gera um template de gladiador,iniciar - Inicia o evento,parar - Cancela o evento,forcestart - For�ar o in�cio do gladiador,Forcedeathmatch - For�a o deathmatch,Forcevencedor <tag> - For�a um cl� vencedor do gladiador,Top set - Seta o cl� top 1,Kick - Expulsa um player do gladiador,Top reset - Reseta o top vit�rias do gladiador,Puxar - Transfere todos os jogadores do gladiador para outro lugar";
				for (int i = 0; i < 200; i++) p.sendMessage("");
				p.sendMessage(tag + "§3§lComandos da equipe");
				
				for (String msg : string.split(",")) p.sendMessage("§7/Glad §b" + msg.replace("-", "§7-§b"));
				
			}
			
		} else {
			p.sendMessage("");
			p.sendMessage(tag + "§aQuais comandos você deseja ver?");
			TextComponent playerCommands = new TextComponent("§dComandos do jogador.");
			playerCommands.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/gladiador help jogador"));
			playerCommands.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("§aClique aqui para ver os comandos do jogador.").create()));
			p.sendMessage("");
			p.spigot().sendMessage(playerCommands);
			TextComponent staffCommands = new TextComponent("§bComandos da staff.");
			staffCommands.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/gladiador help equipe"));
			staffCommands.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("§aClique aqui para ver os comandos da equipe.").create()));
			p.spigot().sendMessage(staffCommands);
			
		}
		
	}
	
}
