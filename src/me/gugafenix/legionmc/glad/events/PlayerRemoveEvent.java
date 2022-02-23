package me.gugafenix.legionmc.glad.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.HClan.Objects.Clan;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.tasks.Tasks;
import me.gugafenix.legionmc.glad.tasks.Tasks.TaskId;

public class PlayerRemoveEvent implements Listener {
	
	public PlayerRemoveEvent() { Main.registerEvent(new PlayerRemoveEvent()); }
	
	@EventHandler
	void onPlayerDeathEvent(PlayerDeathEvent e) {
		Player d = e.getEntity();
		Player p = e.getEntity().getKiller();
		GladPlayer gpm = Main.getPlayerManager().getPlayer(p);
		GladPlayer gpk = Main.getPlayerManager().getPlayer(d);
		Gladiator glad = Gladiator.getGladRunning();
		
		if (glad == null) return;
		if (gpm == null || !gpm.isInGladiator()) return;
		
		e.setDeathMessage(null);
		gpk.setKills(gpk.getKills() + 1);
		
		for (Clan clans : glad.getAllClans()) {
			int i = 0;
			for (Player cp : clans.getOnlinePlayers()) {
				if (Main.getPlayerManager().getPlayer(cp) == null && !Main.getPlayerManager().getPlayer(cp).isInGladiator()) continue;
				i++;
			}
			
			if (i <= 0) {
				glad.getAllClans().remove(clans);
				Bukkit.getOnlinePlayers().forEach(all -> {
					all.sendMessage(Main.tag + "§bBoletim do gladiador");
					all.sendMessage("§3Mais um clã perdeu a batalha");
					all.sendMessage("§aO clã " + clans.getNome() + " foi eliminado do gladiador");
					all.sendMessage("§eÚiltimo sobrevivente do clã: §f" + p.getName());
				});
				continue;
			}
		}
		
		glad.getPlayers().forEach(tp -> {
			tp.getPlayer()
					.sendMessage("§cO jogador §7" + gpm.getPlayer().getName() + " " + gpm.getClan().getTagClan() + " §cfoi eliminado");
			tp.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			if (gpm.getClan() == tp.getClan()) return;
			tp.setEnemies(tp.getEnemies() - 1);
			gpm.setInGladiator(false);
			glad.getPlayers().remove(gpm);
			Main.getPlayerManager().getPlayers().remove(gpm);
		});
		
		if (glad.getAllClans().size() <= glad.getClanAmountToDeathMatch()) {
			
			for (GladPlayer gps : glad.getPlayers()) {
				Player tp = gps.getPlayer();
				tp.teleport(glad.getDeathMatchWorld().getSpawnLocation());
				tp.sendMessage("§6§lO death match iniciará em 10 segundos");
			}
			
			new Tasks(TaskId.DEATHMATCH, glad).start();
			
		}
		
	}
}
