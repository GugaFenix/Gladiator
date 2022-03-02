package me.gugafenix.legionmc.glad.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import me.HClan.Objects.Clan;
import me.gugafenix.legionmc.glad.cabin.Cabin;
import me.gugafenix.legionmc.glad.file.File;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.player.GladPlayer.SelectionStatus;
import me.gugafenix.legionmc.glad.tasks.Tasks;
import me.gugafenix.legionmc.glad.tasks.Tasks.TaskId;

public class Gladiator {
	private World world, DeathMatchWorld;
	private List<Clan> clans;
	private WorldBorder border;
	private File preset;
	private int borderReduction, clanAmountToDeathMatch, timeToDecreaseBorder, timeToStart, PlayerAmount;
	private List<String> warns, scoreboard;
	private int timeBelowWarns;
	private String title, subtitle;
	private static boolean hasGladRunning;
	private static Gladiator gladRunning;
	private List<GladPlayer> players;
	private GladiatorStatus status;
	private int maxClanMembers, minClanMembers, minClanKDR, minClanMoney;
	private List<String> spawnPoints;
	private long startMilis;
	private Cabin cabin;
	private Clan winner;
	
	public static enum GladiatorStatus {
		WARNING, IN_BATTLE, SELECTING, LOCKED, DEATH_MATCH;
	};
	
	@SuppressWarnings("unchecked")
	public Gladiator(File presetFile) {
		this.winner = null;
		this.clans = new ArrayList<>();
		this.players = new ArrayList<>();
		this.status = GladiatorStatus.WARNING;
		setHasGladRunning(true);
		setGladRunning(this);
		/*
		 * Configuration
		 */
		this.preset = presetFile;
		FileConfiguration config = preset.getConfig();
		/*
		 * Event
		 */
		this.startMilis = 0;
		this.world = Bukkit.getWorld(config.getString("Evento.Mundo"));
		this.timeToStart = config.getInt("Evento.TempoParaIniciar");
		this.timeBelowWarns = config.getInt("Evento.TempoEntreAvisos");
		this.spawnPoints = (List<String>) config.getList("Spawns");
		/*
		 * Border
		 */
		this.border = world.getWorldBorder();
		border.setCenter(
				new Location(world, config.getInt("Borda.Centro.X"), config.getInt("Borda.Centro.Y"), config.getInt("Borda.Centro.Z")));
		border.setSize(config.getInt("Borda.Tamanho.Inicio"));
		border.setDamageAmount(config.getInt("Borda.Dano"));
		this.timeToDecreaseBorder = config.getInt("Borda.Reducao.Tempo");
		this.borderReduction = config.getInt("Borda.Tamanho.Reducao");
		/*
		 * DeathMatch
		 */
		this.clanAmountToDeathMatch = config.getInt("DeathMatch.QuantidadeDeClãs");
		this.DeathMatchWorld = Bukkit.getWorld(config.getString("DeathMatch.Mundo"));
		/*
		 * Messages
		 */
		this.warns = config.getStringList("Aviso");
		this.scoreboard = config.getStringList("Scoreboard");
		this.title = config.getString("Titulo");
		this.subtitle = config.getString("Subtitulo");
		/*
		 * Clan configs
		 */
		this.maxClanMembers = config.getInt("Clan.MaxMembros");
		this.minClanKDR = config.getInt("Clan.MinKDR");
		this.minClanMoney = config.getInt("Clan.MinDinheiro");
		this.minClanMembers = config.getInt("Clan.MinMembros");
		
		/*
		 * Camarote
		 */
		this.cabin = new Cabin(this);
	}
	
	private BukkitTask runningTask;
	
	public void runTask(TaskId id) { this.runningTask = new Tasks(id, this).start(); }
	
	public BukkitTask getTaskRunning() { return runningTask; }
	
	public static boolean hasGladRunning() { return hasGladRunning; }
	
	public void setPlayers(List<GladPlayer> players) { this.players = players; }
	
	public int getTimeBelowWarns() { return timeBelowWarns; }
	
	public void setTimeBelowWarns(int timeBelowWarns) { this.timeBelowWarns = timeBelowWarns; }
	
	public String getTitle() { return title; }
	
	public void setTitle(String title) { this.title = title; }
	
	public String getSubtitle() { return subtitle; }
	
	public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
	
	public World getWorld() { return world; }
	
	public void setWorld(World world) { this.world = world; }
	
	public int getPlayerAmount() { return PlayerAmount; }
	
	public void setPlayerAmount(int playerAmount) { PlayerAmount = playerAmount; }
	
	public List<GladPlayer> getPlayers() { return players; }
	
	public WorldBorder getBorder() { return border; }
	
	public void setBorder(WorldBorder border) { this.border = border; }
	
	public File getPreset() { return preset; }
	
	public void setPreset(File preset) { this.preset = preset; }
	
	public int getTimeToDecreaseBorder() { return timeToDecreaseBorder; }
	
	public void setTimeToDecreaseBorder(int timeToDecreaseBorder) { this.timeToDecreaseBorder = timeToDecreaseBorder; }
	
	public int getBorderReduction() { return borderReduction; }
	
	public void setBorderReduction(int borderReduction) { this.borderReduction = borderReduction; }
	
	public int getClanAmountToDeathMatch() { return clanAmountToDeathMatch; }
	
	public void setClanAmountToDeathMatch(int clanAmountToDeathMatch) { this.clanAmountToDeathMatch = clanAmountToDeathMatch; }
	
	public World getDeathMatchWorld() { return DeathMatchWorld; }
	
	public void setDeathMatchWorld(World deathMatchWorld) { DeathMatchWorld = deathMatchWorld; }
	
	public List<String> getWarns() { return warns; }
	
	public void setWarns(List<String> warns) { this.warns = warns; }
	
	public int getTimeToStart() { return timeToStart; }
	
	public void setTimeToStart(int timeToStart) { this.timeToStart = timeToStart; }
	
	public List<String> getScoreboard() { return scoreboard; }
	
	public void setScoreboard(List<String> scoreboard) { this.scoreboard = scoreboard; }
	
	public static boolean HasGladRunning() { return hasGladRunning; }
	
	public static void setHasGladRunning(boolean hasGladRunning) { Gladiator.hasGladRunning = hasGladRunning; }
	
	public static Gladiator getGladRunning() { return gladRunning; }
	
	public static void setGladRunning(Gladiator gladRunning) { Gladiator.gladRunning = gladRunning; }
	
	public GladiatorStatus getStatus() { return status; }
	
	public void setStatus(GladiatorStatus status) { this.status = status; }
	
	public int getMaxClanMembers() { return maxClanMembers; }
	
	public void setMaxClanMembers(int maxClanMembers) { this.maxClanMembers = maxClanMembers; }
	
	public int getMinClanMembers() { return minClanMembers; }
	
	public void setMinClanMembers(int minClanMembers) { this.minClanMembers = minClanMembers; }
	
	public int getMinClanKDR() { return minClanKDR; }
	
	public void setMinClanKDR(int minClanKDR) { this.minClanKDR = minClanKDR; }
	
	public int getMinClanMoney() { return minClanMoney; }
	
	public void setMinClanMoney(int minClanMoney) { this.minClanMoney = minClanMoney; }
	
	public List<String> getSpawnPoints() { return spawnPoints; }
	
	public void setSpawnPoints(List<String> spawnPoints) { this.spawnPoints = spawnPoints; }
	
	public void cancel() {
		this.runningTask.cancel();
		
		if (!getPlayers().isEmpty() && this.getClans().size() != 0) {
			
			getPlayers().forEach(p -> {
				getPlayers().remove(p);
				p.getPlayer().teleport(Bukkit.getServer().getWorld(Bukkit.getWorlds().get(0).getName()).getSpawnLocation());
				p.setInGladiator(false);
				p.setSelectedPlayers(null);
				p.setSelectionStatus(SelectionStatus.NO_STATUS);
				Main.getPlayerManager().getPlayers().remove(p);
				getPlayers().remove(p);
			});
		}
		
		gladRunning = null;
		hasGladRunning = false;
	}
	
	public void updateInfoFromAll() { this.getPlayers().forEach(all -> all.updateInfos()); }
	
	public List<Clan> getClans() { return clans; }
	
	public long getStartMilis() { return startMilis; }
	
	public void setStartMilis(long startMilis) { this.startMilis = startMilis; }
	
	public Cabin getCabin() { return cabin; }
	
	public void setCabin(Cabin cabin) { this.cabin = cabin; }

	public Clan getWinner() { return winner; }

	public void setWinner(Clan winner) { this.winner = winner; }
}
