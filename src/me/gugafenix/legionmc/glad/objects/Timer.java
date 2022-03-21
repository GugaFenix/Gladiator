/*
 * 
 */
package me.gugafenix.legionmc.glad.objects;

import java.util.ListIterator;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.gugafenix.legionmc.glad.file.File;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.tasks.Tasks.TaskId;

public class Timer {
	
	private Gladiator gladRunning;
	private BukkitTask runnable;
	public static Timer timer;
	
	public Timer() { timer = this; }
	
	private File getAutoStartFile() {
		for (File file : Main.getFileManager().getFiles()) {
			ListIterator<File> it = Main.getFileManager().getFiles().listIterator();
			while (it.hasNext()) if ((file = it.next()).getConfig().getBoolean("AutoStart")) return file;
		}
		return null;
	}
	
	private int getTime() {
		String str = getAutoStartFile().getConfig().getString("AutoStartSpace");
		int space = Integer.valueOf(str.replace(getTimeType() + "", ""));
		
		switch (getTimeType()) {
		case 's':
			return space * 20;
		case 'h':
			return space * 20 * 60 * 60;
		case 'm':
			return space * 20 * 60;
		case 'd':
			return space * 20 * 60 * 60 * 24;
		default:
			return 7 * 20 * 60 * 60 * 24;
		}
	}
	
	private char getTimeType() {
		String str = getAutoStartFile().getConfig().getString("AutoStartSpace");
		return str.toCharArray()[str.length() - 1];
	}
	
	public void start() {
		
		if (getAutoStartFile() == null) {
			
			Main.getMain().log("", Main.tag + "§cNenhum preset definido para ser auto-iniciado", "");
			return;
			
		}
		
		if (this.runnable != null) this.runnable.cancel();
		
		this.runnable = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if (Gladiator.hasGladRunning()) {
					Main.getMain().log("", Main.tag
							+ "§cO gladiador programado para ser auto-iniciado não pode realizar tal ação por já haver um gladiador em andamento.",
							"§6Para reiniciar o timer, basta recarregar o plugin", "");
					this.cancel();
					return;
				}
				
				gladRunning = new Gladiator(getAutoStartFile());
				gladRunning.runTask(TaskId.SEND_WARNS);
			}
		}.runTaskTimerAsynchronously(Main.getMain(), getTime(), getTime());
		
	}
	
	public Gladiator getGladRunning() { return gladRunning; }
	
	public static Timer getTimer() { return timer; }
	
	public static void setTimer(Timer timer) { Timer.timer = timer; }
	
}
