/*
 * 
 */
package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;

public class Watch {
	
	public void execute(Player p, String cmd, String[] args) {
		
		// Vars
		String tag = Main.tag;
		Gladiator glad = Gladiator.getGladRunning();
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		
		if (glad == null) {
			p.sendMessage(tag + "§cNão há nenhum evento gladiador ocorrendo.");
			return;
		}
		
		if (gp == null) return;
		if (gp.isInGladiator()) {
			p.sendMessage(tag + "§cVocê está em batalha no gladiador, para sair use /glad sair");
			return;
		}
		
		if (gp.isWatching()) {
			p.sendMessage(tag + "§cVocê já está no camarote do gladiador");
			return;
		}
		
		p.teleport(glad.getCabin().getLocation());
		glad.getCabin().getPlayers().add(p);
		gp.setWatching(true);
		gp.setInGladiator(true);
		gp.updateInfos();
		
	}
	
}
