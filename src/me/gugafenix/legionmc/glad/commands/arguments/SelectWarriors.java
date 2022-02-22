package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.HClan.Objects.Jogador;
import me.gugafenix.legionmc.glad.invs.SelectMembersMenu;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.player.GladPlayer.SelectionStatus;
import me.gugafenix.legionmc.glad.utils.API;

public class SelectWarriors {
	public void execute(CommandSender sender, String cmd, String[] args) {
		Player p = (Player) sender;
		Jogador j = Jogador.check(p);
		
		// Check se o player ta no glad
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		if (gp == null) {
			p.sendMessage(Main.tag + "§cVocê precisa entrar no gladiador primeiro, /glad entrar");
			return;
		}
		
		// Check se o player tem clã
		if (j.getClan() == null) {
			p.sendMessage("§cVocê precisa de um clã para isso");
			return;
		}
		
		// Check se é um lider ou colider
		if (j.getClan().getLider() != j && !j.getClan().getColideres().contains(j)) {
			p.sendMessage(Main.tag + "§cApenas o lider ou colider do clã pode escolher os guerreiros");
			API.getApi().playSound(p, Sound.VILLAGER_NO);
			return;
		}
		
		// Abre o menu
		SelectMembersMenu menu = new SelectMembersMenu();
		menu.createInventory();
		menu.openInventory(p);
		gp.setSelectionStatus(SelectionStatus.SELECTING);
	}
	
}
