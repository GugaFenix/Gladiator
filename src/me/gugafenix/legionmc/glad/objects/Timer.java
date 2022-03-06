package me.gugafenix.legionmc.glad.objects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.gugafenix.legionmc.glad.file.File;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.tasks.Tasks.TaskId;

public class Timer {
	private enum TimeType {
		DAYS, HOURS, MINUTES, SECONDS;
	}
	
	private File file;
	private int time;
	private BukkitTask task;
	private static Timer timer;
	
	public Timer() {
		timer = this;
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if (file == null) {
					Main.getMain().log("",
							Main.tag + "§cO preset informado para ser iniciado automaticamente não existe ou não foi definido", "");
				} else {
					
					if (!Gladiator.hasGladRunning()) {
						new Gladiator(file).runTask(TaskId.SEND_WARNS);
					} else {
						Main.getMain().log("", Main.tag + "§cO início de um gladiador utilizando o preset §f" + file.getName()
								+ " §cfoi cancelado por já haver um gladiador em andamento", "");
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.getMain(), getTime(getAutoStartFile()), getTime(getAutoStartFile()));
	}
	
	private int getTime(File file) {
		TimeType timeType = TimeType.DAYS;
		
		String str = file.getConfig().getString("SpacoEntreGladiadores");
		
		if (str.endsWith("d")) timeType = TimeType.DAYS;
		else if (str.endsWith("s")) timeType = TimeType.SECONDS;
		else if (str.endsWith("h")) timeType = TimeType.HOURS;
		else if (str.endsWith("m")) timeType = TimeType.MINUTES;
		else timeType = TimeType.SECONDS;
		
		time = Integer.valueOf(str.replace(str.charAt(str.length() - 1) + "", ""));
		
		switch (timeType) {
		case DAYS:
			return time * 20 * 60 * 60 * 24;
		case HOURS:
			return time * 20 * 60 * 60;
		case MINUTES:
			return time * 20 * 60;
		case SECONDS:
			return time * 20;
		default:
			return time * 0;
		}
	}
	
	private File getAutoStartFile() {
		for (File f : Main.getFileManager().getFiles()) {
			FileConfiguration config = f.getConfig();
			Boolean autoStart = config.getBoolean("AutoStart");
			if (autoStart) return f;
		}
		return null;
	}
	
	public File getFile() { return file; }
	
	public void setFile(File file) { this.file = file; }
	
	public void reload() {
		task.cancel();
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if (file == null) {
					Main.getMain().log("",
							Main.tag + "§cO preset informado para ser iniciado automaticamente não existe ou não foi definido");
				} else {
					
					if (!Gladiator.hasGladRunning()) {
						new Gladiator(file).runTask(TaskId.SEND_WARNS);
					} else {
						Main.getMain().log("", Main.tag + "§cO início de um gladiador utilizando o preset §f" + file.getName()
								+ " §cfoi cancelado por já haver um gladiador em andamento", "");
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.getMain(), getTime(getAutoStartFile()), getTime(getAutoStartFile()));
	}
	
	public static Timer getTimer() { return timer; }
	
}
