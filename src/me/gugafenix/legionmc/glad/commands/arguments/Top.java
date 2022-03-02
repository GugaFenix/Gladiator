package me.gugafenix.legionmc.glad.commands.arguments;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.HClan.Managers.Manager;
import me.HClan.MenuPodio.menuTopClansGladiadores;
import me.HClan.Objects.Clan;
import me.HClan.Objects.Jogador;
import me.gugafenix.legionmc.glad.main.Main;

public class Top {
	
	public void execute(Player p, String cmd, String[] args) {
		
		switch (preparedArgument(args[1].toLowerCase())) {
		case "help":
			p.sendMessage("§3Gladiador Top Help");
			p.sendMessage("§b/top reset §f- §bReseta o top gladiador");
			p.sendMessage("§b/top clã <clã> §f- §bExibe a posição do clã no gladiador");
			p.sendMessage("§b/top help §f- §bExibe os comandos relacionados ao top");
			p.sendMessage("§b/top §f- §bAbre o menu do top gladiador");
			break;
		case "reset":
			menuTopClansGladiadores.get().setTop(new ArrayList<Clan>());
			p.sendMessage("§5Top gladiadores resetado!");
			break;
		case "clan":
			
			if (args[2] == null) {
				if (Jogador.check(p).getClan() == null) {
					p.sendMessage(Main.tag + "§cVocê não tem um clã");
					return;
				}
			}
			
			if (!menuTopClansGladiadores.get().getTop().contains(Clan.get(args[2]))) {
				if (Manager.get().getClanByTag(args[2]) == null) {
					p.sendMessage(Main.tag + "§cO seu clã não está em nenhuma posição");
					return;
				}
			}
			p.sendMessage(Main.tag + "§bA posição do seu clã no gladiador é: §f"
					+ menuTopClansGladiadores.get().getTop().indexOf(Clan.get(args[2])) + 1);
			break;
		default:
			
			menuTopClansGladiadores.get().open(p);
			
			break;
		}
		
	}
	
	private String preparedArgument(String string) {
		
		String arg = string;
		
		if (arg.equalsIgnoreCase("ajuda") || arg.equalsIgnoreCase("comandos")) return "help";
		if (arg.equalsIgnoreCase("clear") || arg.equalsIgnoreCase("resetar")) return "reset";
		if (arg.equalsIgnoreCase("clear") || arg.equalsIgnoreCase("resetar")) return "clan";
		else return null;
	}
	
}
