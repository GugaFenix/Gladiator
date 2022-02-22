package me.gugafenix.legionmc.glad.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import me.gugafenix.legionmc.glad.main.Main;

public class FileManager {
	private List<File> files;
	
	public FileManager() { files = new ArrayList<>(); }
	
	public void loadConfigs() {
		Main.getMain().log("", Main.tag.replace("0", "8") + "§3Carregamento dos presets iniciado...");
		long milis = System.currentTimeMillis();
		/*
		 * Creating the presets folder
		 */
		java.io.File presets = new java.io.File(Main.getMain().getDataFolder() + "/Presets");
		if (!presets.exists()) presets.mkdir();
		/*
		 * Loading the presets
		 */
		saveResource("Default.yml", Main.getMain().getDataFolder() + "/Presets/", "Default.yml", true);
		
		for (java.io.File file : presets.listFiles()) new File(file.getName(), file.getPath());
		Main.getMain().log(
				Main.tag.replace("0", "8") + "§aCarregamento dos presets finalizado em §d" + (System.currentTimeMillis() - milis) + "ms§a!",
				"");
	}
	
	public File getFile(String name) {
		ListIterator<File> it = files.listIterator();
		File file = null;
		while (it.hasNext()) if ((file = it.next()).getName().contentEquals(name)) return file;
		return null;
	}
	
	public File getFile(java.io.File file2) {
		ListIterator<File> it = files.listIterator();
		File file = null;
		
		while (it.hasNext()) if ((file = it.next()).getFile().getName().contentEquals(file2.getName())) return file;
		return null;
	}
	
	public List<File> getFiles() { return files; }
	
	public void setFiles(List<File> files) { this.files = files; }
	
	public void saveResource(String resourcePath, String outPath, String outName,boolean replace) {
		
		if (resourcePath == null || resourcePath.equals("")) {
			
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
			
		}
		
		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = Main.getMain().getResource(resourcePath);
		
		if (in == null) {
			
			throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in ");
			
		}
		
		java.io.File outFile = new java.io.File(outPath, outName);
		int lastIndex = resourcePath.lastIndexOf('/');
		java.io.File outDir = new java.io.File(outPath, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
		
		if (!outDir.exists()) { outDir.mkdirs(); }
		
		try {
			
			if (!outFile.exists() || replace) {
				
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				
				while ((len = in.read(buf)) > 0) { out.write(buf, 0, len); }
				
				out.close();
				in.close();
				
			} else {
				
				Main.getMain().getLogger().log(Level.WARNING,
						"Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
				
			}
			
		} catch (IOException ex) {
			
			Main.getMain().getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
			
		}
		
	}
	
}
