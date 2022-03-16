/*
 * 
 */
package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.player.GladPlayer.SelectionStatus;
import me.gugafenix.legionmc.glad.tasks.Tasks;
import me.gugafenix.legionmc.glad.tasks.Tasks.TaskId;

public class ForceDeathMatch {
	
	public void execute(Player p, String cmd, String[] args) {
		
		Gladiator glad = Gladiator.getGladRunning();
		
		if (!p.hasPermission("*")) {
			p.sendMessage("§cVocê não tem permissão para isto");
			return;
		}
		
		if (glad == null) {
			p.sendMessage(Main.tag + "§cO servidor não possui nenhum gladiador ocorrendo");
			return;
		}
		BukkitTask task = glad.getTaskRunning();
		if (task == Tasks.getDeathMatch()|| glad.getTaskRunning() == null) {
			p.sendMessage(Main.tag + "§cO gladiador está iniciando ou já está em batalha");
			return;
		}
		
		for (GladPlayer gp : glad.getPlayers()) {
			gp.setSelectionStatus(SelectionStatus.READY);
			gp.getPlayer().sendMessage(Main.tag + "§3O início do death match foi sancionado mais cedo por " + p.getName());
		}
		
		Gladiator.getGladRunning().runTask(TaskId.DEATHMATCH);
		
	}
	
}
