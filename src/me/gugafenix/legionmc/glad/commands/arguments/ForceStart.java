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
import me.gugafenix.legionmc.glad.utils.Messages;
import me.gugafenix.legionmc.glad.utils.PlaceHolder;

public class ForceStart {
	
	public void execute(Player p, String cmd, String[] args) {
		Gladiator glad = Gladiator.getGladRunning();
		
		if (!p.hasPermission("*")) {
			p.sendMessage(PlaceHolder.replace(p, Messages.permission));
			return;
		}
		
		if (glad == null) {
			p.sendMessage(Main.tag + "§cO servidor não possui nenhum gladiador ocorrendo");
			return;
		}
		BukkitTask task = glad.getTaskRunning();
		if (task == Tasks.getStartBattle()) {
			p.sendMessage(Main.tag + "§cO gladiador já está em batalha.");
			return;
		}
		
		for (GladPlayer gp : glad.getPlayers()) {
			gp.setSelectionStatus(SelectionStatus.READY);
			gp.getPlayer().sendMessage(Main.tag + "§3O início do gladiador foi sancionado mais cedo por " + p.getName());
		}
		
		Gladiator.getGladRunning().runTask(TaskId.START_BATTLE);
	}
	
}
