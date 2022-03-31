/*
 * 
 */
package me.gugafenix.legionmc.glad.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.HClan.MenuPodio.menuTopClansGladiadores;
import me.HClan.Objects.Clan;
import me.HClan.Objects.Jogador;
import me.clip.placeholderapi.PlaceholderAPI;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.objects.Gladiator.GladiatorStatus;
import me.gugafenix.legionmc.glad.tasks.Tasks.TaskId;
import me.gugafenix.legionmc.glad.utils.API;
import me.gugafenix.legionmc.glad.utils.Messages;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class GladPlayer {
	
	private int enemies, allies;
	private int kills;
	private Player player;
	private Clan clan;
	private boolean isInGladiator;
	private boolean isWatching;
	private List<Player> selectedPlayers;
	private SelectionStatus selectionStatus;
	
	public static enum SelectionStatus {
		READY, SELECTING, NO_STATUS
	};
	
	public GladPlayer(Player player) {
		selectedPlayers = new ArrayList<>();
		this.selectionStatus = SelectionStatus.NO_STATUS;
		this.player = player;
		this.clan = Jogador.check(player).getClan();
		this.kills = 0;
		this.enemies = 0;
		this.allies = 0;
		this.isWatching = false;
		this.isInGladiator = false;
		Main.getPlayerManager().getPlayers().add(this);
	}
	
	public List<Player> getSelectedPlayers() { return selectedPlayers; }
	
	public void setSelectedPlayers(List<Player> selectedPlayers) { this.selectedPlayers = selectedPlayers; }
	
	public Player getPlayer() { return player; }
	
	public int getEnemies() { return enemies; }
	
	public void setEnemies(int enemies) { this.enemies = enemies; }
	
	public int getAllies() { return allies; }
	
	public void setAllies(int allies) { this.allies = allies; }
	
	public boolean isInGladiator() { return isInGladiator; }
	
	public void setInGladiator(boolean isInGladiator) { this.isInGladiator = isInGladiator; }
	
	public boolean isWatching() { return isWatching; }
	
	public void setWatching(boolean isWatching) { this.isWatching = isWatching; }
	
	public void setPlayer(Player player) { this.player = player; }
	
	public int getKills() { return kills; }
	
	public void setKills(int kills) { this.kills = kills; }
	
	public Clan getClan() { return clan; }
	
	public void setClan(Clan clan) { this.clan = clan; }
	
	public SelectionStatus getSelectionStatus() { return selectionStatus; }
	
	public void setSelectionStatus(SelectionStatus selectionStatus) { this.selectionStatus = selectionStatus; }
	
	public void deleteFromGladiator(Gladiator glad) {
		if (glad == null) return;
		if (!this.isInGladiator) return;
		if (this.isWatching) return;
		
		this.updateInfos();
		
		if (this.getAllies() <= 0) for (Player p : Bukkit.getOnlinePlayers()) for (String msg : Messages.klanKilled)
			p.sendMessage(PlaceholderAPI.setPlaceholders(p, msg).replace("%clan%", this.getClan().getNome())
					.replace("%kills%", getClanKills() + "").replace("%last_member%%", this.getPlayer().getName()));
		
		this.setInGladiator(false);
		glad.updateInfoFromAll();
		
		// Vitória
		if (glad.getClans().size() <= 1) {
			if (glad.getWinner() == null) {
				glad.setWinner(glad.getClans().get(0));
				for (Player p : Bukkit.getOnlinePlayers()) {
					for (String msg : Messages.victory) {
						p.sendMessage(PlaceholderAPI.setPlaceholders(p, msg).replace("%winner%", glad.getWinner().getTagClan())
								.replace("%last_clan%", clan.getTagClan()));
						p.sendMessage(PlaceholderAPI.setPlaceholders(p, Messages.print).replace("%time%", glad.getTimeToPrint() + ""));
						new BukkitRunnable() {
							@Override
							public void run() {
								glad.cancel();
								this.cancel();
							}
						}.runTaskTimer(Main.getMain(), glad.getTimeToPrint(), 1);
					}
				}
			}
		}
		
		if (glad.getClans().size() <= glad.getClanAmountToDeathMatch()) glad.runTask(TaskId.DEATHMATCH);
	}
	
	public void delete() { Main.getPlayerManager().getPlayers().remove(this); }
	
	public static Location unserialize(String string) {
		String args[] = string.split(":");
		Double X = Double.parseDouble(args[1]);
		Double Y = Double.parseDouble(args[2]);
		Double Z = Double.parseDouble(args[3]);
		
		return new Location(Bukkit.getWorld(args[0]), X, Y, Z);
	}
	
	public boolean isArmor(ItemStack item) {
		String type = item.getType().toString().toUpperCase();
		if (type.contains("HELMET") || type.contains("CHESTPLATE") || type.contains("LEGGINGS") || type.contains("BOOTS")
				|| type.contains("SWORD"))
			return true;
		return false;
	}
	
	public void updateInfos() {
		Gladiator glad = Gladiator.getGladRunning();
		for (GladPlayer gp : glad.getPlayers()) {
			if (gp != this) {
				if (gp.getClan() != this.getClan()) this.enemies++;
				else this.allies++;
			}
		}
	}
	
	private int getClanKills() {
		int clanKills = 0;
		for (Player p : clan.getOnlinePlayers()) {
			GladPlayer gp = Main.getPlayerManager().getPlayer(p);
			if (gp == null) continue;
			clanKills = +gp.getKills();
		}
		return clanKills;
	}
}
