package me.gugafenix.legionmc.glad.commands.arguments;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Confirmation;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.player.GladPlayer.SelectionStatus;
import me.gugafenix.legionmc.glad.utils.API;

public class Leave {
	
	public void execute(Player p, String cmd, String[] args) {
		Map<String, Long> map = Confirmation.getMap();
		Gladiator glad = Gladiator.getGladRunning();
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		
		if (!Gladiator.HasGladRunning()) {
			p.sendMessage(Main.tag + "§cO servidor não possui nenhum gladiador em andamento.");
			API.getApi().playSound(p, Sound.VILLAGER_NO);
			return;
			
		}
		
		if (!glad.getPlayers().contains(gp)) {
			p.sendMessage(Main.tag + "§cVocê não está participando do gladiador");
			API.getApi().playSound(p, Sound.VILLAGER_NO);
			return;
		}
		
		if (!map.containsKey(p.getName())) {
			map.put(p.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));
			p.sendMessage(Main.tag + "§eEnvie novamente o comando para confirmar sua saída do gladiador");
			return;
		}
		
		if (System.currentTimeMillis() <= map.get(p.getName())) {
			map.remove(p.getName());
			
			// Warning all
			glad.getPlayers().forEach(
					gps -> gps.getPlayer().sendMessage(Main.tag + "§cO player §7" + p.getName() + ", §csaiu do evento e foi eliminado."));
			p.sendMessage(Main.tag + "§bVocê foi removido e eliminado do gladiador.");
			p.teleport(Bukkit.getServer().getWorld(Bukkit.getWorlds().get(0).getName()).getSpawnLocation());
			
			// Deletando o player do glad
			gp.setInGladiator(false);
			gp.setSelectedPlayers(null);
			gp.setSelectionStatus(SelectionStatus.NO_STATUS);
			glad.getPlayers().remove(gp);
			Main.getPlayerManager().getPlayers().remove(gp);
			for (GladPlayer gps : glad.getPlayers()) {
				if (gps.getClan() == gp.getClan()) continue;
				gps.setAllies(gps.getAllies() - 1);
			}
			// Play Sound
			API.getApi().playSound(p, Sound.LEVEL_UP);
			
			return;
		}
		
		map.replace(p.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));
		p.sendMessage(Main.tag + "§eEnvie novamente o comando para confirmar sua saída do gladiador");
	}
	
}
