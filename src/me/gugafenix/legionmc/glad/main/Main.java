package me.gugafenix.legionmc.glad.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.gugafenix.legionmc.glad.commands.Commander;
import me.gugafenix.legionmc.glad.events.PlayerRemoveEvent;
import me.gugafenix.legionmc.glad.file.FileManager;
import me.gugafenix.legionmc.glad.invs.events.onSelectPlayerEvent;
import me.gugafenix.legionmc.glad.player.GladPlayerManager;
import me.gugafenix.legionmc.glad.spawnselection.SpawnSelectManager;
import me.gugafenix.legionmc.glad.spawnselection.events.BugOnSelectEvent;
import me.gugafenix.legionmc.glad.spawnselection.events.PlayerBreakBlock;
import me.gugafenix.legionmc.glad.spawnselection.events.PlayerChatEvent;
import me.gugafenix.legionmc.glad.spawnselection.events.PlayerPlaceBlock;
import me.gugafenix.legionmc.glad.utils.API;
import me.gugafenix.legionmc.glad.utils.PlaceHolder;

public class Main extends JavaPlugin {
	
	private static Main main;
	public static String tag = "§0§l[§6§lGladiador§0§l]";
	private static FileManager fileManager;
	private static API api;
	private static GladPlayerManager playerManager;
	private static SpawnSelectManager spawnsManager;
	private static List<Listener> listeners;
	
	@Override
	public void onEnable() {
		main = this;
		
		listeners = new ArrayList<>();
		listeners.add(new PlayerRemoveEvent());
		listeners.add(new PlayerBreakBlock());
		listeners.add(new PlayerChatEvent());
		listeners.add(new PlayerPlaceBlock());
		listeners.add(new onSelectPlayerEvent());
		listeners.add(new BugOnSelectEvent());
		registerEvents();
		
		api = new API();
		new PlaceHolder().register();
		fileManager = new FileManager();
		fileManager.loadConfigs();
		playerManager = new GladPlayerManager();
		spawnsManager = new SpawnSelectManager();
		saveDefaultConfig();
		registerMainCommand();
	}
	
	private void registerMainCommand() {
		((CraftServer) Bukkit.getServer()).getCommandMap().register("Gladiator",
				new Commander().setAliases(Arrays.asList(new String[] { "glad", "gladiator" })));
	}
	
	public void log(String... strings) {
		if (strings.length < 2) {
			Bukkit.getConsoleSender().sendMessage(strings[0]);
			return;
		}
		for (String msg : strings) { Bukkit.getConsoleSender().sendMessage(msg); }
	}
	
	public void registerEvents() { listeners.forEach(l -> Bukkit.getPluginManager().registerEvents(l, main)); }
	
	public static GladPlayerManager getPlayerManager() { return playerManager; }
	
	public static API getApi() { return api; }
	
	public static FileManager getFileManager() { return fileManager; }
	
	public static Main getMain() { return main; }
	
	public static SpawnSelectManager getSpawnsManager() { return spawnsManager; }
	
}
