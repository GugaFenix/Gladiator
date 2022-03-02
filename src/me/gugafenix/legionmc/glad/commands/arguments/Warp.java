package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;

public class Warp {
	
	public void execute(Player p, String cmd, String[] args) {
		
		Gladiator glad = Gladiator.getGladRunning();
		
		if (!p.hasPermission("*")) {
			p.sendMessage("§cVocê não tem permissão para isto");
			return;
		}
		
		if (args.length < 2) {
			p.sendMessage("§cUse /glad warp <camarote/arena>");
			return;
		}
		
		switch (args[1].toLowerCase()) {
		case "camarote":
			for (Player wp : glad.getCabin().getPlayers()) {
				wp.teleport(p);
				wp.sendMessage("§bO camarote todo foi teleportado até a localização de §f" + p.getName());
				wp.playSound(wp.getLocation(), Sound.ENDERMAN_TELEPORT, 10.0f, 10.0f);
				p.sendMessage("§aJogadores teleportados!");
				p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 10.0f, 10.0f);
			}
			break;
		case "arena":
			for (GladPlayer gp : glad.getPlayers()) {
				Player wp = gp.getPlayer();
				wp.teleport(p);
				wp.sendMessage("§bTodos os jogadores da arena foram teleportados até a localização de §f" + p.getName());
				wp.playSound(wp.getLocation(), Sound.ENDERMAN_TELEPORT, 10.0f, 10.0f);
				p.sendMessage("§aJogadores teleportados!");
				p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 10.0f, 10.0f);
			}
			break;
		default:
			p.sendMessage("§cUse /glad warp <camarote/arena>");
			break;
		}
		
	}
	
}
