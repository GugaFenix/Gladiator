/*
 * 
 */
package me.gugafenix.legionmc.glad.utils;

import org.bukkit.entity.Player;

import me.HClan.Objects.Clan;
import me.HClan.Objects.Jogador;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;

public class PlaceHolder extends PlaceholderExpansion {
	
	@Override
	public String getAuthor() { return "GugaFenix"; }
	
	@Override
	public String getIdentifier() { return "glad"; }
	
	@Override
	public String getVersion() { return "v0.1"; }
	
	@Override
	public String onPlaceholderRequest(Player p, String params) {
		Jogador player = Jogador.check(p);
		if (player == null) player = new Jogador(p.getName());
		String string = params.replace("&", "�");
		
		if (Gladiator.hasGladRunning()) {
			if (params.toLowerCase().equalsIgnoreCase("tag")) return Main.tag;
			else if (params.toLowerCase().equalsIgnoreCase("clan")) {
				
				if (player.getClan() != null) return player.getClan().getTagClan();
				else return "NoClan";
				
			} else if (params.toLowerCase().equalsIgnoreCase("timetostart"))
				
				return String.valueOf(Gladiator.getGladRunning().getTimeToStart());
			
			else if (params.toLowerCase().equalsIgnoreCase("numclans"))
				
				return String.valueOf(Gladiator.getGladRunning().getClans().size());
			
			else if (params.toLowerCase().equalsIgnoreCase("clans")) {
				String clans = "";
				for (Clan clan : Gladiator.getGladRunning().getClans()) clans += clan.getTagClan();
				return clans;
				
			} else if (params.toLowerCase().equalsIgnoreCase("numplayers"))
				return String.valueOf(Gladiator.getGladRunning().getPlayers().size());
			
			else if (params.toLowerCase().equalsIgnoreCase("enemies"))
				return String.valueOf(Main.getPlayerManager().getPlayer(p).getEnemies());
			else if (params.toLowerCase().equalsIgnoreCase("kills")) return String.valueOf(Main.getPlayerManager().getPlayer(p).getKills());
			else if (params.toLowerCase().equalsIgnoreCase("lastclanplayers"))
				return String.valueOf(Main.getPlayerManager().getPlayer(p).getAllies());
		} else {
			
			if (params.toLowerCase().equalsIgnoreCase("tag")) return Main.tag;
			else if (params.toLowerCase().equalsIgnoreCase("clan")) {
				if (player.getClan() != null) return player.getClan().getTagClan();
				else return "NoClan";
				
			} else if (params.toLowerCase().equalsIgnoreCase("timetostart")) return "NoGladRunning";
			else if (params.toLowerCase().equalsIgnoreCase("numclans")) return "NoGladRunning";
			else if (params.toLowerCase().equalsIgnoreCase("clans")) return "NoGladRunning";
			else if (params.toLowerCase().equalsIgnoreCase("numplayers")) return "NoGladRunning";
			else if (params.toLowerCase().equalsIgnoreCase("enemies")) return "NoGladRunning";
			else if (params.toLowerCase().equalsIgnoreCase("kills")) return "NoGladRunning";
			else if (params.toLowerCase().equalsIgnoreCase("lastclanplayers")) return "NoGladRunning";
		}
		return string;
	}
	
	public static String replace(Player p, String string) { return PlaceholderAPI.setPlaceholders(p, string); }
	
}
