/*
 * 
 */
package me.gugafenix.legionmc.glad.commands.arguments;

import java.util.HashMap;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.HClan.Objects.Clan;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.utils.ValueComparator;

public class Info {
	
	public void execute(CommandSender sender, String cmd, String[] args) {
		
		Gladiator glad = Gladiator.getGladRunning();
		
		if (glad == null) {
			sender.sendMessage(Main.tag + "§cO servidor não possui nenhum gladiador ocorrendo");
			return;
		}
		
		sender.sendMessage("§3§l[§b§lBoletim do Gladiador§3§l]");
		sender.sendMessage("§2Quantidade de clãs: §a" + glad.getClans().size());
		sender.sendMessage("§7Clã com mais kills: §7" + getBestClan(glad)[0]);
		sender.sendMessage("§6Clã com mais membros: §e" + getBestClan(glad)[1]);
		sender.sendMessage("§3Tempo de duração: §b" + getDurationTime(glad));
		sender.sendMessage("§4§l[§cClãs sobreviventes§4§l]");
		sender.sendMessage(getClanList(glad));
		
	}
	
	private String[] getBestClan(Gladiator glad) {
		
		if (glad.getClans().size() <= 1) return new String[] { glad.getClans().get(0).getTagClan(), glad.getClans().get(0).getTagClan() };
		
		HashMap<Clan, Integer> killMap = new HashMap<>();
		HashMap<Clan, Integer> memberMap = new HashMap<>();
		
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
			
			killMap.put(clan, kills);
			memberMap.put(clan, members);
			
		}
		ValueComparator vcp = new ValueComparator(killMap);
		TreeMap<Clan, Integer> tree = new TreeMap<>(vcp);
		
		String bestKiller = tree.firstKey().getTagClan();
		
		tree.clear();
		
		vcp = new ValueComparator(memberMap);
		tree = new TreeMap<>(vcp);
		String moreMembers = tree.firstKey().getTagClan();
		
		tree.clear();
		killMap.clear();
		memberMap.clear();
		
		return new String[] { bestKiller, moreMembers };
	}
	
	private String getDurationTime(Gladiator glad) {
		long miliSeconds = glad.getStartMilis() - System.currentTimeMillis();
		long seconds = miliSeconds * 1000;
		long minutes = seconds * 60;
		long hours = minutes * 60;
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
