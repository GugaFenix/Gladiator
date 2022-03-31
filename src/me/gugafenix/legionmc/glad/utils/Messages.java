package me.gugafenix.legionmc.glad.utils;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.gugafenix.legionmc.glad.main.Main;

public class Messages {
	
	private static FileConfiguration config = Main.getMain().getConfig();
	public static String permission = toString("NoPermission");
	public static List<String> klanKilled = getMessage("ClanKilled");
	public static List<String> victory = getMessage("Victory");
	public static String print = toString("TimeToPrint");
	public static String join = toString("Join");
	public static String cancelled = toString("GladCancelled");
	public static List<String> started = getMessage("GladStarted");
	public static String borderwarn = toString("BorderWarn");
	public static String killed = toString("Killed");
	public static String suicide = toString("Suicide");
	public static String dm = toString("DeathMatch");
	public static List<String> beforestart = getMessage("BeforeStart");
	
	private static List<String> getMessage(String string) {
		List<String> list = config.getStringList("Messages." + string);
		if (list == null || list.isEmpty()) return null;
		return list;
	}
	
	private static String toString(String string) {
		List<String> msg = getMessage(string);
		return msg == null ? config.getString(string) : msg.get(0);
	}
}
