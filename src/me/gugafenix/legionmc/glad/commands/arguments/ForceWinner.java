/*
 * 
 */
package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.HClan.Objects.Clan;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.utils.API;

public class ForceWinner {
	
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
			p.sendMessage(Main.tag + "§cUse /glad forcewinner <clã>");
			return;
		}
		
		if (Bukkit.getPlayer(args[1]) == null || gp == null) {
			p.sendMessage(Main.tag + "§cClan não presente no gladiador");
			return;
		}
		
		Clan clan = Clan.get(args[1]);
		
		if (clan == null) {
			p.sendMessage(Main.tag + "§cClan inexistente");
			return;
		}
		
		glad.setWinner(clan);
		p.sendMessage("§bClan §f" + clan.getTagClan() + " §bdefinido como ganhador");
		API.getApi().playSound(p, Sound.ARROW_HIT);
		
	}
	
}
