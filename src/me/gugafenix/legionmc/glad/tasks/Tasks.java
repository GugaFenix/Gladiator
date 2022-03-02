package me.gugafenix.legionmc.glad.tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.HClan.Objects.Jogador;
import me.clip.placeholderapi.PlaceholderAPI;
import me.gugafenix.legionmc.glad.invs.SelectMembersMenu;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.objects.Gladiator.GladiatorStatus;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.player.GladPlayer.SelectionStatus;
import me.gugafenix.legionmc.glad.utils.API;

public class Tasks {
	private Gladiator glad;
	private BukkitTask sendWarns, checkSelection, startBattle, deathMatch;
	private TaskId id;
	
	public enum TaskId {
		SEND_WARNS, CHECK_SELECTION, START_BATTLE, DEATHMATCH;
	}
	
	public Tasks(TaskId id, Gladiator glad) {
		this.id = id;
		this.glad = glad;
	}
	
	public BukkitTask start() {
		BukkitTask task = null;
		switch (id) {
		case SEND_WARNS:
			task = runWarnsTask();
			break;
		
		case CHECK_SELECTION:
			task = runSelectionCheckTask();
			break;
		
		case START_BATTLE:
			task = runStartBattleTask();
			break;
		case DEATHMATCH:
			task = runDeathMatchTask();
			break;
		default:
			break;
		}
		
		return task;
	}
	
	private BukkitTask runWarnsTask() {
		return sendWarns = new BukkitRunnable() {
			int i = glad.getTimeBelowWarns();
			
			@Override
			public void run() {
				
				// Check se o tempo de aviso acabou
				if (glad.getTimeToStart() > 0) {
					glad.setTimeToStart(glad.getTimeToStart() - (1));
				} else {
					glad.getPlayers().forEach(gps -> gps.setSelectionStatus(SelectionStatus.SELECTING));
					
					// Warn
					if (glad.getClans().size() <= 1) {
						Bukkit.getOnlinePlayers().forEach(all -> {
							all.sendMessage(Main.tag + "§9O gladiador foi cancelado por falta de clãs");
							glad.cancel();
							this.cancel();
						});
					}
					
					glad.setStatus(GladiatorStatus.SELECTING);
					runSelectionCheckTask();
					this.cancel();
					return;
				}
				
				/*
				 * Send Warns
				 */
				
				// Todos os players
				for (Player p : Bukkit.getOnlinePlayers()) {
					
					// Check se o player j§ est§ no gladiador
					if (Main.getPlayerManager().getPlayer(p) != null) continue;
					
					// Clear chat
					for (int j = 0; j < 200; j++) p.sendMessage("");
					
					if (i == 1) {
						sendWarn(p);
						i = glad.getTimeBelowWarns();
					} else {
						i--;
					}
				}
				
			}
		}.runTaskTimerAsynchronously(Main.getMain(), 0, 20);
	}
	
	private BukkitTask runDeathMatchTask() {
		glad.getWorld().setPVP(false);
		glad.getDeathMatchWorld().setPVP(false);
		return setDeathMatch(new BukkitRunnable() {
			int i = 10;
			
			@Override
			public void run() {
				if (i > 0) {
					
					for (GladPlayer gp : glad.getPlayers())
						API.getApi().sendTitle("§bDEATH MATCH", "§biniciando em §3" + i, gp.getPlayer());
					
					i--;
				} else {
					glad.getWorld().setPVP(true);
					for (GladPlayer gp : glad.getPlayers())
						API.getApi().sendTitle("§bO death match iniciou ", "§3§lLUTE!" + i, gp.getPlayer());
					this.cancel();
					
				}
			}
		}.runTaskTimerAsynchronously(Main.getMain(), 0, 20));
	}
	
	private void sendWarn(Player p) {
		for (String msg : glad.getWarns()) { p.sendMessage(replace(p, msg)); }
		API.getApi().sendTitle(replace(p, glad.getTitle()), replace(p, glad.getSubtitle()), p);
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 10f, 10f);
		p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 10.1f, 10f);
		p.playSound(p.getLocation(), Sound.SHEEP_SHEAR, 101f, 10f);
	}
	
	public BukkitTask runSelectionCheckTask() {
		glad.setStatus(GladiatorStatus.SELECTING);
		
		glad.getPlayers().forEach(p -> {
			p.setSelectionStatus(SelectionStatus.SELECTING);
			p.getPlayer().sendMessage(Main.tag
					+ "§bO evento gladiador será iniciado assim que todos os guerreiros estiverem sido selecionados pelos seus líderes, prepare-se para a batalha!");
			API.getApi().playSound(p.getPlayer(), Sound.ANVIL_USE);
		});
		
		return checkSelection = new BukkitRunnable() {
			int i = 0;
			
			@Override
			public void run() {
				
				glad.getPlayers().forEach(p -> {
					
					// Check se n§o § o lider e se est§ pronto
					if (p.getClan().getLider() != Jogador.check(p.getPlayer())) return;
					
					// Todos os players que ainda est§o selecionando
					if (p.getSelectionStatus() == SelectionStatus.SELECTING) {
						
						SelectMembersMenu menu = new SelectMembersMenu();
						menu.createInventory();
						
						if (!p.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("§0Selecionar Guerreiros"))
							menu.openInventory(p.getPlayer());
						
						// Se j§ se passaram 5 segundos
						if (i < 5) i++;
						else {
							
							// Clear chat
							for (int j = 0; j < 200; j++) p.getPlayer().sendMessage("");
							
							// Send select message
							p.getPlayer().sendMessage("§e§l§m----§6§l§m--------" + Main.tag + "§6§l§m --------§e§l§m---");
							p.getPlayer().sendMessage("§3Voc§ se propos a desafiar todos à uma batalha mortal");
							p.getPlayer().sendMessage("§bAgora, escolha aqueles que lutarão ao seu lado por " + p.getClan().getTagClan());
							p.getPlayer().sendMessage("§6§l§m---------------§8§l[§6§lSelecine seus Guerreiros§8§l]§6§l§m-----------------");
							
							// Reset timer
							i = 0;
						}
					}
				});
				
			}
			
		}.runTaskTimerAsynchronously(Main.getMain(), 0, 20);
		
	}
	
	public BukkitTask runStartBattleTask() {
		glad.setStatus(GladiatorStatus.IN_BATTLE);
		return startBattle = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if (glad.getTimeToStart() > 1) {
					for (GladPlayer p : glad.getPlayers()) {
						API.getApi().sendTitle("§b§lIniciando batalha em", "§3§l" + glad.getTimeToStart() + "§b§l segundos", p.getPlayer());
						API.getApi().playSound(p.getPlayer(), Sound.CLICK);
					}
					glad.setTimeToStart(glad.getTimeToStart() - (1));
				} else {
					
					for (GladPlayer p : glad.getPlayers()) {
						// Clear chat
						for (int j = 0; j < 100; j++) p.getPlayer().sendMessage("");
						API.getApi().sendTitle("§b§lBatalha iniciada", "§3§lLUTE!", p.getPlayer());
						API.getApi().playSound(p.getPlayer(), Sound.EXPLODE);
						
						int rand = new Random().nextInt(glad.getSpawnPoints().size());
						Location loc = unserialize(glad.getSpawnPoints().get(rand));
						p.getPlayer().teleport(loc);
						
						Bukkit.getScheduler().runTask(Main.getMain(), () -> p.getPlayer().setGameMode(GameMode.SURVIVAL));
					}
					
					glad.getWorld().setPVP(true);
					glad.setStartMilis(System.currentTimeMillis());
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously(Main.getMain(), 0, 20);
	}
	
	public Location unserialize(String string) {
		String args[] = string.split(":");
		Double X = Double.parseDouble(args[0]);
		Double Y = Double.parseDouble(args[1]);
		Double Z = Double.parseDouble(args[2]);
		
		return new Location(this.glad.getWorld(), X, Y, Z);
	}
	
	public Gladiator getGlad() { return glad; }
	
	public void setGlad(Gladiator glad) { this.glad = glad; }
	
	public BukkitTask getSendWarns() { return sendWarns; }
	
	public void setSendWarns(BukkitTask sendWarns) { this.sendWarns = sendWarns; }
	
	public BukkitTask getCheckSelection() { return checkSelection; }
	
	public void setCheckSelection(BukkitTask checkSelection) { this.checkSelection = checkSelection; }
	
	public BukkitTask getStartBattle() { return startBattle; }
	
	public void setStartBattle(BukkitTask startBattle) { this.startBattle = startBattle; }
	
	public TaskId getId() { return id; }
	
	public void setId(TaskId id) { this.id = id; }
	
	private String replace(Player p, String string) { return PlaceholderAPI.setPlaceholders(p, string).replace("&", "§"); }
	
	public boolean hasPlayerSelecting() {
		for (GladPlayer p : glad.getPlayers()) {
			if (p.getSelectionStatus() == SelectionStatus.SELECTING) { return true; }
			return false;
		}
		return false;
	}
	
	public BukkitTask getDeathMatch() { return deathMatch; }
	
	public BukkitTask setDeathMatch(BukkitTask deathMatch) {
		this.deathMatch = deathMatch;
		return deathMatch;
	}
	
}
