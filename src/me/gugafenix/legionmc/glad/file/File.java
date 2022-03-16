/*
 * 
 */
package me.gugafenix.legionmc.glad.file;

import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.gugafenix.legionmc.glad.main.Main;

public class File {
	
	private FileConfiguration config;
	private java.io.File file;
	private String name;
	
	public File(String name, String path) {
		this.name = name;
		this.file = new java.io.File(path.replace(name, ""), name);
		
		if (!file.exists()) {
			
			try {
				
				file.createNewFile();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
		}
		
		config = YamlConfiguration.loadConfiguration(file);
		if(Main.getFileManager().getFiles().add(this)) Main.getMain().log(Main.tag.replace("0", "8") + "§bArquivo preset §f" + file.getName() + " §b carregado com exito!");;
	}
	
	public FileConfiguration getConfig() { return config; }
	
	public java.io.File getFile() { return file; }
	
	public String getName() { return name; }
	
	public void setName(String name) { this.name = name; }
	
	public FileConfiguration save() {
		
		try {
			
			config.save(file);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		return this.getConfig();
	}
	
}
