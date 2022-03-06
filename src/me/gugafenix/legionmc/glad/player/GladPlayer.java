package me.gugafenix.legionmc.glad.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.HClan.MenuPodio.menuTopClansGladiadores;
import me.HClan.Objects.Clan;
import me.HClan.Objects.Jogador;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.objects.Gladiator.GladiatorStatus;
import me.gugafenix.legionmc.glad.tasks.Tasks.TaskId;
import me.gugafenix.legionmc.glad.utils.API;
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
		if (!glad.getPlayers().contains(this)) return;
		if (this.isWatching) return;
		
		// Warn to glad players
		glad.getPlayers().forEach(tp -> {
			tp.getPlayer()
					.sendMessage("§cO jogador §7" + this.getPlayer().getName() + " " + this.getClan().getTagClan() + " §cfoi eliminado");
			tp.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			if (this.getClan() == tp.getClan()) return;
			tp.setEnemies(tp.getEnemies() - 1);
			this.setInGladiator(false);
		});
		
		// Check se o clan foi eliminado
		for (Clan clans : glad.getClans()) {
			int i = 0;
			for (Player cp : clans.getOnlinePlayers()) {
				if (Main.getPlayerManager().getPlayer(cp) == null && !Main.getPlayerManager().getPlayer(cp).isInGladiator()) continue;
				if (Main.getPlayerManager().getPlayer(cp).isWatching()) continue;
				i++;
			}
			
			// Check memebros do clã no glad
			if (i <= 0) {
				glad.getClans().remove(clans);
				Bukkit.getOnlinePlayers().forEach(all -> {
					all.sendMessage(Main.tag + "§bBoletim do gladiador");
					all.sendMessage("§3Mais um clã perdeu a batalha");
					all.sendMessage("§aO clã " + clans.getNome() + " foi eliminado do gladiador");
					all.sendMessage("§eÚiltimo sobrevivente do clã: §f" + this.getPlayer().getName());
				});
				
				continue;
			}
		}
		
		for (GladPlayer gps : glad.getPlayers()) {
			if (gps.getClan() == this.getClan()) continue;
			gps.setAllies(gps.getAllies() - 1);
		}
		
		// Iniciando death match
		if (glad.getClans().size() <= glad.getClanAmountToDeathMatch() && glad.getStatus() != GladiatorStatus.DEATH_MATCH) {
			
			for (GladPlayer gps : glad.getPlayers()) {
				Player tp = gps.getPlayer();
				tp.teleport(glad.getDeathMatchWorld().getSpawnLocation());
				tp.sendMessage("§6§lO death match iniciará em 10 segundos");
			}
			
			glad.runTask(TaskId.DEATHMATCH);
		}
		
		if (glad.getClans().size() <= 1) {
			Clan winner = glad.getClans().get(0);
			Clan lastClan = this.getClan();
			if (glad.getWinner() != null) { glad.setWinner(glad.getClans().get(0)); }
			
			for (Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(Main.tag + "§bBoletim do gladiador");
				all.sendMessage("§3§lO GLADIADOR FOI FINALIZADO!");
				all.sendMessage("§b§lO ganhador foi o clan §f" + winner.getNome());
				all.sendMessage("§4§lÚltimo sobrevivente: §c" + lastClan);
				all.sendMessage("§lPóximo gladiador em §75 dias");
				API.getApi().playParticle(false, all.getLocation(), 0, 0, 0, EnumParticle.FIREWORKS_SPARK);
				API.getApi().playSound(all, Sound.FIREWORK_LAUNCH);
				API.getApi().playSound(all, Sound.ANVIL_BREAK);
				
				for (Player cp : clan.getOnlinePlayers()) {
					cp.teleport(unserialize(glad.getPreset().getConfig().getString("Server.Lobby.localização")));
					API.getApi().sendTitle("§b§lPARAbÉNS!", "§aVocê foi um dos ganhadores do gladiador", cp);
					
					for (ItemStack item : cp.getInventory()) {
						if (!isArmor(item)) continue;
						if (item.getDurability() > item.getType().getMaxDurability() / 2) continue;
						
						item.setDurability((short) (item.getType().getMaxDurability() / 2));
						
					}
					
					cp.sendMessage(Main.tag
							+ "§aComo compensação por sua vitória, suas armaduras e espadas no inventário foram reparadas até a metade!");
					API.getApi().playSound(cp, Sound.ANVIL_USE);
				}
				
				clan.setGladiadoresVencidos(clan.getGladiadoresVencidos() + 1);
				menuTopClansGladiadores.get().updateTop();
			}
		}
		
		glad.getPlayers().remove(this);
		Main.getPlayerManager().getPlayers().remove(this);
		
		glad.cancel();
	}
	
	public Location unserialize(String string) {
		String args[] = string.split(":");
		Double X = Double.parseDouble(args[0]);
		Double Y = Double.parseDouble(args[1]);
		Double Z = Double.parseDouble(args[2]);
		
		return new Location(Bukkit.getWorld(Gladiator.getGladRunning().getPreset().getConfig().getString("Server.Lobby.mundo")), X, Y, Z);
	}
	
	public boolean isArmor(ItemStack item) {
		boolean isArmor = false;
		switch (item.getType()) {
		case DIAMOND_CHESTPLATE:
			isArmor = true;
			break;
		case DIAMOND_LEGGINGS:
			isArmor = true;
			break;
		case DIAMOND_HELMET:
			isArmor = true;
			break;
		case DIAMOND_BOOTS:
			isArmor = true;
			break;
		case IRON_CHESTPLATE:
			isArmor = true;
			break;
		case IRON_LEGGINGS:
			isArmor = true;
			break;
		case IRON_HELMET:
			isArmor = true;
			break;
		case IRON_BOOTS:
			isArmor = true;
			break;
		case GOLD_CHESTPLATE:
			isArmor = true;
			break;
		case GOLD_LEGGINGS:
			isArmor = true;
			break;
		case GOLD_HELMET:
			isArmor = true;
			break;
		case GOLD_BOOTS:
			isArmor = true;
			break;
		case LEATHER_CHESTPLATE:
			isArmor = true;
			break;
		case LEATHER_LEGGINGS:
			isArmor = true;
			break;
		case LEATHER_HELMET:
			isArmor = true;
			break;
		case LEATHER_BOOTS:
			isArmor = true;
			break;
		case WOOD_SWORD:
			isArmor = true;
			break;
		case IRON_SWORD:
			isArmor = true;
			break;
		case DIAMOND_SWORD:
			isArmor = true;
			break;
		case GOLD_SWORD:
			isArmor = true;
			break;
		default:
			isArmor = false;
			break;
		}
		
		return isArmor;
		
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
	
}
