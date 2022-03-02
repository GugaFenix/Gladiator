package me.gugafenix.legionmc.glad.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.HClan.Objects.Clan;
import me.HClan.Objects.Jogador;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.objects.Gladiator.GladiatorStatus;
import me.gugafenix.legionmc.glad.player.GladPlayer;

public class Join {
	
	public boolean execute(CommandSender sender, String cmd, String[] args) {
		
		// Vars
		Player p = (Player) sender;
		Clan clan = Jogador.check(p).getClan();
		String tag = Main.tag;
		Jogador jogador = Jogador.check(p);
		Gladiator glad = Gladiator.getGladRunning();
		GladPlayer gp = Main.getPlayerManager().getPlayer(p);
		
		if (glad == null) {
			p.sendMessage(tag + "§cNão há nenhum evento gladiador ocorrendo.");
			return true;
		}
		
		// Check se j� ta no gladiador
		if (Main.getPlayerManager().getPlayer(p) != null && glad.getPlayers().contains(gp)) {
			p.sendMessage(tag + "§cVocê já está participando do gladiador");
			return false;
		}
		
		// Check se tem cl�
		if (jogador.getClan() == null) {
			p.sendMessage(tag + "§cVocê  precisa de um clã para participar do gladiador.");
			return false;
		}
		
		// Check de requisitos
		boolean hasMoney = clan.getMoney().getSaldo() >= glad.getMinClanMoney();
		
		// Check se � lider ou colider
		if (clan.getLider() != jogador && !clan.getColideres().contains(jogador)) {
			p.sendMessage(tag + "§cApenas lideres ou colideres do clã podem entrar no gladiador por enquanto.");
			return false;
		}
		
		// Check se tem os requisitos
		if (!hasMoney) {
			p.sendMessage(tag);
			p.sendMessage("§3§lRequisito para entrar no gladiador");
			p.sendMessage("§bMoney do clã: §f" + glad.getMinClanMoney());
			p.sendMessage("");
			p.sendMessage("§7Para mandar dinheiro para o clã use /clan, vá em 'banco do clã' e pressione 'depositar'");
			return false;
		}
		
		if (gp == null) gp = new GladPlayer(p);
		
		if (glad.getStatus() != GladiatorStatus.WARNING) {
			p.sendMessage(tag + "§cVocê não pode entrar enquanto o gladiador está ocorrendo");
			return false;
		}
		
		// Manda o player pro gladiador
		p.sendMessage(Main.tag
				+ "§bEm alguns segundos, o processo de seleção de guereiros iniciará, prepare-se e prepare seus membros para isto.");
		gp.setInGladiator(true);
		if (!glad.getClans().contains(gp.getClan())) glad.getClans().add(gp.getClan());
		glad.getPlayers().add(gp);
		glad.updateInfoFromAll();
		
//		new GladBoard(glad.getScoreboard().get(0), glad.getScoreboard().subList(1, glad.getScoreboard().size() - 1)).create(p);
		
		return true;
		
	}
	
}
