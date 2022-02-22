package me.gugafenix.legionmc.glad.tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.HClan.Objects.Jogador;
import me.clip.placeholderapi.PlaceholderAPI;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.objects.Gladiator.GladiatorStatus;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.player.GladPlayer.SelectionStatus;
import me.gugafenix.legionmc.glad.utils.API;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Tasks {
	private Gladiator glad;
	private BukkitTask sendWarns, checkSelection, startBattle;
	private TaskId id;
	
	public enum TaskId {
		SEND_WARNS, CHECK_SELECTION, START_BATTLE;
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
					if(glad.getAllClans().size() <= 1) {
						Bukkit.getOnlinePlayers().forEach(all -> {
							all.sendMessage(Main.tag + "§9O gladiador foi cancelado por falta de clãs");
							glad.cancel();
						});
					}
					
					
					runSelectionCheckTask();
					glad.setStatus(GladiatorStatus.SELECTING);
					glad.setTimeToStart(10);
					this.cancel();
					return;
				}
				
				/*
				 * Send Warns
				 */
				
				// Todos os players
				for (Player p : Bukkit.getOnlinePlayers()) {
					
					// Check se o player já está no gladiador
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
			p.getPlayer().sendMessage(Main.tag + "§bO evento gladiador será iniciado assim que todos os guerreiros estiverem selecionados, prepare-se para a batalha!");
			API.getApi().playSound(p.getPlayer(), Sound.ANVIL_USE);
		});
		
		return checkSelection = new BukkitRunnable() {
			int i = 0;
			
			@Override
			public void run() {
				
				glad.getPlayers().forEach(p -> {
					
					// Check se não é o lider e se está pronto
					if (p.getClan().getLider() != Jogador.check(p.getPlayer())) return;
					
					// Todos selecionados
					if (!hasPlayerSelecting()) {
						this.cancel();
						runStartBattleTask();
						return;
					}
					
					// Todos os players que ainda estão selecionando
					if (p.getSelectionStatus() == SelectionStatus.SELECTING) {
						// Se já se passaram 5 segundos
						if (i < 5) i++;
						else {
							
							// Clear chat
							for (int j = 0; j < 200; j++) p.getPlayer().sendMessage("");
							
							// Send select message
							p.getPlayer().sendMessage("§e§l§m----§6§l§m--------" + Main.tag + "§6§l§m --------§e§l§m---");
							p.getPlayer().sendMessage("§3Você se propos a desafiar outros clãs em uma batalha mortal");
							p.getPlayer().sendMessage("");
							p.getPlayer().sendMessage("§3Agora, escolha aqueles que lutarão ao seu lado por " + p.getClan().getTagClan());
							TextComponent t = new TextComponent("§6§l§m-----------§8§l[§6§lSelecionar Guerreiros§8§l]§6§l§m-------------");
							t.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/gladiador selecionarguerreiros"));
							t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder("§cClique aqui para selecionar os membros do seu clã que participarão do evento")
											.create()));
							p.getPlayer().spigot().sendMessage(t);
							
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
						p.getPlayer().teleport(glad.getWorld().getSpawnLocation());
						API.getApi().sendTitle("§b§lBatalha iniciada", "§3§lLUTE!", p.getPlayer());
						API.getApi().playSound(p.getPlayer(), Sound.ENDERDRAGON_DEATH);
						Bukkit.getScheduler().runTask
						
						(Main.getMain(), () -> p.getPlayer().setGameMode(GameMode.SURVIVAL));
					}
					
					glad.getWorld().setPVP(true);
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously(Main.getMain(), 0, 20);
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
	
	private boolean hasPlayerSelecting() {
		for (GladPlayer p : glad.getPlayers()) {
			if (p.getSelectionStatus() == SelectionStatus.SELECTING) { return true; }
			return false;
		}
		return false;
	}
	
}
