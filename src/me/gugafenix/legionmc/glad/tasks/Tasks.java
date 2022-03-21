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
import org.bukkit.util.Vector;

import me.HClan.Objects.Clan;
import me.HClan.Objects.DiscordClan;
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
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.server.v1_8_R3.WorldBorder;

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
			int i = glad.getTimeBelowWarns();

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
					if (gp != null) {
						p.sendMessage(Main.tag + "§eO gladiador iniciará em §c" + glad.getTimeToStart() + " §esegundos");
						p.sendMessage(Main.tag + "§ePense bem se deseja lutar até a morte pois, depois que "
								+ "a arena for fechada para batalha, você não poderá mas desistir, "
								+ "por enquanto, para sair do gladiador use §c/glad sair");
					}

					p.sendMessage("");
					if (i == 1) {
						sendWarn(p);
						p.sendMessage("§5§lClãs no gladiador");
						List<Clan> clans = glad.getClans();
						if (!clans.isEmpty()) {
							StringBuilder sb = new StringBuilder(clans.get(0).getTagClan() + ", ");
							for (int i = 1; i < clans.size(); i++) {

								if (i == clans.size()) {
									sb.append("§f, " + clans.get(clans.size()).getTagClan() + "§f.");
									break;
								}
								if (glad.getClans().size() > 1) sb.append(glad.getClans().get(0).getTagClan() + "§f, ");
							}

							p.sendMessage(sb.toString());
						} else p.sendMessage("§fNenhum clã no gladiador");

						p.sendMessage("");
						i = glad.getTimeBelowWarns();

						DiscordClan dc = Jogador.check(p).getClan().getDiscordClan();
						if (dc != null) {
							TextChannel channel = dc.getChannelAvisos();

							if (channel == null) continue;

							dc.sendMessage(channel, dc.getRole().getAsMention() + " **O evento gladiador será iniciado no servidor em "
									+ glad.getTimeToStart() + " segundos**");
						}
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
						API.getApi().sendTitle("§bO death match iniciou ", "§3§lLUTE!" + i, gp.getPlayer());
						gp.getPlayer().teleport(unserialize(glad.getPreset().getConfig().getString("DeathMatch.Spawn")));
					}

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
							p.getPlayer().sendMessage("§6§l§m----------------------------------------------------------");

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

						p.getPlayer().sendMessage("§e§l§m----§6§l§m--------" + Main.tag + "§6§l§m --------§e§l§m---");
						p.getPlayer().sendMessage("§3Seu clã desafiou todos os outros à uma batalha");
						p.getPlayer().sendMessage("§bLute, resista, ajude seus aliados e honre " + p.getClan().getTagClan());
						p.getPlayer().sendMessage("§6§l§m--------------------------------------");

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
		List<Player> players = new ArrayList<>();

		for (GladPlayer gps : glad.getPlayers()) {
			players.add(gps.getPlayer());
			gps.getPlayer()
					.sendMessage(Main.tag + "§5Em §f" + glad.getTimeToStartBorder() + " §5segundos a borda começará a ser reduzida.");
		}

		BorderManager bm = new BorderManager(glad.getWorld(), players, glad);
		glad.setBorder(bm);

		return new BukkitRunnable() {
			@Override
			public void run() {

				List<Player> players = new ArrayList<>();
				for (GladPlayer gps : glad.getPlayers()) {
					players.add(gps.getPlayer());
					double distance = gps.getPlayer().getLocation().distanceSquared(bm.getCenter());
					if(distance > bm.getBorder().getSize() / 2) gps.getPlayer().setVelocity(gps.getPlayer().getLocation().getDirection().multiply(0.5));
				}
				bm.setPlayers(players);

				float reduction = ((float) (glad.getStartSize() - glad.getFinalSize()) / (glad.getAllTime() * 20));
				bm.update(reduction);

				if (bm.getBorder().getSize() <= glad.getFinalSize()) {
					bm.getPlayers()
							.forEach(p -> p.sendMessage(Main.tag + "§3A borda chegou ao seu tamanho mínimo, uma área de §f100 §3blocos"));
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
