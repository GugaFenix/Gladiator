/*
 *
 */
package me.gugafenix.legionmc.glad.tasks;

import java.util.ArrayList;
import java.util.List;
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
import me.gugafenix.legionmc.glad.border.BorderManager;
import me.gugafenix.legionmc.glad.invs.SelectMembersMenu;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.objects.Gladiator.GladiatorStatus;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.player.GladPlayer.SelectionStatus;
import me.gugafenix.legionmc.glad.scoreboard.ScoreManager;
import me.gugafenix.legionmc.glad.scoreboard.Scoreboard;
import me.gugafenix.legionmc.glad.utils.API;
import me.gugafenix.legionmc.glad.utils.Messages;
import me.gugafenix.legionmc.glad.utils.PlaceHolder;

public class Tasks {
	private Gladiator glad;
	private static BukkitTask sendWarns, checkSelection, startBattle, deathMatch, borderDecrease;
	private TaskId id;
	
	public enum TaskId {
		SEND_WARNS, CHECK_SELECTION, START_BATTLE, DEATHMATCH, BORDER, SCOREBOARD_UPDATE;
	}
	
	public Tasks(TaskId id, Gladiator glad) {
		this.id = id;
		this.glad = glad;
	}
	
	public BukkitTask start() {
		BukkitTask task = null;
		switch (id) {
		case SEND_WARNS:
			return task = runWarnsTask();
		case CHECK_SELECTION:
			return task = runSelectionCheckTask();
		case START_BATTLE:
			return task = runStartBattleTask();
		case DEATHMATCH:
			return task = runDeathMatchTask();
		case BORDER:
			return task = runBorderUpdateTask();
		default:
		}
		
		return task;
	}
	
	public BukkitTask start(TaskId id) {
		BukkitTask task = null;
		switch (id) {
		case SEND_WARNS:
			return task = runWarnsTask();
		case CHECK_SELECTION:
			return task = runSelectionCheckTask();
		case START_BATTLE:
			return task = runStartBattleTask();
		case DEATHMATCH:
			return task = runDeathMatchTask();
		case BORDER:
			return task = runBorderUpdateTask();
		default:
		}
		return task;
	}
	
	public static BukkitTask getTaskById(TaskId id) {
		BukkitTask task = null;
		switch (id) {
		case SEND_WARNS:
			task = getWarnsTask();
			break;
		case CHECK_SELECTION:
			task = getCheckSelection();
			break;
		
		case START_BATTLE:
			task = getStartBattle();
			break;
		case DEATHMATCH:
			task = getDeathMatch();
			break;
		case BORDER:
			task = getBorderDecrease();
			break;
		default:
			break;
		}
		return task;
	}
	
	private BukkitTask runWarnsTask() {
		return sendWarns = new BukkitRunnable() {
			
			@Override
			public void run() {
				glad.setRunningTask(sendWarns);
				// Check se o tempo de aviso acabou
				if (glad.getTimeToStart() > 0) {
					glad.setTimeToStart(glad.getTimeToStart() - (1));
				} else {
					for (GladPlayer gps : glad.getPlayers()) gps.setSelectionStatus(SelectionStatus.SELECTING);
					
					// Warn
					if (glad.getClans().size() < 2) {
						for (Player all : Bukkit.getOnlinePlayers()) {
							all.sendMessage(Main.tag + "§9O gladiador foi cancelado por falta de clãs");
							glad.cancel();
							this.cancel();
						}
					}
					
					glad.setStatus(GladiatorStatus.SELECTING);
					glad.setRunningTask(runSelectionCheckTask());
					this.cancel();
					return;
				}
				
				/*
				 * Send Warns
				 */
				
				// Todos os players
				for (Player p : Bukkit.getOnlinePlayers()) {
					
					// Check se o player j§ est§ no gladiador
					GladPlayer gp = Main.getPlayerManager().getPlayer(p);
					
					if (gp != null && gp.isInGladiator())
						for (String msg : Messages.beforestart) p.sendMessage(PlaceHolder.replace(p, msg));
					else {
						
						p.sendMessage("");
						sendWarn(p);
						p.sendMessage("");
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.getMain(), 0, glad.getTimeBelowWarns() * 20);
	}
	
	private BukkitTask runDeathMatchTask() {
		glad.getWorld().setPVP(false);
		glad.getDeathMatchWorld().setPVP(false);
		
		return deathMatch = new BukkitRunnable() {
			int i = 10;
			
			@Override
			public void run() {
				if (i > 0) {
					for (GladPlayer gp : glad.getPlayers()) {
						API.getApi().sendTitle("§b§lDEATH MATCH", "§biniciando em §3" + i, gp.getPlayer());
						
						Scoreboard score = ScoreManager.getManager().getScoreBoard(gp.getPlayer());
						
						if (score != null) {
							score.create();
							score.update();
							continue;
						}
						
						score = new Scoreboard(gp.getPlayer(), glad.getScoreboard().get(0));
						score.create();
						score.update();
					}
					i--;
				} else {
					glad.getDeathMatchWorld().setPVP(true);
					for (GladPlayer gp : glad.getPlayers()) {
						API.getApi().sendTitle("§bO death match iniciou ", "§3§lLUTE!", gp.getPlayer());
						Location loc = unserialize(glad.getPreset().getConfig().getString("DeathMatch.Spawn"));
						loc.setWorld(Bukkit.getWorld(glad.getPreset().getConfig().getString("DeathMatch.Mundo")));
						gp.getPlayer().teleport(loc);
					}
					
					glad.runTask(TaskId.BORDER);
					this.cancel();
					
				}
			}
		}.runTaskTimerAsynchronously(Main.getMain(), 0, 20);
		
	}
	
	private BukkitTask runSelectionCheckTask() {
		glad.setStatus(GladiatorStatus.SELECTING);
		if (glad.isRandomWarriors()) {
			for (GladPlayer p : glad.getPlayers()) {
				for (int o = 0; o < glad.getMaxClanMembers() - 1; o++) {
					
					int rand = new Random().nextInt(p.getClan().getOnlinePlayers().size());
					
					Player sp = p.getClan().getOnlinePlayers().get(rand);
					GladPlayer selected = Main.getPlayerManager().getPlayer(sp);
					
					while (p.getSelectedPlayers().contains(sp) || glad.getPlayers().contains(selected))
						rand = new Random().nextInt(p.getClan().getOnlinePlayers().size());
					
					sp = p.getClan().getOnlinePlayers().get(rand);
					selected = Main.getPlayerManager().getPlayer(sp);
					
					if (selected == null) selected = new GladPlayer(sp);
					
					p.getSelectedPlayers().add(selected.getPlayer());
					p.getPlayer().sendMessage("§bO jogador §f" + selected.getPlayer().getName() + " §bfoi selecionado");
				}
				
			}
			return null;
		}
		
		for (GladPlayer p : glad.getPlayers()) {
			p.setSelectionStatus(SelectionStatus.SELECTING);
			p.getPlayer().sendMessage(Main.tag
					+ "§bO evento gladiador será iniciado assim que todos os guerreiros estiverem sido selecionados pelos seus líderes, prepare-se para a batalha!");
			API.getApi().playSound(p.getPlayer(), Sound.ANVIL_USE);
		}
		
		return checkSelection = new BukkitRunnable() {
			int i = 0;
			
			@Override
			public void run() {
				
				for (GladPlayer p : glad.getPlayers()) {
					
					// Check se n§o § o lider e se est§ pronto
					if (p.getClan().getLider() != Jogador.check(p.getPlayer())) return;
					
					// Todos os players que ainda est§o selecionando
					if (p.getSelectionStatus() == SelectionStatus.SELECTING) {
						
						SelectMembersMenu menu = new SelectMembersMenu();
						menu.createInventory();
						
						if (!p.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("§8§lSelecionar Guerreiros"))
							Bukkit.getScheduler().runTask(Main.getMain(), () -> menu.openInventory(p.getPlayer()));
						
						// Se j§ se passaram 5 segundos
						if (i < 5) i++;
						else {
							
							// Clear chat
							for (int j = 0; j < 200; j++) p.getPlayer().sendMessage("");
							
							// Send select message
							p.getPlayer().sendMessage("§e§l§m---" + Main.tag + "§e§l§m---");
							p.getPlayer().sendMessage("§3Você se propos a desafiar todos à uma batalha mortal");
							p.getPlayer().sendMessage("§bAgora, escolha aqueles que lutarão ao seu lado por " + p.getClan().getTagClan());
							p.getPlayer().sendMessage("§6§l§m-------------------------");
							
							// Reset timer
							i = 0;
						}
					}
				}
				
			}
			
		}.runTaskTimerAsynchronously(Main.getMain(), 0, 20);
	}
	
	private BukkitTask runStartBattleTask() {
		glad.setStatus(GladiatorStatus.IN_BATTLE);
		glad.setTimeToStart(10);
		
		if (glad.getClans().size() <= glad.getClanAmountToDeathMatch()) {
			for (GladPlayer gp : glad.getPlayers())
				gp.getPlayer().sendMessage(Main.tag + "§bPara que a batalha seja mais forvorosa, vamos direto ao ponto, o §9§lDEATH MATCH");
			return runDeathMatchTask();
		}
		
		glad.getPlayers().forEach(gp -> {
			Player p = gp.getPlayer();
			int rand = new Random().nextInt(glad.getSpawnPoints().size());
			p.teleport(unserialize(glad.getSpawnPoints().get(rand)));
			p.sendMessage(Main.tag);
			p.sendMessage("§aTodos possúem §f" + glad.getTimeToAgroup() + " §asegundos para se juntar ao seu clã.");
			API.getApi().playSound(p, Sound.ANVIL_BREAK);
		});
		
		return startBattle = new BukkitRunnable() {
			@Override
			public void run() {
				
				if (glad.getTimeToStart() >= 1) {
					for (GladPlayer p : glad.getPlayers()) {
						API.getApi().sendTitle("§b§lIniciando batalha em", "§3§l" + glad.getTimeToStart() + "§b§l segundos", p.getPlayer());
						API.getApi().playSound(p.getPlayer(), Sound.CLICK);
					}
					glad.setTimeToStart(glad.getTimeToStart() - (1));
				} else {
					
					for (GladPlayer p : glad.getPlayers()) {
						
						// Clear chat
						for (int j = 0; j < 100; j++) p.getPlayer().sendMessage("");
						
						glad.getPlayers().forEach(all -> {
							for (String msg : Messages.started) all.getPlayer().sendMessage(PlaceHolder.replace(all.getPlayer(), msg));
						});
						
						API.getApi().playSound(p.getPlayer(), Sound.EXPLODE);
						
						Bukkit.getScheduler().runTask(Main.getMain(), () -> p.getPlayer().setGameMode(GameMode.SURVIVAL));
						glad.setRunningTask(runBorderUpdateTask());
					}
					
					glad.getWorld().setPVP(true);
					glad.setStartMilis(System.currentTimeMillis());
					
					for (GladPlayer gp : glad.getPlayers()) {
						
						Scoreboard score = ScoreManager.getManager().getScoreBoard(gp.getPlayer());
						
						if (score != null) {
							score.create();
							score.update();
							continue;
						}
						
						score = new Scoreboard(gp.getPlayer(), glad.getScoreboard().get(0));
						score.create();
						score.update();
					}
					
					glad.runTask(TaskId.BORDER);
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously(Main.getMain(), glad.getTimeToAgroup() * 20, 20);
	}
	
	private BukkitTask runBorderUpdateTask() {
		System.out.println("Task called");
		
		List<Player> players = new ArrayList<>();
		if (glad.getTaskRunning() == null || glad.getTaskRunning() != getDeathMatch() || glad.getTaskRunning() != getStartBattle())
			return null;
		
		if (glad.DmBorder()) return null;
		else if (glad.BattleBorder()) return null;
		else {
			players = new ArrayList<>();
			for (GladPlayer gps : glad.getPlayers()) {
				
				if (players.contains(gps.getPlayer())) continue;
				
				players.add(gps.getPlayer());
				gps.getPlayer().sendMessage(PlaceHolder.replace(gps.getPlayer(),
						Messages.borderwarn.replace("%timetostartborder%", glad.getTimeToStartBorder() + "")));
			}
		}
		
		BorderManager bm = new BorderManager(glad.getWorld(), players, glad);
		if (glad.getTaskRunning() == getDeathMatch()) bm.setWorld(glad.getDeathMatchWorld());
		glad.setBorder(bm);
		
		return new BukkitRunnable() {
			@Override
			public void run() {
				
				List<Player> players = new ArrayList<>();
				for (GladPlayer gps : glad.getPlayers()) {
					players.add(gps.getPlayer());
					double distance = gps.getPlayer().getLocation().distanceSquared(bm.getCenter());
					if (distance > bm.getBorder().getSize() / 2)
						gps.getPlayer().setVelocity(gps.getPlayer().getLocation().getDirection().multiply(0.5));
				}
				bm.setPlayers(players);
				
				float reduction = ((float) (glad.getStartSize() - glad.getFinalSize()) / (glad.getAllTime() * 20));
				String str = glad.getTaskRunning() == getStartBattle() ? "Arena" : "DeathMatch";
				bm.update(reduction, str);
				
				if (bm.getBorder().getSize() <= glad.getFinalSize()) {
					bm.getPlayers().forEach(p -> p.sendMessage(
							Main.tag + "§3A borda chegou ao seu tamanho mínimo, uma área de §f" + reduction * reduction + " §3blocos"));
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously(Main.getMain(), glad.getTimeToStartBorder() * 20, 1);
	}
	
	private void sendWarn(Player p) {
		for (String msg : glad.getWarns()) { p.sendMessage(replace(p, msg)); }
		API.getApi().sendTitle(replace(p, glad.getTitle()), replace(p, glad.getSubtitle()), p);
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 10f, 10f);
		p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 10.1f, 10f);
		p.playSound(p.getLocation(), Sound.SHEEP_SHEAR, 101f, 10f);
		
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
	
	public static BukkitTask getCheckSelection() { return checkSelection; }
	
	public static void setCheckSelection(BukkitTask checkSelection) { Tasks.checkSelection = checkSelection; }
	
	public static BukkitTask getStartBattle() { return startBattle; }
	
	public static void setStartBattle(BukkitTask startBattle) { Tasks.startBattle = startBattle; }
	
	public static void setSendWarns(BukkitTask sendWarns) { Tasks.sendWarns = sendWarns; }
	
	public static BukkitTask getDeathMatch() { return deathMatch; }
	
	public static BukkitTask setDeathMatch(BukkitTask deathMatch) {
		Tasks.deathMatch = deathMatch;
		return deathMatch;
	}
	
	public static BukkitTask getWarnsTask() { return sendWarns; }
	
	public static BukkitTask getBorderDecrease() { return borderDecrease; }
	
	public static void setBorderDecrease(BukkitTask borderDecrease) { Tasks.borderDecrease = borderDecrease; }
	
}
