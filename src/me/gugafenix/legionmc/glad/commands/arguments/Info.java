package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.entity.Player;

import me.HClan.Objects.Clan;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;

public class Info {
	
	public void execute(Player p, String cmd, String[] args) {
		
		Gladiator glad = Gladiator.getGladRunning();
		
		if (glad == null) {
			p.sendMessage(Main.tag + "§cO servidor não possui nenhum gladiador ocorrendo");
			return;
		}
		
		p.sendMessage("§3§l[§b§lBoletim do Gladiador§3§l]");
		p.sendMessage("§2Quantidade de clãs: §a" + glad.getClans().size());
		p.sendMessage("§7Clã com mais kills: §7" + getBestClan(glad)[0]);
		p.sendMessage("§6Clã com mais membros: §e" + getBestClan(glad)[1]);
		p.sendMessage("§3Tempo de duração: §b" + getDurationTime(glad));
		p.sendMessage("§4§l[§cClãs sobreviventes§4§l]");
		p.sendMessage(getClanList(glad));
		
	}
	
	private String[] getBestClan(Gladiator glad) {
		
		for (Clan clan : glad.getClans()) {
			int kills = 0;
			int members = 0;
			
			for (Player p : clan.getOnlinePlayers()) {
				GladPlayer gp = Main.getPlayerManager().getPlayer(p);
				if (gp == null) continue;
				kills += gp.getKills();
				
				if (gp.isWatching()) continue;
				members++;
			}
			
			return new String[] { String.valueOf(kills), String.valueOf(members) };
		}
		return null;
		
	}
	
	private String getDurationTime(Gladiator glad) {
		long seconds = ((glad.getStartMilis() - System.currentTimeMillis()) * 1000);
		long minutes = ((glad.getStartMilis() - System.currentTimeMillis()) * 1000 * 60);
		long hours = ((glad.getStartMilis() - System.currentTimeMillis()) * 1000 * 60 * 60);
		
		if (hours >= 1) return hours + " horas";
		if (minutes >= 1) return minutes + " minutos";
		return seconds + " segundos";
		
	}
	
	private String getClanList(Gladiator glad) {
		StringBuilder sb = new StringBuilder(glad.getClans().get(0).getTagClan());
		for (int i = 1; i < glad.getClans().size(); i++) {
			if (i == glad.getClans().size() - 1) {
				sb.append(" §fe " + glad.getClans().get(i));
				continue;
			}
			sb.append(", " + glad.getClans().get(i));
		}
		return sb.toString();
	}
	
}
