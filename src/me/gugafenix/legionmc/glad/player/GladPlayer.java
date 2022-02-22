package me.gugafenix.legionmc.glad.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.HClan.Objects.Clan;
import me.HClan.Objects.Jogador;
import me.gugafenix.legionmc.glad.main.Main;

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
	
}
