package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.utils.API;

public class Kick {
	
	public void execute(Player p, String cmd, String[] args) {
		Gladiator glad = Gladiator.getGladRunning();
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		
		if (!p.hasPermission("*")) {
			p.sendMessage("§cVocê não tem permissão para isto");
			return;
		}
		
		if (glad == null) {
			p.sendMessage(Main.tag + "§cO servidor não possui nenhum gladiador ocorrendo");
			return;
		}
		
		if (args.length < 2) {
			p.sendMessage(Main.tag + "§cUse /glad kick <player> [motivo]");
			return;
		}
		
		if (Bukkit.getPlayer(args[1]) == null || gp == null) {
			p.sendMessage(Main.tag + "§cJogador não presente no gladiador.");
			return;
		}
		
		String cause;
		if (args[2] != null)
			cause = args[2].toLowerCase().replace(String.valueOf(args[2].charAt(0)), String.valueOf(args[2].charAt(0)).toUpperCase());
		else cause = "Não especificado";
		
		for (GladPlayer gps : glad.getPlayers()) {
			gps.getPlayer().sendMessage("§0§l[§6§lRemoção do gladiador§0§l]");
			gps.getPlayer().sendMessage("§cResponsável: §7" + p.getName());
			gps.getPlayer().sendMessage("§cPunido: §7" + gp.getPlayer().getName());
			gps.getPlayer().sendMessage("§cMotivo: §7" + cause);
			gps.getPlayer().sendMessage("§cClã: §7" + gp.getClan().getTagClan());
		}
		gp.deleteFromGladiator(glad);
		glad.updateInfoFromAll();
		p.sendMessage(Main.tag + "§cJogador expulso do gladiador!");
		API.getApi().playSound(p, Sound.ARROW_HIT);
	}
	
}
